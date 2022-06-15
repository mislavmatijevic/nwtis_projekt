package org.foi.nwtis.mmatijevi.projekt.modeli;

import org.foi.nwtis.podaci.Korisnik;

import jakarta.json.bind.annotation.JsonbTransient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KorisnikPrikaz extends Korisnik {
    @JsonbTransient
    private String lozinka;

    public KorisnikPrikaz(String korIme, String ime, String prezime, String email) {
        super(korIme, ime, prezime, "", email);
    }
}
