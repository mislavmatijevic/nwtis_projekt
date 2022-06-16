package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

	/**
	 * Učitavanje konfiguracije
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext kontekst = sce.getServletContext();
		String nazivDatoteke = kontekst.getInitParameter("konfiguracija");
		String putanja = kontekst.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;

		Konfiguracija konfig;
		try {
			konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
			kontekst.setAttribute("postavke", konfig);
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
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		Terminal.infoIspis("Obrisani parametri konfiguracije!");
		ServletContextListener.super.contextDestroyed(sce);
	}
}
