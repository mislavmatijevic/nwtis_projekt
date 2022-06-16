package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.KorisniciKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.ProvjereKlijent;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Controller
@Path("korisnici")
@RequestScoped
public class MvcPregledKorisnika {
    @Inject
    Models model;
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
                List<Korisnik> korisnici = korisniciKlijent.dohvatiSveKorisnike(korisnik);
                if (korisnici != null) {

                    boolean korisnikJeAdministrator = provjeriAdministrskeOvlasti(korisnik, korisniciKlijent);

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
            model.put("greskaPoruka", "Prvo se morate prijaviti!");
        }

    }

    @POST
    @View("korisnici.jsp")
    public void brisanjeTokenaDrugogKorisnika(@FormParam("korime") String korime, @FormParam("odjava") String odjava,
            @Context HttpServletRequest zahtjev) {

        HttpSession sjednica = zahtjev.getSession();
        PrijavljeniKorisnik korisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");

        if (korisnik != null) {
            if (odjava != null) {
                izbrisiTrenutniZeton(korisnik);
            } else if (korime != null) {
                izbrisiZetoneZaKorisnickoIme(korime, korisnik);
            }
        } else {
            model.put("greskaPoruka", "Prijavite se u sustav!");
        }
    }

    /**
     * Obavlja brisanje trenutno aktivnog žetona, odnosno odjavu korisnika u trenutnom pogledu preglednika.
     * @param korisnik Objekt prijavljenog korisnika.
     */
    private void izbrisiTrenutniZeton(PrijavljeniKorisnik korisnik) {

        ProvjereKlijent provjereKlijent = new ProvjereKlijent(kontekst);
        boolean uspjeh = false;

        uspjeh = provjereKlijent.deaktivirajZeton(korisnik);

        if (uspjeh) {
            model.put("infoPoruka", "Odjavljeni ste! Morat ćete ponoviti prijavu za nastavak rada.");
        } else {
            model.put("greskaPoruka", "Problem! Moguće da vam je istekla sesija.");
        }
    }

    private void izbrisiZetoneZaKorisnickoIme(String korime, PrijavljeniKorisnik korisnik) {

        KorisniciKlijent korisniciKlijent = new KorisniciKlijent(kontekst);

        boolean korisnikJeAdministrator = false;
        try {
            korisnikJeAdministrator = provjeriAdministrskeOvlasti(korisnik, korisniciKlijent);
        } catch (Exception ex) {
            model.put("greskaPoruka", ex.getLocalizedMessage());
        }

        if (korisnikJeAdministrator) {
            ProvjereKlijent provjereKlijent = new ProvjereKlijent(kontekst);
            String odgovor = provjereKlijent.deaktivirajSveZetone(korime, korisnik);
            model.put("infoPoruka", odgovor);
        } else {
            model.put("greskaPoruka", "Niste administrator sustava!");
        }
    }

    private boolean provjeriAdministrskeOvlasti(PrijavljeniKorisnik korisnik, KorisniciKlijent korisniciKlijent)
            throws ZetonIstekaoException {
        Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
        String administratorskaGrupa = konfig.dajPostavku("sustav.administratori");

        List<String> korisnikoveGrupe = korisniciKlijent.dohvatiKorisnikoveGrupe(korisnik);

        boolean korisnikJeAdministrator = false;

        if (korisnikoveGrupe != null) {
            korisnikJeAdministrator = korisnikoveGrupe.contains(administratorskaGrupa);
        }

        return korisnikJeAdministrator;
    }
}
