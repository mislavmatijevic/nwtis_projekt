package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class NovaOznakaNedostupnaException extends Exception {
    public NovaOznakaNedostupnaException() {
        super("Neuspjelo postavljanje posljednje oznake žetona!");
    }
}