package org.foi.nwtis.mmatijevi.projekt.odgovori;

import org.foi.nwtis.podaci.Aerodrom;

/**
 * Napredna klasa za JSON odgovor s poslužitelja koji sadržava i neki objekt.
 */
public class RestOdgovorAerodrom extends RestOdgovorUzPodatke<Aerodrom> {
    public RestOdgovorAerodrom(boolean uspjeh, String poruka, Aerodrom aerodrom) {
        super(uspjeh, poruka, aerodrom);
    }
}
