package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NeovlasteniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NepostojeciZetonException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NovaOznakaNedostupnaException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Zeton;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * <h1>Žetoni</h1>
 * Klasa za upravljanje žetonima.
 */
@ApplicationScoped
public class ServisZetona extends KonfigurabilniServis {

    @Inject
    GeneratorOznakaZetona goz;
    @Inject
    ServisKorisnika servisKorisnika;
    @Inject
    Baza baza;

    public Zeton stvoriNoviZeton(String korime) throws NovaOznakaNedostupnaException {

        if (korime != null && !korime.isBlank()) {

            int novaOznaka = goz.generirajNovuOznaku();
            long trenutnoVrijemeMs = (new Date().getTime());
            int novoVrijeme = (int) (trenutnoVrijemeMs / 1000)
                    + Integer.parseInt(this.konfig.dajPostavku("zeton.trajanje"));

            try (Connection veza = baza.dohvatiVezu();
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

    /**
     * Provjerava je li žeton važeći.
     * @param zeton Broj žetona.
     * @param korime Korisnik kojemu je žeton izdan.
     * @return true - valjan, false - nije valjan/istekao
     * @throws NepostojeciZetonException Ističe da žeton nije izdan korisniku (ili ne postoji uopće).
     */
    public boolean provjeriZeton(int zeton, String korime) throws NepostojeciZetonException {

        boolean zetonJeValjan = false;

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza
                        .prepareStatement(
                                "SELECT rok_trajanja, status FROM zetoni WHERE oznaka_zeton = ? AND podaci_korisnik = ?")) {

            izraz.setInt(1, zeton);
            izraz.setString(2, korime);

            try (ResultSet rezultat = izraz.executeQuery()) {
                if (rezultat.next()) {

                    boolean status = rezultat.getBoolean("status");

                    if (status) {
                        int rokTrajanja = rezultat.getInt("rok_trajanja");
                        int trenutnoVrijeme = (int) (new Date().getTime() / 1000);
                        if (trenutnoVrijeme < rokTrajanja) {
                            zetonJeValjan = true;
                        } else {
                            deaktivirajZeton(zeton);
                        }
                    }
                } else {
                    throw new NepostojeciZetonException(
                            "Žeton nije generiran za korisnika " + korime);
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                    "Neuspjelo stvaranje žetona za korisnika " + korime,
                    ex);
        }

        return zetonJeValjan;
    }

    /**
     * Označava u bazi da je žeton deaktiviran
     * @param zeton Oznaka žetona koji treba deaktivirati.
     * @throws NepostojeciZetonException U slučaju da žeton ne postoji u bazi.
     */
    public void deaktivirajZeton(int zeton) throws NepostojeciZetonException {
        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izrazDohvataBaremJednog = veza
                        .prepareStatement("SELECT status FROM nwtis_bp_1.zetoni WHERE oznaka_zeton = ?")) {

            izrazDohvataBaremJednog.setInt(1, zeton);

            boolean zetonJeAktivan = false;

            try (ResultSet rs = izrazDohvataBaremJednog.executeQuery()) {
                if (rs.next()) {
                    zetonJeAktivan = rs.getBoolean("status");
                } else {
                    throw new NepostojeciZetonException("Žeton " + zeton + " nije bio izdan");
                }
            }

            if (zetonJeAktivan) {
                try (PreparedStatement izrazPromijeneStatusa = veza
                        .prepareStatement(
                                "UPDATE nwtis_bp_1.zetoni SET status = 0 WHERE oznaka_zeton = ?")) {
                    izrazPromijeneStatusa.setInt(1, zeton);
                    izrazPromijeneStatusa.executeUpdate();
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                    "Neuspjelo deaktiviranje žetona " + zeton, ex);
        }
    }

    /**
     * Deaktivira sve žetone u bazi jednog korisnika pod naredbom određenog korisnika.
     * @param aktivator Korisnik koji je naložio deaktivaciju.
     * Može biti u ulozi iz grupe definirane postavkom "sustav.administratori".
     * @param korime Korisnik kojemu će se svi žetoni deaktivirati.
     * @return Broj deaktiviranih žetona.
     * @throws NeovlasteniKorisnik Aktivator nema ovlasti za brisanje svih žetona odabranog korisnika.
     * @throws NepostojeciZetonException Neki se žeton nije mogao obrisati.
     */
    public int deaktivirajSveZetone(String aktivator, String korime)
            throws NeovlasteniKorisnik, NepostojeciZetonException {
        int brojObrisanihZetona = 0;

        if (!aktivator.equals(korime)) {
            String grupaAdministratora = this.konfig.dajPostavku("sustav.administratori");
            List<String> grupeKorisnika = servisKorisnika.dohvatiGrupeKorisnika(aktivator);
            if (!grupeKorisnika.contains(grupaAdministratora)) {
                throw new NeovlasteniKorisnik("Korisnik " + aktivator
                        + " nije u ulozi administratora sustava te kao takav ne može brisati sve žetone korisnika "
                        + korime + "!");
            }
        }

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza
                        .prepareStatement(
                                "UPDATE zetoni SET status = 0 WHERE podaci_korisnik = ? AND status = 1")) {

            izraz.setString(1, korime);

            brojObrisanihZetona = izraz.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                    "Neuspjelo deaktiviranje svih žetona za korisnika " + korime, ex);
        }

        return brojObrisanihZetona;
    }

}
