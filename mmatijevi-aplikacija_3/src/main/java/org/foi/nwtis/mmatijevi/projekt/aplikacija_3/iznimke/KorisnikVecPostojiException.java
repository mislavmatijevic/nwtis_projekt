package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class KorisnikVecPostojiException extends Exception {
    public KorisnikVecPostojiException(String korime) {
        super("Već postoji korisnik s korisničkim imenom '" + korime + "'");
    }
}
