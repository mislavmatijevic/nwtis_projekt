package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.ws;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti.AerodromiKlijent;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;

@WebService(serviceName = "meteo")
public class WsMeteo {
	@Resource
	private WebServiceContext wsContext;
	@Inject
	ServletContext kontekst;

	/** 
	 * Vraća meteorološke podatke za aerodrom.
	 * @param icao Oznaka aerodroma za kojega se traže meteo podaci.
	 * @return MeteoPodaci
	 */
	@WebMethod
	public MeteoPodaci dajMeteo(@WebParam(name = "icao") String icao) {
		MeteoPodaci meteoPodaci = null;

		Aerodrom aerodrom = null;
		AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(kontekst);
		aerodrom = aerodromiKlijent.dohvatiJedanAerodrom(icao);

		if (aerodrom != null) {
			Lokacija lokacija = aerodrom.getLokacija();

			Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
			String apiKey = konfig.dajPostavku("OpenWeatherMap.apikey");

			OWMKlijent owmKlijent = new OWMKlijent(apiKey);
			try {
				meteoPodaci = owmKlijent.getRealTimeWeather(lokacija.getLatitude(), lokacija.getLongitude());
			} catch (NwtisRestIznimka e) {
				e.printStackTrace();
				return null;
			}
		}

		return meteoPodaci;
	}
}
