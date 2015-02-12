package org.dei.perla.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.engine.Record;
import org.springframework.messaging.core.MessageSendingOperations;

public class StompHandler implements TaskHandler {

	private final Logger log = Logger.getLogger(StompHandler.class);

	private final MessageSendingOperations<String> msg;
	private final int id;
	private final String dest;

	public StompHandler(MessageSendingOperations<String> msg, int id) {
		this.msg = msg;
		this.id = id;
		this.dest = "/output/" + id;
	}

	@Override
	public void complete(Task task) {
		log.debug("Task in query '" + id + "' completed");
	}

	@Override
	public synchronized void newRecord(Task task, Record record) {
		try {
			msg.convertAndSend(dest, convert(record));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void error(Task task, Throwable cause) {
		log.error("Error in query '" + id + "'", cause);
	}

	private Map<String, String> convert(Record record) {
		Map<String, String> m = new HashMap<>();
		record.fields().forEach((f) -> {
			m.put(f.getName(), f.getValue().toString());
		});
		return m;
	}

}
