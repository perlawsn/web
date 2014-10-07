package org.dei.perla.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.dei.perla.fpc.Task;
import org.dei.perla.fpc.TaskHandler;
import org.dei.perla.fpc.engine.Record;
import org.springframework.messaging.core.MessageSendingOperations;

public class StompHandler implements TaskHandler {

	private final MessageSendingOperations<String> msg;
	private final String dest;

	public StompHandler(MessageSendingOperations<String> msg, int id) {
		this.msg = msg;
		this.dest = "/output/" + id;
	}

	@Override
	public void complete(Task task) {
		System.out.println("completed");
	}

	@Override
	public synchronized void newRecord(Task task, Record record) {
		msg.convertAndSend(dest, convert(record));
	}

	@Override
	public void error(Task task, Throwable cause) {
		System.out.println("error");
	}

	private Map<String, String> convert(Record record) {
		Map<String, String> m = new HashMap<>();
		record.fields().forEach((f) -> {
			m.put(f.getName(), f.getValue().toString());
		});
		return m;
	}

}
