package org.dei.perla.web.aggr.types;

import java.io.Serializable;

import org.dei.perla.core.fpc.Sample;

public class DataMessage implements Serializable{

	private static final long serialVersionUID = -4780173621200356867L;
	
	private Sample sample;

	public DataMessage (Sample sample){
		this.sample = sample;
	}
	
	public Sample getSample() {
		return sample;
	}

}
