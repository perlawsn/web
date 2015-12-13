package org.dei.perla.web.aggre.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dei.perla.web.aggr.types.DataMessage;

public class MirrorTaskConsumer implements Runnable {

	private javax.naming.Context ictx = null;
	private Destination dest = null;
	private ConnectionFactory cf = null;
	private MirrorTask task;

	public MirrorTaskConsumer(MirrorTask task) {
		this.task = task;
	}

	@Override
	public void run() {
		System.out.println("Listens to ");
		Properties p = new Properties();
		p.setProperty("java.naming.factory.initial",
				"fr.dyade.aaa.jndi2.client.NamingContextFactory");
		p.setProperty("java.naming.factory.host", "localhost");
		p.setProperty("java.naming.factory.port", "16400");

		try {
			ictx = new InitialContext(p);

			dest = (Destination) ictx.lookup(task.getQueue());
			cf = (ConnectionFactory) ictx.lookup("cf");
			ictx.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection cnx;
		try {
			cnx = cf.createConnection();
			Session sess = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer recv = sess.createConsumer(dest);

			recv.setMessageListener(new MsgListener());
			cnx.start();
			System.in.read();
			cnx.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Consumer closed.");

	}

	public class MsgListener implements MessageListener {

		public void onMessage(Message msg) {
			try {

				Enumeration e = msg.getPropertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String value = msg.getStringProperty(key);

				}

				if (msg instanceof TextMessage) {
					System.out.println(((TextMessage) msg).getText());
				} else if (msg instanceof ObjectMessage) {

					if (((ObjectMessage) msg).getObject() instanceof DataMessage) {

						DataMessage message = (DataMessage) ((ObjectMessage) msg)
								.getObject();

						task.getHandler().data(task, message.getSample());

					}

				} else {
					System.out.println("Error");
				}
			} catch (JMSException jE) {
				System.err.println("Exception in listener: " + jE);
			}
		}
	}

}
