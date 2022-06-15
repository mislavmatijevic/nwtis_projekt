package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.KorisniciKlijent;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.Konfiguracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikRegistracija;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Controller
@Path("registracija")
@RequestScoped
public class MvcRegistracija {
    @Inject
    Models model;
    @Inject
    ServletContext kontekst;

    @GET
    @Path("")
    @View("registracija.jsp")
    public void registracijaUnos() {
        Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
        String sustavKorisnik = konfig.dajPostavku("sustav.korisnik");
        model.put("sustavKorisnik", sustavKorisnik);
    }

    @POST
    @Path("")
    @View("registracija.jsp")
    public void registracijaPoslana(
            @FormParam("korime") String korime,
            @FormParam("ime") String ime,
            @FormParam("prezime") String prezime,
            @FormParam("lozinka") String lozinka,
            @FormParam("email") String email) {
        KorisniciKlijent pk = new KorisniciKlijent(kontekst);

        KorisnikRegistracija korisnik = new KorisnikRegistracija(korime, ime, prezime, lozinka, email);

        boolean registracijaObavljena = false;

        try {
            registracijaObavljena = pk.registrirajKorisnika(korisnik);
            if (registracijaObavljena) {
                model.put("infoPoruka", "Korisnik je registriran!");
            } else {
                model.put("greskaPoruka", "Nažalost, registracija nije uspjela zbog nepoznatog problema!");
            }
        } catch (KorisnikNePostojiException ex) {
            model.put("greskaPoruka", "Dogodio se problem sa sustavskim korisnikom!");
        } catch (KorisnikNeispravanException ex) {
            model.put("greskaPoruka", "Popunite sva polja!");
        } catch (KorisnikVecPostojiException ex) {
            model.put("greskaPoruka", "Korisnik s tim korisničkim imenom već postoji!");
        } catch (Exception ex) {
            model.put("greskaPoruka", ex.getLocalizedMessage());
        } finally {
            if (registracijaObavljena) {
                model.put("korime", korime);
                model.put("ime", ime);
                model.put("prezime", prezime);
                model.put("lozinka", lozinka);
                model.put("email", email);
            }
            Konfiguracija konfig = (Konfiguracija) kontekst.getAttribute("postavke");
            String sustavKorisnik = konfig.dajPostavku("sustav.korisnik");
            model.put("sustavKorisnik", sustavKorisnik);
        }

    }
}
