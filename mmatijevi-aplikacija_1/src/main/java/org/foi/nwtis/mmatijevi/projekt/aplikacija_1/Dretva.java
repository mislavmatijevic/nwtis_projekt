package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

public class Dretva extends Thread {
    private Socket veza = null;
    private Server roditelj = null;

    public Dretva(Server roditelj, Socket veza) {
        super();
        this.veza = veza;
        this.roditelj = roditelj;
    }

    @Override
    public void run() {
        super.run();

        if (veza == null) {
            return;
        }

        try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(),
                Charset.forName("UTF-8"));
                OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
                        Charset.forName("UTF-8"));) {

            StringBuilder ulazno = new StringBuilder();
            while (true) {
                int i = isr.read();
                if (i == -1) {
                    break;
                }
                ulazno.append((char) i);
            }

            String komanda = ulazno.toString();

            Terminal.infoIspis("Dobivena komanda: \"" + komanda + "\"");
            this.veza.shutdownInput();

            if (komanda.equals("QUIT")) {
                posaljiOdgovor(osw, "OK");
                this.roditelj.ugasiPosluzitelja();
                return;
            } else if (komanda.equals("STATUS")) {
                int oznakaStatusa = StanjeServera.dajInstancu().dajStatus().ordinal();
                posaljiOdgovor(osw, "OK " + oznakaStatusa);
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                definirajOdgovor(osw, komanda);
            }
        } catch (IOException ex) {
            Logger.getLogger(Dretva.class.getName()).log(Level.SEVERE, "Problem pri zauzimanju utičnice",
                    ex);
            Terminal.greskaIspis("Problem pri zauzimanju utičnice: " + ex.getMessage());
        }

        this.roditelj.prijaviGasenjeDretve(this);
        return;
    }

    private void definirajOdgovor(OutputStreamWriter osw, String komanda) {
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
                    posaljiOdgovor(osw, "DISTANCE.");
                } else if (komanda.equals("CLEAR")) {
                    posaljiOdgovor(osw, "CLEAR");
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
            Logger.getLogger(Dretva.class.getName()).log(Level.SEVERE, "Neuspjelo slanje odgovora", ex);
            Terminal.greskaIspis("Neuspjelo slanje odgovora: " + ex.getMessage());
        }
    }

    @Override
    public void interrupt() {
        Logger.getLogger(Dretva.class.getName()).info("Dretva se gasi");
        super.interrupt();
        this.roditelj.prijaviGasenjeDretve(this);
    }
}
