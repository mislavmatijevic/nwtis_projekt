package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class KorisnikNePostojiException extends Exception {
    public KorisnikNePostojiException(String korime) {
        super("Korisnik " + korime + " nije pronađen u bazi");
    }
}
