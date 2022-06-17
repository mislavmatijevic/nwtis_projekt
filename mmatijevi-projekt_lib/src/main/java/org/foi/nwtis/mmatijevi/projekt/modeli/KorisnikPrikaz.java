package org.foi.nwtis.mmatijevi.projekt.modeli;

import org.foi.nwtis.podaci.Korisnik;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KorisnikPrikaz extends Korisnik {
    public KorisnikPrikaz(String korIme, String ime, String prezime, String email) {
        super(korIme, ime, prezime, "", email);
    }
}
