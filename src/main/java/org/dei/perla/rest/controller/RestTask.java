package org.dei.perla.rest.controller;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dei.perla.fpc.Attribute;
import org.dei.perla.fpc.Task;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RestTask implements Task {

	private AtomicBoolean running = new AtomicBoolean(true);

	private int id;
	private Collection<Attribute> attributes;
	private Collection<Integer> fpcs;

	@JsonIgnore
	private Collection<Task> tasks;

	public RestTask(int id, Collection<Attribute> attributes,
			Collection<Integer> fpcs, Collection<Task> tasks) {
		this.id = id;
		this.attributes = attributes;
		this.fpcs = fpcs;
		this.tasks = tasks;
	}

	public int getId() {
		return id;
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

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public void stop() {
		if (!running.compareAndSet(true, false)) {
			return;
		}
		for (Task t : tasks) {
			t.stop();
		}
	}

}
