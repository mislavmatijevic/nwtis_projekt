package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class KorisnikVecPostojiException extends Exception {
    public KorisnikVecPostojiException(String korime) {
        super("Već postoji korisnik s korisničkim imenom '" + korime + "'");
    }
}
