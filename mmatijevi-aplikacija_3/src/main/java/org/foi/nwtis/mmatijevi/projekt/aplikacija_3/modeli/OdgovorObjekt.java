package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Napredna klasa za JSON odgovor s poslužitelja koji sadržava i neki objekt.
 */
@Getter
@Setter
public class OdgovorObjekt<T> extends Odgovor {
    @JsonbProperty("podaci")
    private T objektKlase;

    public OdgovorObjekt(boolean uspjeh, String poruka, T objekt) {
        super(uspjeh, poruka);
        this.objektKlase = objekt;
    }
}
