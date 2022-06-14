package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class AerodromVecPracenException extends Exception {
    public AerodromVecPracenException(String icao) {
        super("Aerodrom s oznakom '" + icao + "' već jest u listi praćenja!");
    }
}
