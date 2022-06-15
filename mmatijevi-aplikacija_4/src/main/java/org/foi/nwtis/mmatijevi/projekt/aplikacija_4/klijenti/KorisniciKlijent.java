package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikRegistracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.RestOdgovorUzPodatke;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		} else if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			uspjeh = true;
		}

		return uspjeh;
	}

	public List<Korisnik> dohvatiSveKorisnike(String korime, Zeton zeton) throws ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa);
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korime)
				.header("zeton", zeton.getOznaka())
				.get();

		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String odgovor = restOdgovor.readEntity(String.class);

			TypeToken<RestOdgovorUzPodatke<Korisnik[]>> odgovorToken = new TypeToken<RestOdgovorUzPodatke<Korisnik[]>>() {
			};
			RestOdgovorUzPodatke<Korisnik[]> odgovorParsirani = KorisniciKlijent.parsirajJson(odgovor,
					odgovorToken.getType());

			korisnici = new LinkedList<>();
			korisnici.addAll(Arrays.asList((Korisnik[]) odgovorParsirani.getPodaci()));

		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return korisnici;
	}

	public String[] dohvatiKorisnikoveGrupe(String korime, Zeton zeton) throws ZetonIstekaoException {
		Client client = ClientBuilder.newClient();

		WebTarget webResource = client.target(this.odredisnaAdresa).path(korime).path("grupe");
		Response restOdgovor = webResource.request()
				.header("Accept", "application/json")
				.header("korisnik", korime)
				.header("zeton", zeton.getOznaka())
				.get();

		String[] grupe = null;
		if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
			String odgovor = restOdgovor.readEntity(String.class);

			TypeToken<RestOdgovorUzPodatke<String[]>> odgovorToken = new TypeToken<RestOdgovorUzPodatke<String[]>>() {
			};
			RestOdgovorUzPodatke<String[]> odgovorParsirani = KorisniciKlijent.parsirajJson(odgovor,
					odgovorToken.getType());

			grupe = (String[]) odgovorParsirani.getPodaci();
		} else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
			throw new ZetonIstekaoException();
		}

		return grupe;
	}

	private static <T> T parsirajJson(String strRequest, java.lang.reflect.Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(strRequest, typeOfT);
	}

}
