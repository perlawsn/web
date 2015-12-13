package org.dei.perla.web.aggre.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;

public class MirrorFpc implements Fpc {
		
		private final String nodeId;
	  	private final int id;
	    private final String type;
	    private final Collection<Attribute> atts;
	    
	    protected MirrorFpc(int id, String nodeId, String type, Collection<Attribute> atts) {
	        this.id = id;
	        this.type = type;
	        this.atts = atts;
	        this.nodeId = nodeId;
	        System.out.println("Mirror created");
	    }

	    @Override
	    public int getId() {
	        return id;
	    }

	    @Override
	    public String getType() {
	        return type;
	    }

	    @Override
	    public Collection<Attribute> getAttributes() {
	        return atts;
	    }

		@Override
		public Task set(Map<Attribute, Object> values, boolean strict,
				TaskHandler handler) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Task get(List<Attribute> atts, boolean strict,
				TaskHandler handler) {
						
			MirrorTask mirTask = new MirrorTask(atts, handler, strict, nodeId, id);
			
			mirTask.startConsumer();
						
			return mirTask;
		}

		@Override
		public Task get(List<Attribute> atts, boolean strict, long periodMs,
				TaskHandler handler) {
			
			MirrorTask mirTask = new MirrorTask(atts, handler, strict, periodMs, nodeId, id);
			
			mirTask.startConsumer();
			
			return mirTask;
		}

		@Override
		public Task async(List<Attribute> atts, boolean strict,
				TaskHandler handler) {
			
			MirrorTask mirTask = new MirrorTask(atts, handler, strict, true, nodeId, id);
			
			mirTask.startConsumer();
			
			return mirTask;
								
			
		}

		@Override
		public void stop(Consumer<Fpc> handler) {
			// TODO Auto-generated method stub
			//creare stop message da inviare giù
		}
}
