package org.foi.nwtis.mmatijevi.projekt.usluge;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 * Priprema klasu klijenta i usmjerava ju na pravu putanju.
 * Zbog ove klase, klasa klijent na jednostavan način dobije svoju
 * putanju, konfiguraciju, kontekst i podatke o globalnom korisniku sustava.
 */
public abstract class PristupServisu {

    protected ServletContext kontekst = null;
    protected Konfiguracija konfig = null;
    protected String sustavKorisnik;
    protected String sustavLozinka;
    public String odredisnaAdresa;

    @Inject
    PrijavljeniKorisnik podaciKorisnik;

    /**
     * Priprema za rad klijenta.
     * @param odredisnaTocka // Primjeri: korisnici, provjere, aerodromi; sve što ide nakon /api/.
     * @param kontekst // Kontekst preko kojega se dobiva prava putanja.
     */
    public PristupServisu(String odredisnaTocka, ServletContext kontekst) {
        this.kontekst = kontekst;
        this.konfig = (Konfiguracija) this.kontekst.getAttribute("postavke");

        this.sustavKorisnik = this.konfig.dajPostavku("sustav.korisnik");
        this.sustavLozinka = this.konfig.dajPostavku("sustav.lozinka");

        this.odredisnaAdresa = this.konfig.dajPostavku("adresa.aplikacija_3");
        this.odredisnaAdresa += "/" + odredisnaTocka;
    }
}
