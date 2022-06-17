package org.foi.nwtis.mmatijevi.projekt.odgovori;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikPrikaz;

public class RestOdgovorKorisnici extends RestOdgovorUzPodatke<List<KorisnikPrikaz>> {
    public RestOdgovorKorisnici(boolean uspjeh, String poruka, List<KorisnikPrikaz> aerodrom) {
        super(uspjeh, poruka, aerodrom);
    }
}