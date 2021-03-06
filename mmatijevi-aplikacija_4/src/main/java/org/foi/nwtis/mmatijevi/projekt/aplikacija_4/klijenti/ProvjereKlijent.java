package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Ova klasa služi kao klijent između ove aplikacije i aplikacije 3 u pozadini.
 * Ova klasa brine se za slanje i primanje informacija o provjerama i žetonima.
 */
public class ProvjereKlijent extends PristupServisu {
	public ProvjereKlijent(ServletContext kontekst) {
		super("provjere", kontekst);
	}

	public Zeton prijaviKorisnika(String korime, String lozinka) throws KorisnikNePostojiException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korime)
				.header("lozinka", lozinka)
				.get();
		Zeton odgovorZeton = null;

		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			odgovorZeton = gson.fromJson(odgovor, Zeton.class);
		} else {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();

			String porukaPogreske;
			try {
				RestOdgovor odgovorPogreske = gson.fromJson(odgovor, RestOdgovor.class);
				porukaPogreske = odgovorPogreske.getPoruka();
			} catch (Exception e) {
				porukaPogreske = "Dogodio se problem s poslužiteljem!";
				e.printStackTrace();
			}
			throw new KorisnikNePostojiException(porukaPogreske);
		}

		return odgovorZeton;
	}

	public boolean deaktivirajZeton(PrijavljeniKorisnik korisnik) {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa).path(String.valueOf(korisnik.getZeton()));
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik.getKorime())
				.header("lozinka", korisnik.getLozinka())
				.delete();

		return restOdgovor.getStatus() == Response.Status.OK.getStatusCode();
	}

	public String deaktivirajSveZetone(String korime, PrijavljeniKorisnik korisnik) {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa).path("korisnik").path(korime);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik.getKorime())
				.header("lozinka", korisnik.getLozinka())
				.delete();

		String porukaSaPosluzitelja = "";

		String odgovor = restOdgovor.readEntity(String.class);
		Gson gson = new Gson();
		try {
			RestOdgovor odgovorPogreske = gson.fromJson(odgovor, RestOdgovor.class);
			porukaSaPosluzitelja = odgovorPogreske.getPoruka();
		} catch (Exception e) {
			porukaSaPosluzitelja = "Dogodio se problem s poslužiteljem!";
			e.printStackTrace();
		}

		return porukaSaPosluzitelja;
	}
}
