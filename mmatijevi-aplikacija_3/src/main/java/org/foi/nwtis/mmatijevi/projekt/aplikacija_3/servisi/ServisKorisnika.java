package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.KorisnikPrikaz;
import org.foi.nwtis.podaci.Korisnik;

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

    /**
     * Dohvaća sve korisnike iz baze, s lozinkama sakrivenima.
     * @return Listu korisnika ILI 'null' ako je došlo do pogreške.
     */
    public List<KorisnikPrikaz> dajSveKorisnike() {
        List<KorisnikPrikaz> korisnici = new LinkedList<>();

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza
                        .prepareStatement("SELECT korisnik, ime, prezime, email FROM korisnici")) {

            try (ResultSet rezultat = izraz.executeQuery()) {
                while (rezultat.next()) {
                    korisnici.add(dajObjektPrikazaKorisnika(rezultat));
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisKorisnika.class.getName()).log(Level.SEVERE, "Neuspjelo logiranje korisnika", ex);
            korisnici = null;
        }

        return korisnici;
    }

    /**
     * Vraća jednog korisnika po korisničkom imenu.
     * @param korime Korisničko ime.
     * @return Objekt korisnika ILI 'null' ako je došlo do pogreške.
     * @throws KorisnikNePostojiException Korisnik ne postoji u bazi.
     */
    public KorisnikPrikaz dohvatiJednogKorisnika(String korime) throws KorisnikNePostojiException {
        KorisnikPrikaz korisnik = null;

        if (korime != null && !korime.isBlank()) {
            try (Connection veza = baza.dohvatiVezu();
                    PreparedStatement izraz = veza
                            .prepareStatement(
                                    "SELECT korisnik, ime, prezime, email FROM korisnici WHERE korisnik = ?")) {

                izraz.setString(1, korime);

                try (ResultSet rezultat = izraz.executeQuery()) {
                    if (rezultat.next()) {
                        korisnik = dajObjektPrikazaKorisnika(rezultat);
                    } else {
                        throw new KorisnikNePostojiException(korime);
                    }
                }

            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(ServisKorisnika.class.getName()).log(Level.SEVERE, "Neuspio dohvat korisnika", ex);
            }
        }

        return korisnik;
    }

    /**
     * 
     * @param korisnik
     * @return
     * @throws KorisnikVecPostojiException
     * @throws KorisnikNeispravanException
     */
    public boolean dodajNovogKorisnika(Korisnik korisnik)
            throws KorisnikVecPostojiException, KorisnikNeispravanException {
        boolean uspjeh = false;

        String korime = korisnik.getKorIme();

        if (!provjeriIspravnostKorisnika(korisnik)) {
            throw new KorisnikNeispravanException();
        }

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izrazUnos = veza
                        .prepareStatement(
                                "INSERT INTO korisnici (korisnik, ime, prezime, lozinka, email) VALUES (?, ?, ?, ?, ?)")) {

            if (provjeriPostojiLiKorisnik(korime)) {
                throw new KorisnikVecPostojiException(korime);
            }

            izrazUnos.setString(1, korime);
            izrazUnos.setString(2, korisnik.getIme());
            izrazUnos.setString(3, korisnik.getPrezime());
            izrazUnos.setString(4, korisnik.getLozinka());
            izrazUnos.setString(5, korisnik.getEmail());

            izrazUnos.execute();
            uspjeh = true;

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServisZetona.class.getName()).log(Level.SEVERE,
                    "Neuspjelo stvaranje korisnika " + korime + "!", ex);
        }

        return uspjeh;
    }

    private boolean provjeriPostojiLiKorisnik(String korime) throws ClassNotFoundException, SQLException {
        boolean postoji;

        try (PreparedStatement izrazProvjera = baza.dohvatiVezu()
                .prepareStatement("SELECT korisnik FROM korisnici WHERE korisnik = ?");) {
            izrazProvjera.setString(1, korime);

            postoji = izrazProvjera.executeQuery().next();
        }

        return postoji;
    }

    private KorisnikPrikaz dajObjektPrikazaKorisnika(ResultSet rezultat) throws SQLException {
        return new KorisnikPrikaz(rezultat.getString("korisnik"), rezultat.getString("ime"),
                rezultat.getString("prezime"), "**********", rezultat.getString("email"));
    }

    private boolean provjeriIspravnostKorisnika(Korisnik korisnik) {

        boolean unesenoKorIme = korisnik.getKorIme() != null && !korisnik.getKorIme().isBlank();
        boolean unesenoIme = korisnik.getIme() != null && !korisnik.getIme().isBlank();
        boolean unesenoPrezime = korisnik.getPrezime() != null && !korisnik.getPrezime().isBlank();
        boolean unesenaLozinka = korisnik.getLozinka() != null && !korisnik.getLozinka().isBlank();
        boolean unesenEmail = korisnik.getEmail() != null && !korisnik.getEmail().isBlank();

        return (unesenoKorIme && unesenoIme && unesenoPrezime && unesenaLozinka && unesenEmail);
    }
}
