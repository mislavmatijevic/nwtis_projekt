package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class NovaOznakaNedostupnaException extends Exception {
    public NovaOznakaNedostupnaException() {
        super("Neuspjelo postavljanje posljednje oznake žetona!");
    }
}