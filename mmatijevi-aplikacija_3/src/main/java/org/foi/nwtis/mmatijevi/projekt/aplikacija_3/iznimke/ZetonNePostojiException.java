package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke;

public class ZetonNePostojiException extends Exception {
    public ZetonNePostojiException(int zetonOznaka) {
        super("Žeton s oznakom " + zetonOznaka + " nije bio izdan");
    }

    public ZetonNePostojiException(String korime) {
        super("Žeton nije generiran za korisnika " + korime);
    }
}
