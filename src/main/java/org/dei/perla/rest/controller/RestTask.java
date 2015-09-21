package org.dei.perla.rest.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Task;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestTask {

    private AtomicBoolean running = new AtomicBoolean(true);

    private final int id;
    private final long period;
    private final Collection<Attribute> attributes;
    private final Collection<Integer> fpcs;

    @JsonIgnore
    private Collection<Task> tasks;

    public RestTask(int id, Collection<Attribute> attributes,
            long period, Collection<Integer> fpcs, Collection<Task> tasks) {
        this.id = id;
        this.period = period;
        this.attributes = attributes;
        this.fpcs = fpcs;
        this.tasks = tasks;
    }

    public int getId() {
        return id;
    }

    public long getPeriod() {
        return period;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public Collection<Integer> getFpcs() {
        return fpcs;
    }

    public Collection<Task> getTasks() {
        return tasks;
    }

    public boolean isRunning() {
        return running.get();
    }

    protected void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        for (Task t : tasks) {
            t.stop();
        }
    }

}
