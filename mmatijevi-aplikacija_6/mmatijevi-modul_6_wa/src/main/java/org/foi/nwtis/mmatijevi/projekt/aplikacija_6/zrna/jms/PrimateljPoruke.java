package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(mappedName = "jms/NWTiS_mmatijevi", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class PrimateljPoruke implements MessageListener {

	public PrimateljPoruke() {
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				TextMessage msg = (TextMessage) message;
				StringBuilder sb = new StringBuilder();
				sb.append("Stigla poruka:")
				.append(message.getJMSMessageID())
				.append(" ")
				.append(new java.util.Date(message.getJMSTimestamp()))
				.append(" ")
				.append(msg.getText());
				System.out.println(sb.toString());
			} catch (JMSException ex) {
				ex.printStackTrace();
			}

		}
	}
}
