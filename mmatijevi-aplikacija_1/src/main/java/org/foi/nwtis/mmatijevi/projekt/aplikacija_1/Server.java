package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

public class Server {
    private boolean aktivan = true;

    private int port;
    private int maksCekaca;
    private int maksCekanje;
    private Socket veza = null;
    private ExecutorService servisDretvi;

    /**
     * Za instanciranje objekta potrebna su mrežna vrata (port) na kojima se čeka te maksimalan broj klijenata - čekača.
     * @param port Mrežna vrata (port), npr. <i>8000</i>
     * @param maksCekaca Maksimalan broj klijenata - čekača, npr. <i>10</i>
     * @param maksDretvi Maksimalan broj dretvi.
     * @param maksCekanje Maksimalno čekanje za paljenje.
     * @param servisDretvi Servis dretvi koji stvara nove dretve po potrebi.
     */
    public Server(int port, int maksCekaca, int maksCekanje, ExecutorService servisDretvi) {
        this.port = port;
        this.maksCekaca = maksCekaca;
        this.maksCekanje = maksCekanje;
        this.servisDretvi = servisDretvi;
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
                servisDretvi.shutdown();
                while (true) {
                    try {
                        Terminal.infoIspis("Čekanje da se servisDretvi ugasi...");
                        if (servisDretvi.awaitTermination(5, TimeUnit.SECONDS)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                    }
                }
                Terminal.infoIspis("Ugašeno sve");
            }
        }));

        try (ServerSocket ss = new ServerSocket(this.port, this.maksCekaca)) {
            while (this.aktivan) {
                Terminal.infoIspis("Čekanje korisnika na vratima " + this.port);
                this.veza = ss.accept();

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
                        ugasiPosluzitelja();
                    } else if (komanda.equals("STATUS")) {
                        int oznakaStatusa = StanjeServera.dajInstancu().dajStatus().ordinal();
                        posaljiOdgovor(osw, "OK " + oznakaStatusa);
                    } else {
                        Dretva novaDretva = new Dretva(osw, komanda);
                        servisDretvi.execute(novaDretva);
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Problem pri zauzimanju utičnice",
                            ex);
                    Terminal.greskaIspis("Problem pri zauzimanju utičnice: " + ex.getMessage());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Problem pri uspostavi komunikacije", ex);
            Terminal.greskaIspis("Problem pri uspostavi komunikacije: " + ex.getMessage());
        }
    }

    private void ugasiPosluzitelja() throws IOException {
        this.aktivan = false;
        Terminal.pozorIspis("Gašenje!");
        this.servisDretvi.shutdown();
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
