package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli;

import jakarta.enterprise.context.SessionScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SessionScoped
@Getter
@AllArgsConstructor
public class PrijavljeniKorisnik {
    private String korime;
    private String zeton;
}
