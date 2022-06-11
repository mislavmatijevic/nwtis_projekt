package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServisKorisnika extends KonfigurabilniServis {

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

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza
                        .prepareStatement("SELECT * FROM korisnici WHERE korisnik = ? AND lozinka = ?")) {

            izraz.setString(1, korime);
            izraz.setString(2, lozinka);

            try (ResultSet rezultat = izraz.executeQuery()) {
                if (rezultat.next()) {
                    return true;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(ServisKorisnika.class.getName()).log(Level.SEVERE, "Neuspjelo logiranje korisnika", ex);
        }

        return false;
    }

    /**
     * Dohvaća sve grupe u kojima se korisnik nalazi, tj. sve korisnikove uloge.
     * @param korime
     * @return Lista naziva grupa ili 'null' ako je došlo do pogreške pri čitanju.
     * @throws KorisnikNePostojiException
     */
    public List<String> dohvatiGrupeKorisnika(String korime) throws KorisnikNePostojiException {
        List<String> korisnikoveGrupe = new LinkedList<>();

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza
                        .prepareStatement("SELECT grupa FROM uloge WHERE korisnik = ?")) {

            if (!provjeriPostojiLiKorisnik(korime)) {
                throw new KorisnikNePostojiException(
                        "Korisnik " + korime + " nije pronađen u bazi");
            }

            izraz.setString(1, korime);

            try (ResultSet rezultat = izraz.executeQuery()) {
                while (rezultat.next()) {
                    korisnikoveGrupe.add(rezultat.getString("grupa"));
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisKorisnika.class.getName()).log(Level.SEVERE, "Neuspio dohvat grupe korisnika", ex);
            korisnikoveGrupe = null;
        }

        return korisnikoveGrupe;
    }
}
