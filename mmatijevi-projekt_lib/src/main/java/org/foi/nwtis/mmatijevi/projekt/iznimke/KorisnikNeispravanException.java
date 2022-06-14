package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class KorisnikNeispravanException extends Exception {
    public KorisnikNeispravanException() {
        super("Korisnički objekt nije u ispravnom formatu");
    }
}
