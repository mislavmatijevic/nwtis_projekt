package org.foi.nwtis.mmatijevi.projekt.odgovori;

import java.util.List;

import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Napredna klasa za JSON odgovor s poslužitelja koji sadržava i neki objekt.
 */
public class RestOdgovorPodaciLetova extends RestOdgovorUzPodatke<List<AvionLeti>> {
    public RestOdgovorPodaciLetova(boolean uspjeh, String poruka, List<AvionLeti> letovi) {
        super(uspjeh, poruka, letovi);
    }
}
