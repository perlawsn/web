package org.dei.perla.rest.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.dei.perla.channel.ChannelFactory;
import org.dei.perla.channel.IORequestBuilderFactory;
import org.dei.perla.fpc.Attribute;
import org.dei.perla.fpc.Fpc;
import org.dei.perla.fpc.FpcFactory;
import org.dei.perla.fpc.Task;
import org.dei.perla.fpc.TaskHandler;
import org.dei.perla.fpc.base.BaseFpcFactory;
import org.dei.perla.fpc.descriptor.DeviceDescriptor;
import org.dei.perla.fpc.descriptor.DeviceDescriptorParseException;
import org.dei.perla.fpc.descriptor.DeviceDescriptorParser;
import org.dei.perla.fpc.descriptor.InvalidDeviceDescriptorException;
import org.dei.perla.fpc.descriptor.JaxbDeviceDescriptorParser;
import org.dei.perla.fpc.registry.Registry;
import org.dei.perla.fpc.registry.TreeRegistry;
import org.dei.perla.message.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;

public class PerLaController {

	private Logger logger = Logger.getLogger(PerLaController.class);

	@Autowired
	private MessageSendingOperations<String> msg;

	private AtomicInteger nodeIdCount = new AtomicInteger(0);
	private AtomicInteger taskIdCount = new AtomicInteger(0);

	private DeviceDescriptorParser parser;
	private FpcFactory factory;
	private Registry registry = new TreeRegistry();

	private Map<Integer, RestTask> taskMap = new ConcurrentHashMap<>();

	public PerLaController(List<String> packages, List<String> mapperFcts,
			List<String> channelFcts, List<String> requestBuilderFcts)
			throws PerLaException {
		parser = new JaxbDeviceDescriptorParser(packages);

		try {
			List<MapperFactory> mfList = new ArrayList<>();
			for (String s : mapperFcts) {
				Object c = Class.forName(s).newInstance();
				if (!(c instanceof MapperFactory)) {
					String msg = "Class '" + s + "' is not a MapperFactory";
					logger.error(msg);
					throw new PerLaException(msg);
				}
				mfList.add((MapperFactory) c);
			}

			List<ChannelFactory> cfList = new ArrayList<>();
			for (String s : channelFcts) {
				Object c = Class.forName(s).newInstance();
				if (!(c instanceof ChannelFactory)) {
					String msg = "Class '" + s + "' is not a ChannelFactory";
					logger.error(msg);
					throw new PerLaException(msg);
				}
				cfList.add((ChannelFactory) c);
			}

			List<IORequestBuilderFactory> rbfList = new ArrayList<>();
			for (String s : requestBuilderFcts) {
				Object c = Class.forName(s).newInstance();
				if (!(c instanceof IORequestBuilderFactory)) {
					String msg = "Class '" + s
							+ "' is not an IORequestBuilderFactory";
					logger.error(msg);
					throw new PerLaException(msg);
				}
				rbfList.add((IORequestBuilderFactory) c);
			}

			factory = new BaseFpcFactory(mfList, cfList, rbfList);
			logger.info("PerLaController initialized successfully");

		} catch (Exception e) {
			throw new PerLaException("Error creating FPCFactory", e);
		}
	}

	public Fpc createFpc(InputStream descriptor) throws PerLaException {
		Fpc fpc = null;
		int id = nodeIdCount.addAndGet(1);

		try {
			DeviceDescriptor d = parser.parse(descriptor);
			fpc = factory.createFpc(d, id);
			registry.add(fpc);
		} catch (DeviceDescriptorParseException e) {
			logger.error("Error parsing device descriptor", e);
		} catch (InvalidDeviceDescriptorException e) {
			logger.error("Error creating FPC", e);
		}

		logger.info("FPC '" + id + "' created and added to the register");
		return fpc;
	}

	public Fpc getFpc(int id) {
		return registry.get(id);
	}

	public Collection<Fpc> getAllFpcs() {
		return registry.getAll();
	}

	public Collection<Fpc> getFpcByAttribute(Collection<Attribute> with) {
		return registry.getByAttribute(with, Collections.emptyList());
	}

	public int queryPeriodic(Collection<Attribute> atts, long periodMs)
			throws PerLaException {
		Collection<Fpc> fpcs = registry.getByAttribute(atts,
				Collections.emptyList());

		if (fpcs.size() == 0) {
			throw new PerLaException(
					"Requested attributes are not available on any device");
		}

		int id = taskIdCount.addAndGet(1);
		TaskHandler h = new StompHandler(msg, id);
		Collection<Integer> fpcIds = new ArrayList<>();
		Collection<Task> tasks = new ArrayList<>();
		for (Fpc f : fpcs) {
			Task t = f.get(atts, periodMs, h);
			if (t == null) {
				continue;
			}
			fpcIds.add(f.getId());
			tasks.add(t);
		}

		if (tasks.size() == 0) {
			throw new PerLaException("No FPC can satisfy the requested query");
		}

		taskMap.put(id, new RestTask(id, atts, fpcIds, tasks));
		return id;
	}

	public RestTask getTask(int id) {
		return taskMap.get(id);
	}

	public Collection<RestTask> getAllTasks() {
		return taskMap.values();
	}

	public void stopTask(int id) {
		RestTask t = taskMap.remove(id);
		if (t == null) {
			return;
		}
		t.stop();
	}

}
