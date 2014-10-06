package org.dei.perla.rest.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.dei.perla.channel.ChannelFactory;
import org.dei.perla.channel.IORequestBuilderFactory;
import org.dei.perla.fpc.Attribute;
import org.dei.perla.fpc.Fpc;
import org.dei.perla.fpc.FpcFactory;
import org.dei.perla.fpc.base.BaseFpcFactory;
import org.dei.perla.fpc.descriptor.DeviceDescriptor;
import org.dei.perla.fpc.descriptor.DeviceDescriptorParseException;
import org.dei.perla.fpc.descriptor.DeviceDescriptorParser;
import org.dei.perla.fpc.descriptor.InvalidDeviceDescriptorException;
import org.dei.perla.fpc.descriptor.JaxbDeviceDescriptorParser;
import org.dei.perla.fpc.registry.Registry;
import org.dei.perla.fpc.registry.TreeRegistry;
import org.dei.perla.message.MapperFactory;

public class PerLaController {

	private Logger logger = Logger.getLogger(PerLaController.class);

	private AtomicInteger idGenerator = new AtomicInteger(0);

	private DeviceDescriptorParser parser;
	private FpcFactory factory;

	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Registry registry;
	private Collection<Fpc> fpcs;

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
			try {
				lock.writeLock().lock();
				registry = new TreeRegistry();
				fpcs = new ArrayList<>();
			} finally {
				lock.writeLock().unlock();
			}
			logger.info("PerLaController initialized successfully");

		} catch (Exception e) {
			throw new PerLaException("Error creating FPCFactory", e);
		}
	}

	public Fpc createFpc(InputStream descriptor) throws PerLaException {
		Fpc fpc = null;
		int id = idGenerator.addAndGet(1);

		try {
			DeviceDescriptor d = parser.parse(descriptor);
			fpc = factory.createFpc(d, id);
			registry.add(fpc);
			fpcs.add(fpc);
		} catch (DeviceDescriptorParseException e) {
			logger.error("Error parsing device descriptor", e);
		} catch (InvalidDeviceDescriptorException e) {
			logger.error("Error creating FPC", e);
		}

		logger.info("FPC '" + id + "' created and added to the register");
		return fpc;
	}

	public Collection<Fpc> getAllFpcs() {
		try {
			lock.readLock().lock();
			return fpcs;
		} finally {
			lock.readLock().unlock();
		}
	}

	public Collection<Fpc> getFpc(Collection<Attribute> with) {
		try {
			lock.readLock().lock();
			return registry.getFpc(with, Collections.emptyList());
		} finally {
			lock.readLock().unlock();
		}
	}

}
