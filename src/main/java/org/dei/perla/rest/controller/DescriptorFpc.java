package org.dei.perla.rest.controller;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    public Task set(Map<Attribute, Object> valueMap, boolean strict,
            TaskHandler handler) {
        return fpc.set(valueMap, strict, handler);
    }

    @Override
    public Task get(List<Attribute> attributes, boolean strict,
            TaskHandler handler) {
        return fpc.get(attributes, strict, handler);
    }

    @Override
    public Task get(List<Attribute> attributes, boolean strict, long periodMs,
            TaskHandler handler) {
        return fpc.get(attributes, strict, periodMs, handler);
    }

    @Override
    public Task async(List<Attribute> attributes, boolean strict,
            TaskHandler handler) {
        return fpc.get(attributes, strict, handler);
    }

    @Override
    public void stop(Consumer<Fpc> handler) {
        fpc.stop(handler);
    }

    public String getDescriptor() {
        return descriptor;
    }

}
