package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.modeli.PrijavljeniKorisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Controller
@Path("pocetak")
@RequestScoped
public class MvcPocetna {
    @Inject
    Models model;
    @Inject
    HttpSession sjednica;

    @GET
    @View("index.jsp")
    public void pocetak() {

        PrijavljeniKorisnik korisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");

        if (korisnik != null) {
            model.put("korimePrijavljeni", korisnik.getKorime());
        } else {
            model.put("korimePrijavljeni", "Niste prijavljeni");
        }
    }
}
