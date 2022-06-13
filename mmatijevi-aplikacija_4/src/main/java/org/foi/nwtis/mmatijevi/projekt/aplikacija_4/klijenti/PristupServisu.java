package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

public class PristupServisu {

    private Konfiguracija konfig = null;
    public String odredisnaAdresa;

    @Inject
    PrijavljeniKorisnik podaciKorisnik;

    public PristupServisu(String odredisnaTocka, ServletContext context) {
        this.konfig = (Konfiguracija) context.getAttribute("postavke");
        this.odredisnaAdresa = this.konfig.dajPostavku("adresa.aplikacija_3");
        this.odredisnaAdresa += odredisnaTocka;
    }
}
