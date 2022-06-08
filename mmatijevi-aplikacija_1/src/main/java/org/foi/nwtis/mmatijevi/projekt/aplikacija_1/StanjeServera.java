package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

/**
 * Klasa po uzorku dizajna singleton.
 * Vraća trenutno stanje/status servera.
 */
public class StanjeServera {
    private StatusEnum status = StatusEnum.hibernira;

    private static StanjeServera stanjeServera = null;

    private StanjeServera() {
    }

    synchronized public static StanjeServera dajInstancu() {
        if (stanjeServera == null) {
            stanjeServera = new StanjeServera();
        }
        return stanjeServera;
    }

    synchronized public void promjeniStanje() {
        switch (this.status) {
            case hibernira:
                status = StatusEnum.inicijaliziran;
                break;
            case inicijaliziran:
                status = StatusEnum.aktivan;
                break;
            case aktivan: {
                status = StatusEnum.hibernira;
                break;
            }
        }
    }

    public StatusEnum dajStatus() {
        return this.status;
    }
}