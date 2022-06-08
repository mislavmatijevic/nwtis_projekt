package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;

public class Aplikacija {
    private static Konfiguracija konfig = null;

    public static void main(String[] args) {
        if (args.length != 1) {
            Terminal.greskaIspis("Broj argumenata nije 1. Proslijedite konfiguracijsku datoteku!");
            Terminal.pozorIspis("Primjer pokretanja programa: \"ServerMeteo NWTiS_mmatijevi_1.txt\"");
            return;
        }

        try {
            konfig = ucitajPodatke(args[0]);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(Aplikacija.class.getName()).log(Level.WARNING, null, ex);
            Terminal.greskaIspis("Konfiguracija nije ispravna! " + ex.getMessage());
            return;
        }

        int port = Integer.parseInt(konfig.dajPostavku("port"));
        int maksCekanje = Integer.parseInt(konfig.dajPostavku("maks.cekanje"));
        int maksCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
        int maksDretvi = Integer.parseInt(konfig.dajPostavku("maks.dretvi"));

        if (!ProvjeravacPorta.provjeriDostupnostPorta(port)) {
            Terminal.greskaIspis(
                    "PORT " + port + " je zauzet. Promijenite ga u konfiguracijskoj datoteci i pokušajte iznova.");
            return;
        }

        Server server = new Server(port, maksCekaca, maksCekanje, maksDretvi);
        server.cekajINIT();
    }

    /** 
     * Učitava konfiguracijske podatke.
     * @param nazivDatoteke Datoteka s konfiguracijskim podacima.
     * @return Konfiguracija iz datoteke
     * @throws NeispravnaKonfiguracija Pogreška pri čitanju konfiguracije.
     */
    public static Konfiguracija ucitajPodatke(String nazivDatoteke) throws NeispravnaKonfiguracija {
        return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
    }
}
