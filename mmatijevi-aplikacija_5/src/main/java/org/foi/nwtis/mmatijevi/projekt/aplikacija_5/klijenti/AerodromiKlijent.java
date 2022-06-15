package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.iznimke.AerodromVecPracenException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.usluge.ParserRestOdgovoraUzPodatke;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AerodromiKlijent extends PristupServisu {
    public AerodromiKlijent(ServletContext kontekst) {
        super("aerodromi", kontekst);
    }

    public enum VrsteVremenskogRaspona {
        DATUM,
        TIMESTAMP
    }

    public List<AvionLeti> dajPolaske(PrijavljeniKorisnik korisnik, String icao, String danOd, String danDo,
            VrsteVremenskogRaspona vrsta)
            throws ZetonIstekaoException {
        Client client = ClientBuilder.newClient();

        Object rasponOd = vrsta == VrsteVremenskogRaspona.TIMESTAMP ? Integer.parseInt(danOd) : danOd;
        Object rasponDo = vrsta == VrsteVremenskogRaspona.TIMESTAMP ? Integer.parseInt(danDo) : danDo;

        WebTarget webResource = client.target(this.odredisnaAdresa)
                .path(icao)
                .path("polasci")
                .queryParam("vrsta", vrsta.ordinal())
                .queryParam("od", rasponOd)
                .queryParam("do", rasponDo);
        Response restOdgovor = webResource.request()
                .header("Accept", "application/json")
                .header("korisnik", korisnik.getKorime())
                .header("zeton", korisnik.getZeton())
                .get();

        List<AvionLeti> polasci = null;
        if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
            String jsonOdgovor = restOdgovor.readEntity(String.class);
            Gson gson = new Gson();
            polasci = Arrays.asList(gson.fromJson(jsonOdgovor, AvionLeti[].class));
        } else if (restOdgovor.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
            throw new ZetonIstekaoException();
        }

        return polasci;
    }

    /** 
     * Unosi aerodrom po ICAO oznaci u bazu kao aerodrom za praćenje.
     * @param veza Spremna veza na bazu, uspostavljena statičkom metodom ove klase <pre>dohvatiVezu</pre>
     * @param aerodrom Aerodrom kojemu je dovoljno da je unesen samo 'icao'.
     * @return Ako ICAO oznaka ne pripada niti jednome aerodromu u bazi, vraća se <pre>false</pre>
     */
    public boolean unesiAerodromZaPratiti(PrijavljeniKorisnik korisnik, String icao)
            throws ClassNotFoundException, SQLException, AerodromVecPracenException {

        boolean uspjeh = true;

        try {
            List<Aerodrom> vecPraceniAerodromi = dohvatiPraceneAerodrome(korisnik);
            for (Aerodrom aerodrom : vecPraceniAerodromi) {
                if (aerodrom.getIcao().equals(icao)) {
                    uspjeh = false;
                    break;
                }
            }
        } catch (Exception ex) {
        }

        Client client = ClientBuilder.newClient();

        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setIcao(icao);

        WebTarget webResource = client.target(this.odredisnaAdresa);
        Response restOdgovor = webResource.request()
                .header("Accept", "application/json")
                .header("korisnik", korisnik.getKorime())
                .header("zeton", korisnik.getZeton())
                .post(Entity.entity(aerodrom, MediaType.APPLICATION_JSON));

        if (restOdgovor.getStatus() != Response.Status.CREATED.getStatusCode()) {
            uspjeh = false;
        }

        return uspjeh;
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