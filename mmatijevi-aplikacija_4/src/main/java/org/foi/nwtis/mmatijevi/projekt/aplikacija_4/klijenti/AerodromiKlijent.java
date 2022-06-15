package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.usluge.ParserRestOdgovoraUzPodatke;
import org.foi.nwtis.podaci.Aerodrom;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Ova klasa služi kao klijent između ove aplikacije i aplikacije 3 u pozadini.
 * Ova klasa brine se za slanje i primanje informacija o aerodromima.
 */
public class AerodromiKlijent extends PristupServisu {
	public AerodromiKlijent(ServletContext kontekst) {
		super("aerodromi", kontekst);
	}

	public List<Aerodrom> dohvatiPraceneAerodrome(PrijavljeniKorisnik korisnik) throws ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa).queryParam("preuzimanje", 1);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik.getKorime())
				.header("zeton", korisnik.getZeton())
				.get();

		List<Aerodrom> praceniAerodromi = null;
		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String jsonOdgovor = restOdgovor.readEntity(String.class);
			praceniAerodromi = ParserRestOdgovoraUzPodatke.dohvatiPodatkeIzRestOdgovora(jsonOdgovor);
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return praceniAerodromi;
	}
}
