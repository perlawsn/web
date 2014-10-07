package org.dei.perla.rest.controller;

import org.dei.perla.fpc.Task;
import org.dei.perla.fpc.TaskHandler;
import org.dei.perla.fpc.engine.Record;
import org.springframework.messaging.core.MessageSendingOperations;

public class STOMPHandler implements TaskHandler {

	private final MessageSendingOperations<String> msg;
	private final String dest;

	public STOMPHandler(MessageSendingOperations<String> msg, int id) {
		this.msg = msg;
		this.dest = "/output/" + id;
	}

	@Override
	public void complete(Task task) {
		System.out.println("completed");
	}

	@Override
	public void newRecord(Task task, Record record) {
		msg.convertAndSend(dest, record);
	}

	@Override
	public void error(Task task, Throwable cause) {
		System.out.println("error");
	}

}
