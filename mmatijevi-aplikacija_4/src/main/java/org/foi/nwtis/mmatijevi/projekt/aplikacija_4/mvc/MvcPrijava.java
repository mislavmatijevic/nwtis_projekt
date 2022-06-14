package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.ProvjereKlijent;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
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
    HttpSession sjednica;

    @GET
    @Path("")
    @View("prijava.jsp")
    public void prijavaUnos() {
    }

    @POST
    @Path("")
    @View("prijava.jsp")
    public void prijavaPoslana(@FormParam("korime") String korime, @FormParam("lozinka") String lozinka,
            @Context ServletContext context) {
        ProvjereKlijent pk = new ProvjereKlijent(context);

        try {
            Zeton zeton = pk.prijaviKorisnika(korime, lozinka);
            sjednica.setAttribute("korisnik", new PrijavljeniKorisnik(korime, zeton));
            model.put("infoPoruka", "Uspješno ste prijavljeni!");
        } catch (KorisnikNePostojiException e) {
            model.put("greskaPoruka", e.getLocalizedMessage());
        }

    }
}
