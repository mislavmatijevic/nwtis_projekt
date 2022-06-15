package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti;

import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.usluge.PristupServisu;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
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
}
