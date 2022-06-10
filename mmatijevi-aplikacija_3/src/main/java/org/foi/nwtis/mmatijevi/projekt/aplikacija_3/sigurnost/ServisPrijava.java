package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServisPrijava extends KonfigurabilniServis {

    @Inject
    Baza baza;

    /**
     * Prijava korisnika
     * @param korime Korisničko ime
     * @param lozinka Lozinka
     * @return 0 - neuspjeh, 1 - prijavljen
     */
    public boolean prijaviKorisnika(String korime, String lozinka) {
        if (korime == null || lozinka == null || korime.isBlank() || lozinka.isBlank()) {
            return false;
        }

        try (Connection veza = baza.stvoriVezu(this.konfig);
                PreparedStatement izraz = veza
                        .prepareStatement("SELECT * FROM korisnici WHERE korisnik = ? AND lozinka = ?;")) {

            izraz.setString(1, korime);
            izraz.setString(2, lozinka);

            try (ResultSet rezultat = izraz.executeQuery()) {
                if (rezultat.next()) {
                    return true;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(ServisPrijava.class.getName()).log(Level.SEVERE, "Neuspjelo logiranje korisnika!", ex);
        }

        return false;
    }
}
