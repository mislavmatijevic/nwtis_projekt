package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Controller
@Path("pocetak")
@RequestScoped
public class MvcPocetna {
    @Inject
    private Models model;

    @GET
    @Path("")
    @View("index.jsp")
    public void pocetak() {
    }
}
