package org.dei.perla.web.aggre.server;

import java.net.ConnectException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.naming.NamingException;

import org.dei.perla.web.aggr.types.GetMessage;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Sample;
import org.dei.perla.core.fpc.base.SamplePipeline;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.objectweb.joram.client.jms.Queue;
import org.objectweb.joram.client.jms.admin.AdminException;
import org.objectweb.joram.client.jms.admin.AdminModule;
import org.objectweb.joram.client.jms.admin.User;
import org.objectweb.joram.client.jms.tcp.TcpConnectionFactory;

public class MirrorTask implements Task{
	
	private  List<Attribute> atts;
	private  MirrorTaskHandler handler;
	private  int fpcId;
	private  String nodeId;
	private  String queue;
	private ServerMethods servMsgProd = new ServerMethods();
	private boolean hasStarted = false;
    private boolean running = false;
    private SamplePipeline pipeline;
    private Properties p = new Properties();
    private javax.naming.Context jndiCtx;
    private long periodMs;
	public MirrorTask(List <Attribute> atts, TaskHandler handler, boolean strict, 
			long periodMs, String nodeId, int fpcId){
		
		
		this.atts=atts;
		this.handler=(MirrorTaskHandler) handler;
		this.fpcId=fpcId;
		this.nodeId=nodeId;
		
		queue = generateQueue();
		
		subscribeQueue();
		GetMessage reqMess = new GetMessage(atts, strict, false, periodMs, nodeId, queue, this.fpcId);
		try {
			servMsgProd.sendGetMessage(reqMess);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	public MirrorTask(List <Attribute> atts, TaskHandler handler, boolean strict, 
			String nodeId, int fpcId){
		
		this.atts=atts;
		this.handler=(MirrorTaskHandler) handler;
		this.fpcId=fpcId;
		this.nodeId=nodeId;
		
		queue = generateQueue();
		
		subscribeQueue();
		GetMessage reqMess = new GetMessage(atts, strict, false, -1, nodeId, queue, this.fpcId);
		try {
			servMsgProd.sendGetMessage(reqMess);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startConsumer();				
	}
	
	public MirrorTask(List <Attribute> atts, TaskHandler handler, boolean strict, 
			boolean async, String nodeId, int fpcId ){
		
		this.atts=atts;
		this.handler=(MirrorTaskHandler) handler;
		this.fpcId=fpcId;
		this.nodeId=nodeId;
		
		queue = generateQueue();
		
		subscribeQueue();
		GetMessage reqMess = new GetMessage(atts, strict, async, -1, nodeId, queue, this.fpcId);
		try {
			servMsgProd.sendGetMessage(reqMess);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startConsumer();
	}
	
	 public MirrorTask(int id, Collection<Attribute> attributes,
	            long period, Collection<Integer> fpcs, Collection<Task> tasks) {
	        this.fpcId = id;
	        this.periodMs = period;
	        this.atts =(List<Attribute>) attributes;
	        
	        
	    }
	
	protected final synchronized void processSample(Object[] sample) {
	        if (!running) {
	            return;
	        }
	        Sample output = pipeline.run(sample);
	        handler.data(this, output);
	    }
	
	@Override
	public List<Attribute> getAttributes() {
		// TODO Auto-generated method stub
		return atts;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return running;
	}
	
	@Override
	public void stop() {
		
		// Mando lo stop su queue
		running = false;
		
	}

	protected final synchronized void start() {
		
        if (hasStarted) {
            throw new IllegalStateException("Cannot start," +
                    "MirrorTask has already been started once");
        }
        running = true;
        hasStarted = true;
        
        //Iscrive la coda sul server 
        subscribeQueue();
        
      
        
        
        
    }
	
	private String generateQueue() {
		//To do 
		String fpcString = ((Integer)fpcId).toString();
		String queue = "queue" + nodeId + fpcString;
		return queue;
		
    }
	
	public void subscribeQueue(){
		
	    
	    boolean connected=false;
	    
	    while (!connected){
	    
	    Queue queue;
		try {
			queue = Queue.create(this.queue);
			queue.setFreeReading();
			queue.setFreeWriting();
			jndiCtx.bind(this.queue, queue);
			
		    jndiCtx.close();
		} catch (ConnectException | AdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e){
			continue;
		}
	      
    	AdminModule.disconnect();
	    System.out.println("Admin closed.");
	    connected = true;
	    }
		
		
	}
	
	
	public void createContext(){
		
		ConnectionFactory cf = TcpConnectionFactory.create("localhost", 16010);
	    try {
			AdminModule.connect(cf, "root", "root");
			User.create("anonymous", "anonymous");
		} catch (ConnectException | AdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    QueueConnectionFactory qcf = TcpConnectionFactory.create("localhost", 16010);
	    p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "localhost"); //Remote host
	    p.setProperty("java.naming.factory.port", "16400");
	    
		try {
			jndiCtx = new javax.naming.InitialContext(p);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public MirrorTaskHandler getHandler(){
		return this.handler;
	}
	
	public String getQueue(){
		return queue;
	}

	public void startConsumer(){
		MirrorTaskConsumer mtc = new MirrorTaskConsumer(this);
		new Thread(mtc).start();
	}
}

