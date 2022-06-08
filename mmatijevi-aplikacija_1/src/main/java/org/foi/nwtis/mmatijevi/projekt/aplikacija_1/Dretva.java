package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

public class Dretva implements Runnable {

    private OutputStreamWriter osw = null;
    private String komanda = null;

    public Dretva(OutputStreamWriter osw, String komanda) {
        this.osw = osw;
        this.komanda = komanda;
    }

    @Override
    public void run() {
        if (osw == null || komanda == null) {
            return;
        }

        switch (StanjeServera.dajInstancu().dajStatus()) {
            case hibernira:
                if (komanda.equals("INIT")) {
                    StanjeServera.dajInstancu().promjeniStanje();
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 01 Komanda \"" + komanda + "\" nije ispravna dok poslužitelj hibernira.");
                }
                break;
            case inicijaliziran:
                if (komanda.equals("LOAD")) {
                    StanjeServera.dajInstancu().promjeniStanje();
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 02 Komanda \"" + komanda + "\" nije ispravna dok je poslužitelj inicijaliziran.");
                }
                break;
            case aktivan:
                if (komanda.equals("DISTANCE")) {
                } else if (komanda.equals("CLEAR")) {
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 03 Komanda \"" + komanda + "\" nije ispravna dok je poslužitelj aktivan.");
                }
                break;
        }
    }

    /** 
     * Vraća klijentu odgovor koristeći tok klijentove utičnice.
     * <p>Odgovornost je na pozivatelju metode da osigura ispravnu sintaksu odgovora,
     * ova metoda brine se samo za slanje odgovora preko TCP veze.
     * <p>Metoda ispisuje pogrešku ako do nje dođe.
     * @param osw Tok podataka klijentove utičnice.
     * @param odg Odgovor za klijenta u obliku znakovnog niza.
     */
    public void posaljiOdgovor(OutputStreamWriter osw, String odgovor) {
        try {
            osw.write(odgovor);
            osw.flush();
            osw.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Neuspjelo slanje odgovora", ex);
            Terminal.greskaIspis("Neuspjelo slanje odgovora: " + ex.getMessage());
        }
    }
}
