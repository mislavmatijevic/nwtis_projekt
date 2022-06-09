package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

	private KonfiguracijaBP konfig = null;
	private String putanja = null;

	public SlusacAplikacije() {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Terminal.uspjehIspis("Aplikacija 3 se pali!");

		ServletContext context = sce.getServletContext();

		String nazivDatoteke = context.getInitParameter("konfiguracija");
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;

		konfig = new PostavkeBazaPodataka(nazivDatoteke);

		try {
			konfig.ucitajKonfiguraciju();
			context.setAttribute("postavke", konfig);
			System.out.println("Učitani parametri konfiguracije!");
		} catch (NeispravnaKonfiguracija ex) {
			Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE,
					"Aplikacija 3 nije uspjela pročitati konfiguraciju!", ex);
			;
			return;
		}

		ServletContextListener.super.contextInitialized(sce);

		Terminal.uspjehIspis("Aplikacija 3 podignuta!");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		System.out.println("Obrisani parametri konfiguracije!");

		Terminal.pozorIspis("Aplikacija 3 se gasi!");
		ServletContextListener.super.contextDestroyed(sce);
	}
}
