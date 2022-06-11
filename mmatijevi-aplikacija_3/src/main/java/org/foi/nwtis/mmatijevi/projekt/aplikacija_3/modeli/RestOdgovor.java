package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import lombok.Getter;
import lombok.Setter;

/**
 * Klasa za klasičan JSON odgovor s poslužitelja.
 */
@Getter
@Setter
public class RestOdgovor {
    private String uspjeh;
    private String poruka;

    public RestOdgovor(boolean uspjeh, String poruka) {
        super();
        this.poruka = poruka;
        this.uspjeh = uspjeh ? "Uspješno izvršeno" : "Operacija nije izvršena";
    }
}
