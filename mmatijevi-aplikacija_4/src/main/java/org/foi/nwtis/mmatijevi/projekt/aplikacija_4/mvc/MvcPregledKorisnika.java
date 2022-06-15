package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.KorisniciKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Controller
@Path("korisnici")
@RequestScoped
public class MvcPregledKorisnika {
    @Inject
    Models model;
    @Inject
    HttpSession sjednica;
    @Inject
    ServletContext kontekst;

    @GET
    @View("korisnici.jsp")
    public void dohvatiSveKorisnike(@Context HttpServletRequest zahtjev) {
        KorisniciKlijent korisniciKlijent = new KorisniciKlijent(kontekst);

        HttpSession sjednica = zahtjev.getSession();
        PrijavljeniKorisnik korisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");

        if (korisnik != null) {
            try {
                List<Korisnik> korisnici = korisniciKlijent.dohvatiSveKorisnike(
                        korisnik.getKorime(),
                        korisnik.getZeton());
                if (korisnici != null) {

                    Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
                    String administratorskaGrupa = konfig.dajPostavku("sustav.administratori");

                    String[] korisnikoveGrupe = korisniciKlijent.dohvatiKorisnikoveGrupe(
                            korisnik.getKorime(), korisnik.getZeton());

                    boolean korisnikJeAdministrator = false;

                    if (korisnikoveGrupe != null) {
                        for (int i = 0; i < korisnikoveGrupe.length && korisnikJeAdministrator == false; i++) {
                            if (korisnikoveGrupe[i].equals(administratorskaGrupa)) {
                                korisnikJeAdministrator = true;
                            }
                        }
                    }

                    if (korisnikJeAdministrator) {
                        model.put("korisnikJeAdministrator", true);
                    } else {
                        model.put("korisnikJeAdministrator", false);
                    }

                    model.put("korisnici", korisnici);
                } else {
                    model.put("greskaPoruka", "Korisnici nisu mogli biti dohvaćeni...");
                }
            } catch (Exception ex) {
                model.put("greskaPoruka", ex.getLocalizedMessage());
            }
        } else {
            model.put("greskaPoruka", "Prvo se prijavite u sustav!");
        }
    }

    private boolean provjeriAdministrskeOvlasti(PrijavljeniKorisnik korisnik, KorisniciKlijent korisniciKlijent)
            throws ZetonIstekaoException {
        Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
        String administratorskaGrupa = konfig.dajPostavku("sustav.administratori");

        String[] korisnikoveGrupe = korisniciKlijent.dohvatiKorisnikoveGrupe(korisnik);

        boolean korisnikJeAdministrator = false;

        if (korisnikoveGrupe != null) {
            for (int i = 0; i < korisnikoveGrupe.length && korisnikJeAdministrator == false; i++) {
                if (korisnikoveGrupe[i].equals(administratorskaGrupa)) {
                    korisnikJeAdministrator = true;
                }
            }
        }
        return korisnikJeAdministrator;
    }
}
