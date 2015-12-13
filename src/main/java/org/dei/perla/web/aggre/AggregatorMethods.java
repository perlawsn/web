package org.dei.perla.web.aggre;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
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

import org.dei.perla.web.aggr.types.AddFpcMessage;
import org.dei.perla.web.aggr.types.DataMessage;
import org.dei.perla.core.fpc.Attribute;
import org.objectweb.joram.client.jms.Queue;

public class AggregatorMethods {
	
	HashMap<String, String> map;
	static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwzxy";
    static Random rnd = new Random(System.currentTimeMillis());
    static final int LENGHT = 4;
    private String dataQueue;
    
    public AggregatorMethods(String dataQueue){
    	this.dataQueue=dataQueue;
    	
    }
    
    public AggregatorMethods(){
    	
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

    //Invio dati per la creazione di un FPC su server
    public void sendFpcMessage(AddFpcMessage fpc) throws Exception{
    	
    	clone(fpc);
    	
		Properties p = new Properties();
		p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "localhost");
	    p.setProperty("java.naming.factory.port", "16500");
	    javax.naming.Context jndiCtx = new InitialContext(p);
	    Destination queue = (Queue) jndiCtx.lookup("serverqueue");
	    ConnectionFactory cf = (ConnectionFactory) jndiCtx.lookup("cf");
	    jndiCtx.close();
	    Connection cnx = cf.createConnection();
	    Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = sess.createProducer(queue);
	    ObjectMessage omsg = sess.createObjectMessage(fpc);
	    producer.send(omsg);
	    cnx.close();
	}
    
    public void sendDataMessage(DataMessage message) throws Exception{
    	Properties p = new Properties();
		p.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
	    p.setProperty("java.naming.factory.host", "localhost");
	    p.setProperty("java.naming.factory.port", "16500");
	    javax.naming.Context jndiCtx = new InitialContext(p);
	    Destination queue = (Queue) jndiCtx.lookup(dataQueue);
	    ConnectionFactory cf = (ConnectionFactory) jndiCtx.lookup("cf");
	    jndiCtx.close();
	    Connection cnx = cf.createConnection();
	    Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    MessageProducer producer = sess.createProducer(queue);
	    ObjectMessage omsg = sess.createObjectMessage(message);
	    producer.send(omsg);
	    cnx.close();
	}
    
    public static final Object clone(Serializable in) {
        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(byteOutStream);
            outStream.writeObject(in);
            ByteArrayInputStream byteInStream =
                new ByteArrayInputStream(byteOutStream.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteInStream);
            return inStream.readObject();
        } catch (OptionalDataException e) {
         throw new RuntimeException("Optional data found. " + e.getMessage()); //$NON-NLS-1$
        } catch (StreamCorruptedException e) {
         throw new RuntimeException("Serialized object got corrupted. " + e.getMessage()); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
         throw new RuntimeException("A class could not be found during deserialization. " + e.getMessage()); //$NON-NLS-1$
        } catch (NotSerializableException ex) {
            ex.printStackTrace();
         throw new IllegalArgumentException("Object is not serializable: " + ex.getMessage()); //$NON-NLS-1$
        } catch (IOException e) {
         throw new RuntimeException("IO operation failed during serialization. " + e.getMessage()); //$NON-NLS-1$
        }
    }
    
	
}
