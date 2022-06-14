package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli;

import java.io.Serializable;

import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrijavljeniKorisnik implements Serializable {
    private String korime = null;
    private Zeton zeton = null;
}
