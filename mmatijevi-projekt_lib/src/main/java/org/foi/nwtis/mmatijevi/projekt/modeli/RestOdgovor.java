package org.foi.nwtis.mmatijevi.projekt.modeli;

import lombok.Getter;
import lombok.Setter;

/**
 * Klasa za klasičan JSON odgovor s poslužitelja.
 */
@Getter
@Setter
public class RestOdgovor {
    private boolean uspjeh;
    private String poruka;

    public RestOdgovor(boolean uspjeh, String poruka) {
        super();
        this.uspjeh = uspjeh;
        this.poruka = poruka;
    }
}
