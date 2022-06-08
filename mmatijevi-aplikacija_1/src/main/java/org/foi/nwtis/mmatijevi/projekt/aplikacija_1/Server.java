package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

public class Server {
    private int port;
    private int maksCekaca;
    private int maksCekanje;
    private int maksDretvi;
    private Socket veza = null;

    /**
     * Za instanciranje objekta potrebna su mrežna vrata (port) na kojima se čeka te maksimalan broj klijenata - čekača.
     * @param port Mrežna vrata (port), npr. <i>8000</i>
     * @param maksCekaca Maksimalan broj klijenata - čekača, npr. <i>10</i>
     * @param maksDretvi Maksimalan broj dretvi.
     * @param maksCekanje Maksimalno čekanje za paljenje.
     */
    public Server(int port, int maksCekaca, int maksCekanje, int maksDretvi) {
        this.port = port;
        this.maksCekaca = maksCekaca;
        this.maksCekanje = maksCekanje;
        this.maksDretvi = maksDretvi;
    }

    /**
     * Glavna metoda koju objekt klase obavlja sve dok ne dobije signal za prekid.
     * <p>
     * <p>Ova metoda osigurava da ovaj poslužitelj primi i obradi sve klijente, ali na jednodretven način.
     * <p>Odgovornost ove metode je da se pronađe odgovarajući odgovor klijentu na njegovu tekstualnu komandu.
     */
    public void cekajINIT() {
        try (ServerSocket ss = new ServerSocket(this.port, this.maksCekaca)) {
            while (true) {
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
}
