package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;

/**
 * <h3>Klasa koja provjerava mrežna vrata</h3>
 * <p>Ova klasa omogućuje provjeru dostupnosti određenih mrežnih vrata (port).
 */
public class ProvjeravacPorta {

    /** 
     * Provjerava je li port (mrežna vrata) dostupan za zauzimanje i korištenje. Ispisuje pogreške ako naiđe na njih.
     * @param port Brojčana oznaka porta, npr. 8000
     * @return <strong>true</strong> = Dostupan/<strong>false</strong> = Nedostupan
     */
    public static boolean provjeriDostupnostPorta(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ProvjeravacPorta.class.getName()).log(Level.SEVERE, "Pogreška u provjeravanju porta", ex);
            Terminal.pozorIspis("Pogreška u provjeravanju porta: " + ex.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ex) {
                    Logger.getLogger(ProvjeravacPorta.class.getName()).log(Level.SEVERE,
                            "Velika pogreška na mrežnoj utičnici", ex);
                    Terminal.greskaIspis("Velika pogreška na mrežnoj utičnici. Provjeriti hardver.");
                }
            }
        }
        return false;
    }
}
