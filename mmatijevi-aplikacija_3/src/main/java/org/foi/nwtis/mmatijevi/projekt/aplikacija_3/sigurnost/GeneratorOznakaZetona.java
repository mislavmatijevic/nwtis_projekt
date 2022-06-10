package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NovaOznakaNedostupnaException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneratorOznakaZetona {
    @Inject
    Baza baza;

    /**
     * Generira novi žeton, tj. njegovu oznaku, na siguran način.
     * Ako još nije memorizirana oznaka žetona, čita iz baze posljednju.
     * U suprotnom, koristi statičku oznaku.
     * @return
     * @throws NovaOznakaNedostupnaException
     */
    public int generirajNovuOznaku() throws NovaOznakaNedostupnaException {
        return 1 + dohvatiPosljednjuOznakuIzBaze();
    }

    private int dohvatiPosljednjuOznakuIzBaze() throws NovaOznakaNedostupnaException {
        try (Connection veza = baza.stvoriVezu();
                PreparedStatement izraz = veza.prepareStatement(
                        "SELECT oznaka_zeton FROM zetoni ORDER BY oznaka_zeton DESC LIMIT 1");
                ResultSet rs = izraz.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("oznaka_zeton");
            } else {
                return 1;
            }

        } catch (Exception ex) {
            Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                    "Neuspjelo postavljanje posljednje oznake žetona!", ex);
            throw new NovaOznakaNedostupnaException("Neuspjelo postavljanje posljednje oznake žetona!");
        }
    }
}
