package org.dei.perla.web.aggr.types;

import java.io.Serializable;
import java.util.List;
import org.dei.perla.core.fpc.Attribute;


public class GetMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String nodeId;
	private int fpcId;
	private boolean async;
	private boolean strict;
	private List<Attribute> attributes;
	private long periodMs;
	private String queue;
	
	public GetMessage(List<Attribute> attributes, boolean strict, boolean async, 
			long periodMs, String nodeId, String queue, int fpcId){
		this.attributes = attributes;
		this.strict = strict;
		this.async = async;
		this.periodMs = periodMs;
		this.nodeId = nodeId;
		this.fpcId=fpcId;
		this.queue=queue;
		
	}

	public String getNodeId(){
		return nodeId;
	}

	public int getFpcId() {
		return fpcId;
	}

	public boolean isAsync() {
		return async;
	}

	public boolean isStrict() {
		return strict;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public long getPeriodMs() {
		return periodMs;
	}
	
	public String getQueue(){
		return this.queue;
	}
	
}
