package org.foi.nwtis.mmatijevi.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Dretva extends Thread {
    private Socket veza = null;
    private Server roditelj = null;

    private List<Aerodrom> ucitaniAerodromi;
    private Semaphore dozvolaZaUcitavanjeAerodroma;

    public Dretva(Server roditelj, Socket veza, List<Aerodrom> ucitaniAerodromi) {
        super();
        this.veza = veza;
        this.roditelj = roditelj;
        this.ucitaniAerodromi = ucitaniAerodromi;
        this.dozvolaZaUcitavanjeAerodroma = new Semaphore(1);
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
                    posaljiOdgovor(osw, "OK");
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 01 Komanda \"" + komanda + "\" nije ispravna dok poslužitelj hibernira.");
                }
                break;
            case inicijaliziran:
                if (komanda.startsWith("LOAD ")) {
                    String JSON = komanda.substring(5);
                    Gson gsonCitac = new Gson();
                    try {
                        Aerodrom[] aerodromi = gsonCitac.fromJson(JSON, Aerodrom[].class);
                        StanjeServera.dajInstancu().promjeniStanje();

                        if (zatraziUpisAerodroma(osw)) {
                            for (Aerodrom aerodrom : aerodromi) {
                                this.ucitaniAerodromi.add(aerodrom);
                            }
                            otpustiDopustZaUpisAerodroma();
                            posaljiOdgovor(osw, "OK " + this.ucitaniAerodromi.size());
                        } else {
                            posaljiOdgovor(osw, "ERROR 14 Upis aerodroma nije moguć. Možda ga je već netko obavio.");
                        }
                    } catch (JsonSyntaxException ex) {
                        Logger.getLogger(Dretva.class.getName()).log(Level.INFO, "Neispravan JSON", ex);
                        posaljiOdgovor(osw, "ERROR 14 JSON nije u ispravnom formatu.");
                    }
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 02 Inicijalizirani poslužitelj prihvaća samo komandu LOAD (JSON polje objekata Aerodroma).");
                }
                break;
            case aktivan:
                if (komanda.contains("DISTANCE")) {
                    if (this.ucitaniAerodromi == null || this.ucitaniAerodromi.size() == 0) {
                        posaljiOdgovor(osw, "ERROR 14 Aerodromi nisu bili ispravno učitani!");
                    } else {
                        String icao[] = komanda.split("DISTANCE ")[1].split(" ");
                        Aerodrom prvi = pronadjiAerodrom(icao[0]);
                        Aerodrom drugi = pronadjiAerodrom(icao[1]);
                        if (prvi == null && drugi == null) {
                            posaljiOdgovor(osw,
                                    "ERROR 13 Aerodromi s poslanim oznakama nisu učitani!");
                        } else if (prvi == null) {
                            posaljiOdgovor(osw,
                                    "ERROR 11 Aerodrom s oznakom " + icao[0] + " nije učitan!");
                        } else if (drugi == null) {
                            posaljiOdgovor(osw,
                                    "ERROR 12 Aerodrom s oznakom " + icao[1] + " nije učitan!");
                        } else {
                            double udaljenost = izracunajUdaljenostPoHaversineovojFormuli(prvi, drugi);
                            posaljiOdgovor(osw, "OK " + (int) udaljenost);
                        }
                    }
                } else if (komanda.equals("CLEAR")) {
                    this.ucitaniAerodromi.clear();
                    StanjeServera.dajInstancu().promjeniStanje();
                    posaljiOdgovor(osw, "OK");
                } else {
                    posaljiOdgovor(osw,
                            "ERROR 03 Aktivni poslužitelj prihvaća samo komandu DISTANCE (icao1) (icao2).");
                }
                break;
        }
    }

    private void otpustiDopustZaUpisAerodroma() {
        dozvolaZaUcitavanjeAerodroma.release();
    }

    private boolean zatraziUpisAerodroma(OutputStreamWriter osw) {
        boolean dozvola = false;

        if (this.ucitaniAerodromi.isEmpty()) {
            try {
                dozvolaZaUcitavanjeAerodroma.acquire();
                dozvola = true;
            } catch (InterruptedException e) {
                Logger.getLogger(Dretva.class.getName()).log(Level.INFO, "Neispravan JSON", e);
                posaljiOdgovor(osw, "ERROR 14 JSON nije u ispravnom formatu.");
                return false;
            }
        }

        return dozvola;
    }

    /**
     * Vraća aerodrom s ICAO oznakom ili null.
     * @param string ICAO
     * @return Aerodrom ili null.
     */
    private Aerodrom pronadjiAerodrom(String string) {
        for (Aerodrom aerodrom : this.ucitaniAerodromi) {
            if (aerodrom.getIcao().equals(string)) {
                return aerodrom;
            }
        }
        return null;
    }

    /**
     * Koristi Haversinovu formulu za izračunavanje udaljenosti između dva aerodroma.
     * <p>Formula uzima u obzir zakrivljenost planeta Zemlje.
     * @param prvi Prvi aerodrom
     * @param drugi Drugi aerodrom
     * @return Decimalna vrijednost udaljenosti aerodroma
     */
    public double izracunajUdaljenostPoHaversineovojFormuli(Aerodrom prvi, Aerodrom drugi) {
        double gd1 = Math.toRadians(Double.parseDouble(prvi.getLokacija().getLongitude()));
        double gd2 = Math.toRadians(Double.parseDouble(drugi.getLokacija().getLongitude()));
        double gs1 = Math.toRadians(Double.parseDouble(prvi.getLokacija().getLatitude()));
        double gs2 = Math.toRadians(Double.parseDouble(drugi.getLokacija().getLatitude()));
        double udaljenostDuzina = gd2 - gd1;
        double udaljenostSirina = gs2 - gs1;
        double a = Math.pow(Math.sin(udaljenostSirina / 2), 2)
                + Math.cos(gs1) * Math.cos(gs2) * Math.pow(Math.sin(udaljenostDuzina / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;
        return (r * c);
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
