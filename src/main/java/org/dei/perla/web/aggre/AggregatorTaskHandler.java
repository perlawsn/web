package org.dei.perla.web.aggre;

import org.dei.perla.web.aggr.types.DataMessage;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.fpc.Sample;

public class AggregatorTaskHandler implements TaskHandler {
	
	private AggregatorMethods aggrMet;
	private String queue;
	private Task task;
	
	public AggregatorTaskHandler(String queue){
		this.queue=queue;
	}
	
	protected void setTask(Task task) {
		this.task = task;
	}
	
	@Override
	public void complete(Task task) {
		// TODO Auto-generated method stub
	}

	@Override
	public void data(Task task, Sample sample) {
		//qui scatta l'invio
		
		DataMessage dataMessage = new DataMessage(sample);
		try {
			aggrMet.sendDataMessage(dataMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void error(Task task, Throwable cause) {
		// TODO Auto-generated method stub
	}

}
