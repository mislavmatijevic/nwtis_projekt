package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrijavljeniKorisnik implements Serializable {
    private String korime = null;
    private String lozinka = null;
    private int zeton;
}
