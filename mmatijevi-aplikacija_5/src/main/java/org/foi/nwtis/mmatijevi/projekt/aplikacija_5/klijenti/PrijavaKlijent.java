package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti;

import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Ova klasa služi za dohvat žetona globalnog korisnika sustava.
 * Ona omogućuje izvršavanje radnji u njegovo ime.
 */
public class PrijavaKlijent extends PristupServisu {
	public PrijavaKlijent(ServletContext kontekst) {
		super("provjere", kontekst);
	}

	public Zeton prijaviSistemskogKorisnika() {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", sustavKorisnik)
				.header("lozinka", sustavLozinka)
				.get();
		Zeton odgovorZeton = null;

		String odgovor = restOdgovor.readEntity(String.class);
		Gson gson = new Gson();
		odgovorZeton = gson.fromJson(odgovor, Zeton.class);

		return odgovorZeton;
	}
}
