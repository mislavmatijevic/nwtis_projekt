package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.slusaci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.modeli.JmsPoruka;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms.JmsPorukeZrno;
import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {
	@EJB
	JmsPorukeZrno jmsPorukeZrno;
	Konfiguracija konfig;

	/**
	 * Učitavanje konfiguracije
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext kontekst = sce.getServletContext();
		String nazivDatoteke = kontekst.getInitParameter("konfiguracija");
		String putanja = kontekst.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;

		try {
			konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
			kontekst.setAttribute("postavke", konfig);
			procitajJmsPorukeIzDatoteke(konfig.dajPostavku("datoteka.jmsPoruke"));
		} catch (NeispravnaKonfiguracija ex) {
			Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.WARNING, null, ex);
			Terminal.greskaIspis("Konfiguracija nije ispravna! " + ex.getMessage());
			return;
		}

		ServletContextListener.super.contextInitialized(sce);
	}

	/**
	 * Briše konfiguraciju prilikom gašenja.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		pohraniJmsPorukeUDatoteku(konfig.dajPostavku("datoteka.jmsPoruke"));
		ServletContext kontekst = sce.getServletContext();
		kontekst.removeAttribute("postavke");
		Terminal.infoIspis("Obrisani parametri konfiguracije!");
		ServletContextListener.super.contextDestroyed(sce);
	}

	private void procitajJmsPorukeIzDatoteke(String nazivDatotekeJmsPoruka) {
		try (FileInputStream fo = new FileInputStream(new File(nazivDatotekeJmsPoruka));
				ObjectInputStream oo = new ObjectInputStream(fo);) {
			jmsPorukeZrno.setListaPoruka(((LinkedList<JmsPoruka>) oo.readObject()));
		} catch (Exception ex) {
			Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, "Problem s učitavanjem jms poruka",
					ex);
			Terminal.greskaIspis("JMS poruke nisu učitane!");
		}
	}

	private void pohraniJmsPorukeUDatoteku(String nazivDatotekeJmsPoruka) {
		File datoteka = new File(nazivDatotekeJmsPoruka);

		if (!jmsPorukeZrno.getListaPoruka().isEmpty()) {
			try (FileOutputStream fo = new FileOutputStream(datoteka);
					ObjectOutputStream oo = new ObjectOutputStream(fo);) {
				oo.writeObject(jmsPorukeZrno.getListaPoruka());
			} catch (Exception ex) {
				Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, "Problem s pohranom jms poruka",
						ex);
				Terminal.greskaIspis("JMS poruke nisu pohranjene!");
			}
		} else if (datoteka.exists()) {
			datoteka.delete();
		}
	}
}
