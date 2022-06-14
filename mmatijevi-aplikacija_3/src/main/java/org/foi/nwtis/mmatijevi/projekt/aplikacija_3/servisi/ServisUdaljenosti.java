package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ServerUdaljenostiIznimka;
import org.foi.nwtis.mmatijevi.projekt.modeli.OdgovorStatusUdaljenost;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServisUdaljenosti extends KonfigurabilniServis {

    public enum ServerUdaljenostiNaredba {
        STATUS,
        INIT,
        LOAD,
        DISTANCE,
        CLEAR,
        QUIT
    }

    /**
     * Kontaktira poslužitelja udaljenosti zahtijevajući udaljenost među aerodromima.
     * @param naredba ServerUdaljenostiNaredba za poslužitelja.
     * @param argumenti Argumenti uz naredbu. Može biti prazno.
     * @return Broj udaljenosti.
     * @throws ServerUdaljenostiIznimka Poruka sadržava opis problema sa udaljenog poslužitelja.
     * @throws NumberFormatException Problem pri čitanju brojevnog odgovora sa poslužitelja udaljenosti.
     * @throws IOException Pogreška pri spajanju na poslužitelja.
     * @throws SocketException Pogreška pri komunikaciji s poslužiteljem.
     */
    public String izvrsiNaredbu(ServerUdaljenostiNaredba naredba, String[] argumenti)
            throws SocketException, IOException, NumberFormatException, ServerUdaljenostiIznimka {

        StringBuilder komanda = new StringBuilder();

        switch (naredba) {
            case STATUS:
            case INIT:
            case CLEAR:
            case QUIT: {
                komanda.append(naredba.toString());
                break;
            }
            case LOAD: {
                komanda.append(naredba.toString());
                komanda.append(" ");
                komanda.append(argumenti[0]);
                break;
            }
            case DISTANCE: {
                komanda.append(naredba.toString());
                komanda.append(" ");
                komanda.append(argumenti[0]);
                komanda.append(" ");
                komanda.append(argumenti[1]);
                break;
            }
            default: {
                throw new ServerUdaljenostiIznimka("Neispravna naredba");
            }
        }

        String dobivenOdgovor = posaljiKomandu(komanda.toString());

        if (dobivenOdgovor.contains("ERROR")) {
            throw new ServerUdaljenostiIznimka(dobivenOdgovor);
        }

        return dobivenOdgovor;
    }

    /**
     * Šalje STATUS naredbu na poslužitelja.
     * @return Adresa i port u posebnom objektu ILI 'null' u slučaju neuspjeha.
     */
    public OdgovorStatusUdaljenost posaljiStatus() {
        OdgovorStatusUdaljenost odgovor;

        try {
            posaljiKomandu("STATUS");
            odgovor = new OdgovorStatusUdaljenost(
                    konfig.dajPostavku("a"),
                    Integer.parseInt(konfig.dajPostavku("p")));
        } catch (Exception e) {
            odgovor = null;
        }

        return odgovor;
    }

    /**
     * Proslijeđuje komandu poslužitelj udaljenosti.
     * <p>Ima odgovornost uspješno zatvoriti vezu, uključujući slučaj iznimke.
     * @param komanda Komanda za poslužitelja udaljenosti
     * @return Tekst dobivenog odgovora
     * @throws IOException Pogreška pri spajanju na poslužitelja.
     * @throws SocketException Pogreška pri komunikaciji s poslužiteljem.
     * @see ServerUdaljenosti
     */
    public String posaljiKomandu(String komanda) throws IOException, SocketException {
        Socket veza = new Socket();

        try {
            InetSocketAddress isa = new InetSocketAddress(konfig.dajPostavku("a"),
                    Integer.parseInt(konfig.dajPostavku("p")));
            veza.connect(isa, Integer.parseInt(konfig.dajPostavku("maks.cekanje")));

            try (InputStreamReader isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
                    OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(),
                            Charset.forName("UTF-8"));) {
                osw.write(komanda);
                osw.flush();
                veza.shutdownOutput();

                StringBuilder tekst = new StringBuilder();
                while (true) {
                    int i = isr.read();
                    if (i == -1) {
                        break;
                    }
                    tekst.append((char) i);
                }

                veza.shutdownInput();
                return tekst.toString();
            } catch (SocketException ex) {
                Logger.getLogger(ServisUdaljenosti.class.getName()).log(Level.SEVERE, "Nije uspjelo povezivanje", ex);
                throw new SocketException("ServerUdaljenosti je prepoznat, no nije uspjelo povezivanje.");
            }
        } catch (IOException ex) {
            Logger.getLogger(ServisUdaljenosti.class.getName()).log(Level.SEVERE, "Problem pri vezi", ex);
            throw ex;
        } finally {
            ugasiVezu(veza);
        }
    }

    /** 
     * Provjerava je li otvorena i potom gasi vezu. Ispisuje pogrešku ako gašenje veze nije uspjelo.
     * @param veza Veza koju treba zatvoriti.
     */
    public void ugasiVezu(Socket veza) {
        if (!veza.isClosed()) {
            try {
                veza.close();
            } catch (IOException ex) {
                Logger.getLogger(ServisUdaljenosti.class.getName()).log(Level.SEVERE, "Neuspjelo gašenje veze", ex);
                Terminal.greskaIspis("Neuspjelo gašenje veze!");
            }
        }
    }
}
