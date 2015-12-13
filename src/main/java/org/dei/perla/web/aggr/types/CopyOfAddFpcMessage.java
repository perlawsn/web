package org.dei.perla.web.aggr.types;

import java.io.Serializable;
import java.util.Collection;

import org.dei.perla.core.fpc.Attribute;

public class CopyOfAddFpcMessage implements Serializable{
	
	private static final long serialVersionUID = -5592331871913188119L;
	
	private String nodeId;
	private int fpcId;
	private Collection<Attribute> attributes;
	
	public CopyOfAddFpcMessage(String nodeId, int fpcId){
		this.nodeId=nodeId;
		this.fpcId = fpcId;		
		
	}
		
	public String getNodeId() {
		return nodeId;
	}
	
	public int getFpcId() {
		return fpcId;
	}
	
	public void setAttributes(Collection<Attribute> attributes){
		this.attributes=attributes;
		
	}
	
}
