package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.iznimke.AerodromVecPracenException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorAerodrom;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorPodaciLetova;
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
            polasci = gson.fromJson(jsonOdgovor, RestOdgovorPodaciLetova.class).getPodaci();
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

    /**
     * Dohvaća praćene aerodrome u ime globalnog korisnika sustava.
     * @return Lista praćenih aerodroma ILI prazna lista ako je došlo do pogreške.
     */
    public List<Aerodrom> dohvatiPraceneAerodrome() {
        PrijavaKlijent prijavaKlijent = new PrijavaKlijent(kontekst);
        int zeton = prijavaKlijent.prijaviSistemskogKorisnika().getZeton();
        List<Aerodrom> praceni;
        try {
            praceni = dohvatiPraceneAerodrome(new PrijavljeniKorisnik(sustavKorisnik, sustavLozinka, zeton));
        } catch (ZetonIstekaoException e) {
            e.printStackTrace();
            praceni = new ArrayList<>(0);
        }
        return praceni;
    }

    /**
     * Dohvaća praćene aerodrome.
     * @param korisnik Objekt korisnika koji je zahtijevao operaciju.
     * @return Lista praćenih aerodroma.
     * @throws ZetonIstekaoException U slučaju da je žeton istekao.
     */
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

    public Aerodrom dohvatiJedanAerodrom(String icao) {
        Client client = ClientBuilder.newClient();

        PrijavaKlijent prijavaKlijent = new PrijavaKlijent(kontekst);
        int zeton = prijavaKlijent.prijaviSistemskogKorisnika().getZeton();

        WebTarget webResource = client.target(this.odredisnaAdresa).path(icao);
        Response restOdgovor = webResource.request()
                .header("Accept", "application/json")
                .header("korisnik", sustavKorisnik)
                .header("zeton", zeton)
                .get();

        Aerodrom aerodrom = null;
        if (restOdgovor.getStatus() == Response.Status.OK.getStatusCode()) {
            String jsonOdgovor = restOdgovor.readEntity(String.class);
            Gson gson = new Gson();
            aerodrom = gson.fromJson(jsonOdgovor, RestOdgovorAerodrom.class).getPodaci();
        } else {
            Logger.getLogger(AerodromiKlijent.class.getName()).log(Level.WARNING, "Neuspio dohvat aerodroma " + icao);
        }

        return aerodrom;
    }
}
