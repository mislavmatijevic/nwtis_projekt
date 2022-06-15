package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.ws;

import java.text.SimpleDateFormat;
import java.util.List;

import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	@Resource
	private WebServiceContext wsContext;

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
		return null;
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
		return null;
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
		return false;
	}
}
