package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.ws;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.jms.PosiljateljPoruke;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti.AerodromiKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti.AerodromiKlijent.VrsteVremenskogRaspona;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.wsock.Info;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	@Inject
	ServletContext kontekst;
	@EJB
	PosiljateljPoruke posiljateljPoruke;
	@Inject
	Info info;

	/**
	 * Dohvaća polaske u formatu dd.mm.gggg.
	 * @param korisnik Korisničko ime za koje se operacija izvodi.
	 * @param zeton Žeton izdan korisniku s proslijeđenim korisničkim imenom.
	 * @param icao Aerodrom za koji treba dohvatiti polaske.
	 * @param danOd Najraniji dan polaska u formatu dd.mm.gggg
	 * @param danDo Najkasniji dan polaska u formatu dd.mm.gggg
	 * @return Lista odgovarajućih letova.
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeDan(
			@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton,
			@WebParam(name = "icao") String icao,
			@WebParam(name = "danOd") String danOd,
			@WebParam(name = "danDo") String danDo) {
		return dohvatPolazakaPoKriteriju(korisnik, zeton, icao, danOd, danDo, VrsteVremenskogRaspona.DATUM);
	}

	/**
	 * Dohvaća polaske u formatu UNIX Timestampa (sekunde od 1.1.1970.).
	 * @param korisnik Korisničko ime za koje se operacija izvodi.
	 * @param zeton Žeton izdan korisniku s proslijeđenim korisničkim imenom.
	 * @param icao Aerodrom za koji treba dohvatiti polaske.
	 * @param danOd Najraniji dan polaska u formatu dd.mm.gggg
	 * @param danDo Najkasniji dan polaska u formatu dd.mm.gggg
	 * @return Lista odgovarajućih letova.
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeVrijeme(
			@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton,
			@WebParam(name = "icao") String icao,
			@WebParam(name = "vrijemeOd") String vrijemeOd,
			@WebParam(name = "vrijemeDo") String vrijemeDo) {
		return dohvatPolazakaPoKriteriju(korisnik, zeton, icao, vrijemeOd, vrijemeDo, VrsteVremenskogRaspona.TIMESTAMP);
	}

	/** 
	 * U bazu, u tablicu AERODROMI_PRACENI unosi novi ICAO.
	 * @param korisnik Korisničko ime za koje se operacija izvodi.
	 * @param zeton Žeton izdan korisniku s proslijeđenim korisničkim imenom.
	 * @param icao ICAO aerodroma kojega treba početi pratiti.
	 * @return Uspjeh operacije.
	 */
	@WebMethod
	public boolean dodajAerodromPreuzimanje(
			@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton,
			@WebParam(name = "icao") String icao) {

		boolean uspjeh;

		try {
			int zetonBrojcani = Integer.parseInt(zeton);
			PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik(korisnik, "", zetonBrojcani);
			AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(kontekst);
			uspjeh = aerodromiKlijent.unesiAerodromZaPratiti(prijavljeniKorisnik, icao);
			if (uspjeh) {
				posiljateljPoruke.noviPoruka("Korisnik [" + korisnik + "] dodao aerodrom [" + icao + "] u praćenje");
			}
		} catch (Exception e) {
			e.printStackTrace();
			uspjeh = false;
		}

		return uspjeh;
	};

	private List<AvionLeti> dohvatPolazakaPoKriteriju(
			String korisnik,
			String zeton,
			String icao,
			String oznakaOd,
			String oznakaDo,
			VrsteVremenskogRaspona vrsta) {
		List<AvionLeti> polasci = null;

		try {
			int zetonBrojcani = Integer.parseInt(zeton);
			PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik(korisnik, "", zetonBrojcani);
			AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(kontekst);
			polasci = aerodromiKlijent.dajPolaske(prijavljeniKorisnik, icao, oznakaOd, oznakaDo, vrsta);
		} catch (Exception ex) {
			Logger.getLogger(WsAerodromi.class.getName()).log(Level.SEVERE,
					"Neuspio dohvat polazaka po čitljivim datumima: " + oznakaOd + " i " + oznakaDo, ex);
		}

		return polasci;
	}
}
