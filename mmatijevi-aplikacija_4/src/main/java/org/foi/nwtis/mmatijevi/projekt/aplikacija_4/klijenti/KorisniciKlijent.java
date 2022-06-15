package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikRegistracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.usluge.ParserRestOdgovoraUzPodatke;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;
import org.foi.nwtis.podaci.Korisnik;

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
			throws KorisnikNePostojiException, KorisnikNeispravanException, KorisnikVecPostojiException,
			ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		ProvjereKlijent pk = new ProvjereKlijent(kontekst);
		Zeton zeton = pk.prijaviKorisnika(sustavKorisnik, sustavLozinka);

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", sustavKorisnik)
				.header("zeton", zeton.getZeton())
				.post(Entity.entity(korisnik, "application/json"));

		boolean uspjeh = false;

		if (restOdgovor.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
			throw new KorisnikNeispravanException();
		} else if (restOdgovor.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
			throw new KorisnikVecPostojiException(korisnik.getKorIme());
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		} else if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			uspjeh = true;
		}

		return uspjeh;
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

			korisnici = ParserRestOdgovoraUzPodatke
					.<List<Korisnik>>dohvatiPodatkeIzRestOdgovora(jsonOdgovor);
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return korisnici;
	}

	public List<String> dohvatiKorisnikoveGrupe(PrijavljeniKorisnik korisnik) throws ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa).path(korisnik.getKorime()).path("grupe");
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik.getKorime())
				.header("zeton", korisnik.getZeton())
				.get();

		List<String> grupe = null;
		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String jsonOdgovor = restOdgovor.readEntity(String.class);
			grupe = ParserRestOdgovoraUzPodatke.dohvatiPodatkeIzRestOdgovora(jsonOdgovor);
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return grupe;
	}
}
