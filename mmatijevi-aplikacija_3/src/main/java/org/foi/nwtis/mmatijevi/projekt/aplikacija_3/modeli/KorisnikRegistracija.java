package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import org.foi.nwtis.podaci.Korisnik;

import jakarta.json.bind.annotation.JsonbProperty;

public class KorisnikRegistracija extends Korisnik {
    @JsonbProperty("korisnicko_ime")
    private String korIme;

    public KorisnikRegistracija() {
        super();
    }

    public KorisnikRegistracija(String korime, String ime, String prezime, String lozinka, String email) {
        super(korime, ime, prezime, lozinka, email);
    }
}
