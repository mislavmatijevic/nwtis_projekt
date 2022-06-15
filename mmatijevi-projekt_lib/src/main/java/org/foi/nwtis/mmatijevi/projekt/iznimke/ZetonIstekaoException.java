package org.foi.nwtis.mmatijevi.projekt.iznimke;

public class ZetonIstekaoException extends Exception {
    public ZetonIstekaoException() {
        super("Vaša sesija je istekla. Ponovite prijavu.");
    }
}
