package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.slusaci;

import java.io.File;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_2.dretve.PreuzimanjeRasporedaAerodroma;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

	private org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP konfig = null;
	private String putanja = null;
	PreuzimanjeRasporedaAerodroma preuzimanjeDretva = null;

	public SlusacAplikacije() {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext context = sce.getServletContext();
		String nazivDatoteke = context.getInitParameter("konfiguracija");
		this.putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;

		System.out.println(nazivDatoteke);

		konfig = new PostavkeBazaPodataka(nazivDatoteke);
		try {
			konfig.ucitajKonfiguraciju();
			context.setAttribute("postavke", konfig);
			System.out.println("Učitani parametri konfiguracije!");
		} catch (NeispravnaKonfiguracija ex) {
			ex.printStackTrace();
			return;
		}

		System.out.println("Slusac Aplikacije 2 pokrenut!");
		preuzimanjeDretva = new PreuzimanjeRasporedaAerodroma(context);
		preuzimanjeDretva.start();

		ServletContextListener.super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		System.out.println("Obrisani parametri konfiguracije!");

		try {
			preuzimanjeDretva.join(500);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			System.err.println("Problem pri zaustavljanju dretve: " + ex.getLocalizedMessage());
		}

		ServletContextListener.super.contextDestroyed(sce);
	}
}
