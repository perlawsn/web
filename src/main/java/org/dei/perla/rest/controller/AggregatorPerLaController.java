package org.dei.perla.rest.controller;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dei.perla.core.PerLaSystem;
import org.dei.perla.core.Plugin;
import org.dei.perla.core.engine.Executor;
import org.dei.perla.core.fpc.*;
import org.dei.perla.core.registry.DuplicateDeviceIDException;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.web.aggre.AggregatorConsumer;
import org.dei.perla.web.aggre.AggregatorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AggregatorPerLaController {

    private Logger logger = Logger.getLogger(AggregatorPerLaController.class);

    @Autowired
    private MessageSendingOperations<String> msg;

    // Id generators
    private int taskIdSeq = 0;

    private final ReadWriteLock l = new ReentrantReadWriteLock();
    
    private final AggregatorSystem system;
    private final Registry registry;

    private final Map<Integer, RestTask> taskMap = new ConcurrentHashMap<>();
    private AggregatorConsumer consumer;
    private Thread consumerThread;
    public AggregatorPerLaController(List<Plugin> plugins) throws PerLaException {
        system = new AggregatorSystem(plugins);
        registry = system.getRegistry();
        logger.info("PerLaController initialized successfully");
        consumer=system.getAggregatorConsumer();
        consumerThread = new Thread(consumer);
    }

    /**
     * Creates a new {@link Fpc}
     *
     * @param descriptor Device Descriptor used to configure the new {@link
     *                   Fpc}
     * @return
     * @throws PerLaException
     */
    public Fpc createFpc(InputStream fis) throws PerLaException {
        l.writeLock().lock();
        try {
            Fpc fpc = null;
               
                try {
					fpc = system.injectDescriptor(fis);
					 registry.add(fpc);
				} catch (FpcCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicateDeviceIDException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
               
            
            
            logger.debug("FPC created and added to the register");
            return fpc;
        } finally {
            l.writeLock().unlock();
        }
    }

    /**
     * Logs the exception and the error message, then throws an appropriate
     * PerLaException
     *
     * @param msg Error message
     * @param e   Cause of the error
     * @throws PerLaException
     */
    private void failAndThrow(String msg, Exception e) throws PerLaException {
        logger.error(msg, e);
        throw new PerLaException(msg, e);
    }

    /**
     * Retrieves a single FPC from the registry
     *
     * @param id FPC identifier
     * @return A reference to the FPC, or null if no FPC the specified id is
     * found
     */
    public Fpc getFpc(int id) {
        l.readLock().lock();
        try {
            return registry.get(id);
        } finally {
            l.readLock().unlock();
        }
    }

    /**
     * Returns all the registered FPCs
     *
     * @return
     */
    public Collection<Fpc> getAllFpcs() {
        l.readLock().lock();
        try {
            return registry.getAll();
        } finally {
            l.readLock().unlock();
        }
    }

    /**
     * Returns a collection of {@code Fpc}s that possess the desired attributes
     *
     * @param with List of requested data elements
     * @return
     */
    public Collection<Fpc> getFpcByAttribute(Collection<Attribute> with) {
        l.readLock().lock();
        try {
            return registry.get(with, Collections.emptyList());
        } finally {
            l.readLock().unlock();
        }
    }

    /**
     * Starts a new query
     *
     * @param request data to be sampled
     * @param periodMs Query period
     * @return {@link Task} object for controlling query execution
     * @throws PerLaException If the query could not be started, or if no {@link
     *                        FPC} possesses the required attributes.
     */
    public RestTask queryPeriodic(List<Attribute> request, long periodMs)
            throws PerLaException {
        l.writeLock().lock();
        try {
            Collection<Fpc> fpcs = registry.get(request,
                    Collections.emptyList());

            if (fpcs.size() == 0) {
                throw new PerLaException(
                        "Requested attributes are not available on any device");
            }

            int id = taskIdSeq++;
            TaskHandler h = new StompHandler(msg, id);
            Collection<Integer> fpcIds = new ArrayList<>();
            Collection<Task> tasks = new ArrayList<>();
            for (Fpc f : fpcs) {
                Task t = f.get(request, periodMs, h);
                if (t == null) {
                    continue;
                }
                fpcIds.add(f.getId());
                tasks.add(t);
            }

            if (tasks.size() == 0) {
                throw new PerLaException(
                        "No FPC can satisfy the requested query");
            }

            RestTask t = new RestTask(id, request, periodMs, fpcIds, tasks);
            taskMap.put(id, t);
            return t;
        } finally {
            l.writeLock().unlock();
        }
    }

    /**
     * Returns a single {@link Task}
     *
     * @param id {@link Task} identifier
     * @return
     */
    public RestTask getTask(int id) {
        l.readLock().lock();
        try {
            return taskMap.get(id);
        } finally {
            l.readLock().unlock();
        }
    }

    /**
     * Returns all active {@link Task}s
     *
     * @return
     */
    public Collection<RestTask> getAllTasks() {
        l.readLock().lock();
        try {
            return taskMap.values();
        } finally {
            l.readLock().unlock();
        }
    }

    /**
     * Stops a query
     *
     * @param id {@link Task} id
     */
    public void stopTask(int id) {
        l.writeLock().lock();
        try {
            RestTask t = taskMap.remove(id);
            if (t == null) {
                return;
            }
            t.stop();
        } finally {
            l.writeLock().unlock();
        }
    }

    
    public void start(){
		   consumerThread.start();
		   System.out.println("Consumer started");
	   }
    
    
    /**
     * Stops the {@code PerLaController}. This method recursively stops all
     * queries and all FPCs.
     */
    
    
    public void shutdown() {
        l.writeLock().lock();
        try {
            logger.info("Stopping all active queries...");
            taskMap.keySet().forEach(this::stopTask);
            logger.info("Stopping all active FPCs...");
            CountDownLatch l = new CountDownLatch(registry.getAll().size());
            for (Fpc f : registry.getAll()) {
                f.stop((stopped) -> {
                    registry.remove(stopped);
                    l.countDown();
                });
            }
            try {
                l.await();
                Executor.shutdown(20);
                logger.info("PerLa system successfully stopped");
            } catch (InterruptedException e) {
                logger.error("PerLa shutdown sequence interrupted", e);
            }
        } finally {
            l.writeLock().unlock();
        }
    }

}
