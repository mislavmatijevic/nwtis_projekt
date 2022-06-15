package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.ProvjereKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;

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
@Path("prijava")
@RequestScoped
public class MvcPrijava {
    @Inject
    Models model;
    @Inject
    ServletContext kontekst;

    @GET
    @View("prijava.jsp")
    public void prijavaUnos() {
    }

    @POST
    @View("prijava.jsp")
    public void prijavaPoslana(
            @FormParam("korime") String korime,
            @FormParam("lozinka") String lozinka,
            @Context HttpServletRequest zahtjev) {
        ProvjereKlijent pk = new ProvjereKlijent(kontekst);

        try {
            Zeton zeton = pk.prijaviKorisnika(korime, lozinka);
            PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik(korime, lozinka, zeton.getZeton());
            HttpSession sesija = zahtjev.getSession();
            sesija.setAttribute("korisnik", prijavljeniKorisnik);
            model.put("infoPoruka", "Uspješno ste prijavljeni!");
        } catch (Exception e) {
            model.put("greskaPoruka", "Prijava neuspješna");
        }

    }
}
