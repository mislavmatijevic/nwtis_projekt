package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class KorisnikNePostojiException extends Exception {
    public KorisnikNePostojiException(String korime) {
        super("Korisnik " + korime + " nije pronađen u bazi");
    }
}
