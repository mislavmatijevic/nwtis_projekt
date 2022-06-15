package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.mvc;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti.AerodromiKlijent;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ServerUdaljenostiIznimka;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti.ServerUdaljenostiNaredba;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;

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
@Path("udaljenost")
@RequestScoped
public class MvcPosluziteljUdaljenosti {
    @Inject
    Models model;
    @Inject
    ServletContext kontekst;
    @Inject
    PosluziteljUdaljenosti posluziteljUdaljenosti;

    @GET
    @View("udaljenost.jsp")
    public void pocetak() {
        prikaziStatusPosluzitelja();
    }

    @POST
    @View("udaljenost.jsp")
    public void promjenaStanja(@FormParam("komanda") String komanda, @Context HttpServletRequest zahtjev) {

        HttpSession sjednica = zahtjev.getSession();
        PrijavljeniKorisnik korisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");

        if (komanda.equals("LOAD") && korisnik != null) {
            izvrsiLoadNaredbu(komanda, korisnik);
        } else if (komanda.equals("LOAD") && korisnik == null) {
            model.put("greskaPoruka", "Za provedbu \"LOAD\" naredbe trebate biti prijavljeni!");
        } else if (!komanda.equals("LOAD")) {
            izvrsiObicnuNaredbu(komanda);
        }

        if (!komanda.equals("QUIT")) {
            prikaziStatusPosluzitelja();
        } else {
            model.put("status", "Poslužitelj je uspješno ugašen!");
        }
    }

    private void izvrsiLoadNaredbu(String komanda, PrijavljeniKorisnik korisnik) {
        AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(kontekst);
        try {
            List<Aerodrom> praceniAerodromi = aerodromiKlijent.dohvatiPraceneAerodrome(korisnik);
            if (praceniAerodromi != null) {
                Gson gson = new Gson();
                posluziteljUdaljenosti.izvrsiNaredbu(
                        ServerUdaljenostiNaredba.valueOf(komanda),
                        new String[] { gson.toJson(praceniAerodromi) });
            } else {
                model.put("greskaPoruka", "Neuspio dohvat zrakoplova! Uzrok nije poznat.");
            }
        } catch (ZetonIstekaoException ex) {
            model.put("greskaPoruka", ex.getLocalizedMessage());
        } catch (IOException ex) {
            Logger.getLogger(MvcPosluziteljUdaljenosti.class.getName()).log(
                    Level.SEVERE, "Neuspjelo povezivanje sa serverom udaljenosti", ex);
            model.put("greskaPoruka", "Neuspjelo povezivanje sa serverom udaljenosti");
        } catch (ServerUdaljenostiIznimka ex) {
            model.put("greskaPoruka", ex.getLocalizedMessage());
        }
    }

    private void izvrsiObicnuNaredbu(String komanda) {
        try {
            posluziteljUdaljenosti.izvrsiNaredbu(ServerUdaljenostiNaredba.valueOf(komanda), null);
        } catch (ServerUdaljenostiIznimka ex) {
            model.put("greskaPoruka", ex.getLocalizedMessage());
        } catch (Exception ex) {
            Logger.getLogger(MvcPosluziteljUdaljenosti.class.getName()).log(
                    Level.SEVERE, "Neuspjelo povezivanje sa serverom udaljenosti", ex);
            model.put("greskaPoruka", "Neuspjelo povezivanje sa serverom udaljenosti");
        }
    }

    private void prikaziStatusPosluzitelja() {
        try {
            String odgovorPosluzitelja = posluziteljUdaljenosti.izvrsiNaredbu(
                    ServerUdaljenostiNaredba.STATUS, null);

            if (odgovorPosluzitelja.contains("OK ")) {
                String statusBrojcani = odgovorPosluzitelja.split("OK ")[1];
                String status;

                switch (statusBrojcani) {
                    case "0":
                        status = "Hibernira";
                        break;
                    case "1":
                        status = "Inicijaliziran";
                        break;
                    case "2": {
                        status = "Aktivan";
                        break;
                    }
                    default: {
                        status = "Neodređeno stanje";
                    }
                }
                model.put("status", status);
            } else {
                model.put("greskaPoruka", odgovorPosluzitelja);
            }

        } catch (NumberFormatException | IOException | ServerUdaljenostiIznimka e) {
            model.put("greskaPoruka", "Poslužitelj udaljenosti trenutno nije dostupan.");
        }
    }
}
