package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.slusaci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_2.dretve.PreuzimanjeRasporedaAerodroma;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

	private Date vrijemeOd = new Date();
	private Date vrijemeDo = null;
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

		try {
			dnevnikUpisiVremena();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Neuspješan zapis u dnevnik: " + ex.getLocalizedMessage());
		}
		ServletContextListener.super.contextDestroyed(sce);
	}

	private void dnevnikUpisiVremena() throws Exception {
		String dnevnikDatoteka = this.putanja + konfig.dajPostavku("dnevnik.datoteka");

		SimpleDateFormat sdfDnevnik = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		vrijemeDo = new Date();

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(dnevnikDatoteka, true));
			writer.append("\nRad aplikacije: " + sdfDnevnik.format(vrijemeOd) + " - " + sdfDnevnik.format(vrijemeDo));
		} catch (FileNotFoundException e) {
			writer = new BufferedWriter(new FileWriter(dnevnikDatoteka, false));
			writer.append("Rad aplikacije: " + sdfDnevnik.format(vrijemeOd) + " - " + sdfDnevnik.format(vrijemeDo));
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
