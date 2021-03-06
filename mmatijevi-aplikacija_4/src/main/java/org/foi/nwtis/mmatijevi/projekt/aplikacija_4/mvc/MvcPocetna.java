package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Controller
@Path("pocetak")
@RequestScoped
public class MvcPocetna {
    @Inject
    Models model;

    @GET
    @View("index.jsp")
    public void pocetak(@Context HttpServletRequest zahtjev) {

        HttpSession sjednica = zahtjev.getSession();
        PrijavljeniKorisnik korisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");

        if (korisnik != null) {
            model.put("korimePrijavljeni", korisnik.getKorime());
        } else {
            model.put("korimePrijavljeni", "Niste prijavljeni");
        }
    }
}
