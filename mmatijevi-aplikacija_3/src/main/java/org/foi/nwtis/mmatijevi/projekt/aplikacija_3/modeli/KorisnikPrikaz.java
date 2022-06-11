package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import org.foi.nwtis.podaci.Korisnik;

import jakarta.json.bind.annotation.JsonbTransient;

public class KorisnikPrikaz extends Korisnik {
    @JsonbTransient
    private String lozinka;

    public KorisnikPrikaz(String korIme, String ime, String prezime, String lozinka, String email) {
        super(korIme, ime, prezime, lozinka, email);
    }
}
