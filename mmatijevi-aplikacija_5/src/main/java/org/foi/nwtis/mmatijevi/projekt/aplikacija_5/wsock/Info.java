package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.wsock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jws.WebMethod;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/info")
public class Info {
	private static Set<Session> sesije = new HashSet<>();

	@WebMethod
	public void dajMeteo(String info) {
		informiraj(info);
	}

	public void informiraj(String poruka) {
		for (Session sesija : sesije) {
			if (sesija.isOpen()) {
				try {
					sesija.getBasicRemote().sendText(poruka);
				} catch (IOException ex) {
					Logger.getLogger(Info.class.getName()).log(Level.WARNING,
							"Pogreška kod slanja poruke za sesiju: " + sesija.getId(), ex);
				}
			}
		}
	}

	@OnOpen
	public void otvori(Session sesija, EndpointConfig konfig) {
		sesije.add(sesija);
		Terminal.infoIspis("Otvorena veza: " + sesija.getId());
	}

	@OnClose
	public void zatvori(Session sesija, CloseReason razlog) {
		Terminal.infoIspis("Zatvorena veza: " + sesija.getId() + " Razlog: " + razlog.getReasonPhrase());
		sesije.remove(sesija);
	}

	@OnMessage
	public void stiglaPoruka(Session sesija, String poruka) {
		Terminal.infoIspis("Veza: " + sesija.getId() + " Poruka: " + poruka);
	}

	@OnError
	public void pogreska(Session sesija, Throwable iznimka) {
		Terminal.infoIspis("Veza: " + sesija.getId() + " Pogreška: " + iznimka.getMessage());
	}
}
