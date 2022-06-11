package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Ova klasa sadržava informacije o letu.
 */
@Getter
@Setter
@AllArgsConstructor
public class InformacijeLeta {
    private String icaoPolazak;
    private String icaoDolazak;
    private Date pocetakLeta;
    private Date krajLeta;
    private String callsign;
}
