package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class AerodromVecPracenException extends Exception {
    public AerodromVecPracenException(String icao) {
        super("Aerodrom s oznakom '" + icao + "' već jest u listi praćenja!");
    }
}
