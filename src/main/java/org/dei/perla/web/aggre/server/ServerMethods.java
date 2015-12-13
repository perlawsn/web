package org.dei.perla.web.aggre.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.dei.perla.web.aggr.types.GetMessage;
import org.dei.perla.web.aggr.types.QueryMessage;
import org.dei.perla.core.fpc.Attribute;
import org.objectweb.joram.client.jms.Queue;


public class ServerMethods {
	
	HashMap<String, String> map;
	static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwzxy";
    static Random rnd = new Random(System.currentTimeMillis());
    static final int LENGHT = 4;
    
	
	public void sendGetMessage(GetMessage reqMsg) throws Exception{
		Properties p = new Properties();
		p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "localhost");
	    p.setProperty("java.naming.factory.port", "16400");
	    javax.naming.Context jndiCtx = new InitialContext(p);
	    Destination queue = (Queue) jndiCtx.lookup("AggrQueue" + reqMsg.getNodeId());
	    ConnectionFactory cf = (ConnectionFactory) jndiCtx.lookup("cf");
	    jndiCtx.close();
	    Connection cnx = cf.createConnection();
	    Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = sess.createProducer(queue);
	    ObjectMessage omsg = sess.createObjectMessage(reqMsg);
	    producer.send(omsg);
	    cnx.close();
	}

	public void sendQueryMessage(QueryMessage reqMsg) throws Exception{
		
	}
	
	 public HashMap<String, String> generateListAttributes(Collection<Attribute> attributeList){
			
			for(Attribute att:attributeList){
				map.put(att.getId(),att.getType().getId());
						
			}
			
			return map;
		}
		
	    public static String generateId() {
	        StringBuilder sb = new StringBuilder(LENGHT);
	        for (int i = 0; i < LENGHT; i++) {
	            sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
	        }
	        return sb.toString();
	    }
	
}
