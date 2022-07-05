package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(mappedName = "jms/NWTiS_mmatijevi", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class PrimateljPoruke implements MessageListener {
	@EJB
	JmsPorukeZrno jmsPorukeZrno;

	public PrimateljPoruke() {
	}

	public void onMessage(Message dobivenaPoruka) {
		if (dobivenaPoruka instanceof TextMessage) {
			TextMessage tekstPoruka = (TextMessage) dobivenaPoruka;
			try {
				jmsPorukeZrno.zabiljeziPoruku(tekstPoruka.getText());
			} catch (JMSException ex) {
				Logger.getLogger(PrimateljPoruke.class.getName()).log(Level.SEVERE,
						"Neuspio upis JMS poruke u listu JMS poruka.", ex);
			}
		}
	}
}
