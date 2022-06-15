package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikRegistracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Ova klasa služi kao klijent između ove aplikacije i aplikacije 3 u pozadini.
 * Ova klasa brine se za slanje i primanje informacija o korisnicima.
 */
public class KorisniciKlijent extends PristupServisu {
	public KorisniciKlijent(ServletContext kontekst) {
		super("korisnici", kontekst);
	}

	public boolean registrirajKorisnika(KorisnikRegistracija korisnik)
			throws KorisnikNePostojiException, KorisnikNeispravanException, KorisnikVecPostojiException {
		Client client = ClientBuilder.newClient();

		String sustavKorisnik = this.konfig.dajPostavku("sustav.korisnik");
		String sustavLozinka = this.konfig.dajPostavku("sustav.lozinka");

		ProvjereKlijent pk = new ProvjereKlijent(kontekst);
		Zeton zeton = pk.prijaviKorisnika(sustavKorisnik, sustavLozinka);

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", sustavKorisnik)
				.header("zeton", zeton.getOznaka())
				.post(Entity.entity(korisnik, "application/json"));

		boolean uspjeh = false;

		if (restOdgovor.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
			throw new KorisnikNeispravanException();
		} else if (restOdgovor.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
			throw new KorisnikVecPostojiException(korisnik.getKorIme());
		} else if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			uspjeh = true;
		}

		return uspjeh;
	}
}
