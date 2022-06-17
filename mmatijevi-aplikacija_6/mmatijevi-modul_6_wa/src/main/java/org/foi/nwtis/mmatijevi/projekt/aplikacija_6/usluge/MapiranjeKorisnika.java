package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.usluge;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.podaci.Korisnik;

public class MapiranjeKorisnika {
    public static Korisnici izKorisnikaUKorisnici(Korisnik korisnikGlobalni) {
        Korisnici korisnikLokalni = new Korisnici();
        korisnikLokalni.setKorisnik(korisnikGlobalni.getKorIme());
        korisnikLokalni.setIme(korisnikGlobalni.getIme());
        korisnikLokalni.setPrezime(korisnikGlobalni.getPrezime());
        korisnikLokalni.setLozinka(korisnikGlobalni.getLozinka());
        korisnikLokalni.setEmail(korisnikGlobalni.getEmail());
        return korisnikLokalni;
    }
}
