package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;

public class Aplikacija {
    private static Konfiguracija konfig = null;

    public static void main(String[] args) {

        String lokacijaKonfiguracije;

        if (args.length != 1) {
            Terminal.greskaIspis(
                    "Konfiguracijska datoteka nije proslijeđena, koristim klasičnu \"konfiguracija.json\"!");
            lokacijaKonfiguracije = "konfiguracija.json";
        } else {
            lokacijaKonfiguracije = args[0];
        }

        try {
            konfig = ucitajPodatke(lokacijaKonfiguracije);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(Aplikacija.class.getName()).log(Level.WARNING, null, ex);
            Terminal.greskaIspis("Konfiguracija nije ispravna! " + ex.getMessage());
            return;
        }

        int port = Integer.parseInt(konfig.dajPostavku("port"));

        if (!ProvjeravacPorta.provjeriDostupnostPorta(port)) {
            Terminal.greskaIspis(
                    "PORT " + port + " je zauzet. Promijenite ga u konfiguracijskoj datoteci i pokušajte iznova.");
            return;
        }

        int maksCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
        int maksDretvi = Integer.parseInt(konfig.dajPostavku("maks.dretvi"));

        Server server = new Server(port, maksCekaca, maksDretvi);
        server.cekajZahtjeve();
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
