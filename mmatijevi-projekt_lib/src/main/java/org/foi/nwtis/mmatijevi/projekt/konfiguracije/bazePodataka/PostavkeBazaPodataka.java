package org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka;

import java.util.Properties;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;

public class PostavkeBazaPodataka extends KonfiguracijaApstraktna
		implements KonfiguracijaBP {

	public PostavkeBazaPodataka(String nazivDatoteke) {
		super(nazivDatoteke);
	}

	public String getAdminDatabase() {
		return this.dajPostavku("admin.database");
	}

	public String getAdminPassword() {
		return this.dajPostavku("admin.password");
	}

	public String getAdminUsername() {
		return this.dajPostavku("admin.username");
	}

	public String getDriverDatabase() {
		String driver = this.getDriverDatabase(this.getServerDatabase());
		return driver;
	}

	public String getDriverDatabase(String urlBazePodataka) {
		String p[] = urlBazePodataka.split("//");
		if (p.length == 0) {
			System.out.println("Problem jer nema elemenata.");
			return null;
		}
		String kljuc = p[0].substring(0, p[0].length() - 1);
		kljuc = kljuc.replace(':', '.');
		return this.dajPostavku(kljuc);
	}

	public Properties getDriversDatabase() {
		Properties drivers = new Properties();
		Properties prop = this.dajSvePostavke();
		for (Object o : prop.keySet()) {
			String k = (String) o;
			if (k.startsWith("jdbc.")) {
				String v = this.dajPostavku(k);
				drivers.setProperty(k, v);
			}
		}
		return drivers;
	}

	public String getServerDatabase() {
		return this.dajPostavku("server.database");
	}

	public String getUserDatabase() {
		return this.dajPostavku("user.database");
	}

	public String getUserPassword() {
		return this.dajPostavku("user.password");
	}

	public String getUserUsername() {
		return this.dajPostavku("user.username");
	}

	@Override
	public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		this.postavke = konfig.dajSvePostavke();
	}

	@Override
	public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
		Konfiguracija konfig = KonfiguracijaApstraktna.dajKonfiguraciju(datoteka);
		Properties prop = this.dajSvePostavke();
		for (Object o : prop.keySet()) {
			String k = (String) o;
			String v = this.dajPostavku(k);
			konfig.spremiPostavku(k, v);
		}
		konfig.spremiKonfiguraciju();
	}

}
