package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.klijenti;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorKorisnici;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
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

	public List<Korisnik> dohvatiSveKorisnike(PrijavljeniKorisnik korisnik) throws ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik.getKorime())
				.header("zeton", korisnik.getZeton())
				.get();

		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String jsonOdgovor = restOdgovor.readEntity(String.class);

			List<? extends Korisnik> korisniciBezLozinke = new Gson().fromJson(jsonOdgovor, RestOdgovorKorisnici.class)
					.getPodaci();
			korisnici = (List<Korisnik>) korisniciBezLozinke;
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return korisnici;
	}
}
