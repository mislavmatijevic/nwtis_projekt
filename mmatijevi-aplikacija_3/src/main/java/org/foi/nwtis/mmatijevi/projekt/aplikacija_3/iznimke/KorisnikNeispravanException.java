package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class KorisnikNeispravanException extends Exception {
    public KorisnikNeispravanException() {
        super("Korisnički objekt nije u ispravnom formatu");
    }
}
