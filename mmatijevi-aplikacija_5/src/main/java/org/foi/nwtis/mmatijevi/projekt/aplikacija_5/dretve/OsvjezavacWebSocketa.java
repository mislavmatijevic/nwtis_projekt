package org.foi.nwtis.mmatijevi.projekt.aplikacija_5.dretve;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.klijenti.AerodromiKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_5.wsock.Info;
import org.foi.nwtis.podaci.Aerodrom;

import jakarta.servlet.ServletContext;

public class OsvjezavacWebSocketa extends Thread {

    Info info;
    int interval;
    boolean kraj = false;
    AerodromiKlijent aerodromiKlijent;

    /**
     * @param info Aktivni objekt klase WebSocketa.
     * @param intervalOsvjezivanja Koliko često poslati poruku, u sekundama.
     * @param kontekst Kontekst aplikacije.
     */
    public OsvjezavacWebSocketa(Info info, int intervalOsvjezivanja, ServletContext kontekst) {
        super();
        this.info = info;
        this.interval = intervalOsvjezivanja;
        aerodromiKlijent = new AerodromiKlijent(kontekst);
    }

    /**
     * Metoda za pokretanje dretve
     */
    @Override
    public void run() {
        while (!kraj) {

            try {
                sleep(interval * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(OsvjezavacWebSocketa.class.getName()).log(Level.WARNING, "Čekanje nije dovršeno", ex);
            }

            List<Aerodrom> aerodromi = aerodromiKlijent.dohvatiPraceneAerodrome();
            String poruka = "Trenutno vrijeme na poslužitelju: " + new Date().toString() +
                    "-- Broj praćenih aerodroma: " + aerodromi.size();
            info.dajMeteo(poruka);
        }
    }

    /**
     * Metoda za prekidanje rada dretve
     */
    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }
}
