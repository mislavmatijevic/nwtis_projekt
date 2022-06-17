package org.foi.nwtis.mmatijevi.projekt.odgovori;

import java.util.List;

import org.foi.nwtis.podaci.Korisnik;

public class RestOdgovorKorisnici extends RestOdgovorUzPodatke<List<Korisnik>> {
    public RestOdgovorKorisnici(boolean uspjeh, String poruka, List<Korisnik> aerodrom) {
        super(uspjeh, poruka, aerodrom);
    }
}