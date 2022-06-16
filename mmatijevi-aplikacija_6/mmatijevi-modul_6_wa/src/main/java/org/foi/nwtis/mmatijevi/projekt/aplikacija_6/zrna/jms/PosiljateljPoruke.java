package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

@Stateless
public class PosiljateljPoruke {

	@Resource(mappedName = "jms/NWTiS_mmatijeviFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "jms/NWTiS_mmatijevi")
	private Queue queue;

	public boolean noviPoruka(String poruka) {
		boolean status = true;

		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(queue);
			TextMessage message = session.createTextMessage(poruka);
			messageProducer.send(message);
			messageProducer.close();
			connection.close();
		} catch (JMSException ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}
}
