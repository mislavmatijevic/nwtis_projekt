package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

public class Server implements ServerSucelje {
    private boolean aktivan = true;

    private int port;
    private int maksCekaca;
    private int maksCekanje;
    private int maksDretvi;

    private List<Dretva> listaDretvi;
    private Semaphore obustavaStvaranjaDretvi;

    /**
     * Za instanciranje objekta potrebna su mrežna vrata (port) na kojima se čeka te maksimalan broj klijenata - čekača.
     * @param port Mrežna vrata (port), npr. <i>8000</i>
     * @param maksCekaca Maksimalan broj klijenata - čekača, npr. <i>10</i>
     * @param maksDretvi Maksimalan broj dretvi.
     * @param maksCekanje Maksimalno čekanje za paljenje.
     * @param maksDretvi Servis dretvi koji stvara nove dretve po potrebi.
     */
    public Server(int port, int maksCekaca, int maksCekanje, int maksDretvi) {
        this.port = port;
        this.maksCekaca = maksCekaca;
        this.maksCekanje = maksCekanje;
        this.maksDretvi = maksDretvi;

        obustavaStvaranjaDretvi = new Semaphore(maksDretvi);
        listaDretvi = new ArrayList<>(maksDretvi);
    }

    /**
     * Glavna metoda koju objekt klase obavlja sve dok ne dobije signal za inicijalizaciju.
     * <p>
     * <p>Ova metoda osigurava da ovaj poslužitelj primi i obradi sve klijente, na višedretven način.
     * <p>Odgovornost ove metode je da aktivira dretvu koja će obraditi zahtjev.
     */
    public void cekajZahtjeve() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Terminal.pozorIspis("Gašenje...");
                int brojacUgasenihDretvi = 0;
                for (Dretva dretva : listaDretvi) {

                    try {
                        dretva.join(500);
                        brojacUgasenihDretvi++;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Prekid pri gašenju dretve!", ex);
                        Terminal.pozorIspis("Gašenje dretve obustavljeno!");
                    }
                }
                Terminal.uspjehIspis("Ugašeno " + brojacUgasenihDretvi + " dretvi!");
            }
        }));

        try (ServerSocket ss = new ServerSocket(this.port, this.maksCekaca)) {
            while (this.aktivan) {
                Terminal.infoIspis("Čekanje korisnika na vratima " + this.port);
                Socket veza = ss.accept();
                Dretva dretva = new Dretva(this, veza);
                listaDretvi.add(dretva);
                dretva.start();
                Terminal.uspjehIspis("" + obustavaStvaranjaDretvi.availablePermits());
                try {
                    obustavaStvaranjaDretvi.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Prekid pri čekanju dretvi!", ex);
                    Terminal.pozorIspis("Čekanje dretve obustavljeno!");
                }
                Terminal.uspjehIspis("" + obustavaStvaranjaDretvi.availablePermits());
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Problem pri uspostavi komunikacije", ex);
            Terminal.greskaIspis("Problem pri uspostavi komunikacije: " + ex.getMessage());
        }
    }

    @Override
    public void prijaviGasenjeDretve(Dretva objektDretve) {
        obustavaStvaranjaDretvi.release();
        listaDretvi.remove(objektDretve);
    }

    public void ugasiPosluzitelja() {
        this.aktivan = false;
        Terminal.pozorIspis("Dobiven signal za gašenje glavnog poslužitelja!");
        System.exit(0);
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
