package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.GeneratorOznakaZetona.NovaOznakaNedostupnaException;

/**
 * <h1>Žetoni</h1>
 * Klasa za upravljanje žetonima.
 */
public class ServisZetona extends KonfigurabilniServis {

    private static ServisZetona instanca = null;
    GeneratorOznakaZetona goz = null;

    private ServisZetona() {
        super();
        goz = new GeneratorOznakaZetona();
    }

    public static ServisZetona dajInstancu() {
        if (instanca == null) {
            instanca = new ServisZetona();
        }
        return instanca;
    }

    public Zeton stvoriNoviZeton(String korime) throws NovaOznakaNedostupnaException {

        if (korime != null && !korime.isBlank()) {

            int novaOznaka = goz.generirajNovuOznaku(konfig);
            long trenutnoVrijemeMs = (new Date().getTime());
            int novoVrijeme = (int) (trenutnoVrijemeMs / 1000) + Integer.parseInt(konfig.dajPostavku("zeton.trajanje"));

            try (Baza baza = Baza.dajInstancu();
                    Connection veza = baza.stvoriVezu(this.konfig);
                    PreparedStatement izraz = veza
                            .prepareStatement(
                                    "INSERT INTO `nwtis_bp_1`.`zetoni` (`oznaka_zeton`, `podaci_korisnik`, `rok_trajanja`) VALUES (?, ?, ?)")) {

                izraz.setInt(1, novaOznaka);
                izraz.setString(2, korime);
                izraz.setInt(3, novoVrijeme);

                izraz.execute();

                return new Zeton(novaOznaka, novoVrijeme);
            } catch (Exception ex) {
                Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                        "Neuspjelo stvaranje žetona za korisnika " + korime + "!",
                        ex);
            }
        }

        return null;
    }

}
