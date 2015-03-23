package org.dei.perla.rest.controller;

import org.dei.perla.core.fpc.Period;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.utils.StopHandler;

import java.util.Collection;
import java.util.Map;

/**
 * @author Guido Rota 11/10/14.
 */
public class DescriptorFpc implements Fpc {

    private final Fpc fpc;
    private final String descriptor;

    public DescriptorFpc(Fpc fpc, String descriptor) {
        this.fpc = fpc;
        this.descriptor = descriptor;
    }

    @Override
    public int getId() {
        return fpc.getId();
    }

    @Override
    public String getType() {
        return fpc.getType();
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return fpc.getAttributes();
    }

    @Override
    public Task set(Map<Attribute, Object> valueMap, TaskHandler handler) {
        return fpc.set(valueMap, handler);
    }

    @Override
    public Task get(Collection<Attribute> attributes, TaskHandler handler) {
        return fpc.get(attributes, handler);
    }

    @Override
    public Task get(Collection<Attribute> attributes, Period period,
            TaskHandler handler) {
        return fpc.get(attributes, period, handler);
    }

    @Override
    public Task get(Collection<Attribute> attributes, long periodMs, TaskHandler handler) {
        return fpc.get(attributes, periodMs, handler);
    }

    @Override
    public Task async(Collection<Attribute> attributes, TaskHandler handler) {
        return fpc.get(attributes, handler);
    }

    @Override
    public void stop(StopHandler<Fpc> handler) {
        fpc.stop(handler);
    }

    public String getDescriptor() {
        return descriptor;
    }

}
