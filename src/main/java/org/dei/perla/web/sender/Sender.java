package org.dei.perla.web.sender;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.objectweb.joram.client.jms.Queue;
import org.dei.perla.web.aggr.types.*;

public class Sender {

    public static void sendDataMessage(DataMessage message) throws Exception{
    	Properties p = new Properties();
		p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "192.168.0.3");
	    p.setProperty("java.naming.factory.port", "16400");
	    javax.naming.Context jndiCtx = new InitialContext(p);
	    Destination queue = (Queue) jndiCtx.lookup("queue");
	    ConnectionFactory cf = (ConnectionFactory) jndiCtx.lookup("cf");
	    jndiCtx.close();
	    Connection cnx = cf.createConnection();
	    Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = sess.createProducer(queue);
	    ObjectMessage omsg = sess.createObjectMessage(message);
	    producer.send(omsg);
	    cnx.close();
	    
	  
	}
    
    public static void sendFpcMessage(AddFpcMessage message) throws Exception{
    	Properties p = new Properties();
		p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "192.168.0.3");
	    p.setProperty("java.naming.factory.port", "16400");
	    javax.naming.Context jndiCtx = new InitialContext(p);
	    Destination queue = (Queue) jndiCtx.lookup("queue");
	    ConnectionFactory cf = (ConnectionFactory) jndiCtx.lookup("cf");
	    jndiCtx.close();
	    Connection cnx = cf.createConnection();
	    Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = sess.createProducer(queue);
	    ObjectMessage omsg = sess.createObjectMessage(message);
	    producer.send(omsg);
	    cnx.close();
	    
	  
	}
    
    public static void main (String args[]){
    	
    	DataMessage mex=null;
    	try {
			sendDataMessage(mex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
}
