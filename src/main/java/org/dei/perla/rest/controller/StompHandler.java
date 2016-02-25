package org.dei.perla.rest.controller;

import org.apache.log4j.Logger;
import org.dei.perla.core.fpc.Sample;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.HashMap;
import java.util.Map;
import org.dei.perla.web.sender.*;
import org.dei.perla.web.aggr.types.*;

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
    public synchronized void data(Task task, Sample sample) {
        try {
        	
            msg.convertAndSend(dest, convert(sample));
            DataMessage message=null;
            Sender.sendDataMessage(message);
           
            
        } catch (Exception e) {
        	
            log.error(e);
        }
    }

    @Override
    public void error(Task task, Throwable cause) {
        log.error("Error in query '" + id + "'", cause);
    }

    private Map<String, String> convert(Sample s) {
        Map<String, String> m = new HashMap<>();
        s.fields().forEach((a) -> {
            String id = a.getId();
            Object value = s.getValue(id);
            m.put(id, value.toString());
            
            System.out.println ("Preso: "+id+"valore:" +value.toString());
            
        });
        return m;
    }

}
