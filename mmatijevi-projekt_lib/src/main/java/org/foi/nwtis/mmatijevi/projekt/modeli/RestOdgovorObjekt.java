package org.foi.nwtis.mmatijevi.projekt.modeli;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Napredna klasa za JSON odgovor s poslužitelja koji sadržava i neki objekt.
 */
@Getter
@Setter
public class RestOdgovorObjekt<T> extends RestOdgovor {
    @JsonbProperty("podaci")
    private T objektKlase;

    public RestOdgovorObjekt(boolean uspjeh, String poruka, T objekt) {
        super(uspjeh, poruka);
        this.objektKlase = objekt;
    }
}
