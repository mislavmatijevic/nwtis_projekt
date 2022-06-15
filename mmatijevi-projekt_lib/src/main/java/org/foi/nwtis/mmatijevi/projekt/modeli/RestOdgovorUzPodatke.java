package org.foi.nwtis.mmatijevi.projekt.modeli;

import lombok.Getter;
import lombok.Setter;

/**
 * Napredna klasa za JSON odgovor s poslužitelja koji sadržava i neki objekt.
 */
@Getter
@Setter
public class RestOdgovorUzPodatke<T> extends RestOdgovor {
    private T podaci;

    public RestOdgovorUzPodatke(boolean uspjeh, String poruka, T podaci) {
        super(uspjeh, poruka);
        this.podaci = podaci;
    }
}
