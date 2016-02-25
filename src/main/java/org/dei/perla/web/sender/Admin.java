package org.dei.perla.web.sender;

	

import java.net.ConnectException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.naming.NamingException;

import org.objectweb.joram.client.jms.Queue;
import org.objectweb.joram.client.jms.admin.AdminException;
import org.objectweb.joram.client.jms.admin.AdminModule;
import org.objectweb.joram.client.jms.admin.User;
import org.objectweb.joram.client.jms.tcp.TcpConnectionFactory;

public class Admin {
	
    private Properties p = new Properties();
    private javax.naming.Context jndiCtx;
    private int port1;
    private String port2;
    
    public Admin(int port1, String port2 ){
    	this.port1=port1;
    	this.port2=port2;
    
    }
    
	public void createNodeContext() {
		
		ConnectionFactory cf = TcpConnectionFactory.create("localhost", port1);
	    try {
			AdminModule.connect(cf, "root", "root");
			User.create("anonymous", "anonymous");
		} catch (ConnectException | AdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    QueueConnectionFactory qcf = TcpConnectionFactory.create("localhost", port1);
	    p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "192.168.0.3"); //Remote host
	    p.setProperty("java.naming.factory.port", port2);
	    
		try {
			jndiCtx = new javax.naming.InitialContext(p);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    boolean connected=false;
	    String tempNodeId =null;
	    while (!connected){
	    
	    Queue queue;
		try {
			queue = Queue.create("AggrQueue"+ tempNodeId);
			queue.setFreeReading();
			queue.setFreeWriting();
			jndiCtx.bind("queuea", queue);
			
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
	public static void main(String args[]){
		Admin adm=new Admin(16010, "16400");
    	adm.createNodeContext();
	}


}
