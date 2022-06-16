package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.iznimke.ServerUdaljenostiIznimka;
import org.foi.nwtis.mmatijevi.projekt.modeli.OdgovorStatusUdaljenost;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti.ServerUdaljenostiNaredba;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("serveri")
public class RestServeri {

    @Inject
    PosluziteljUdaljenosti servisUdaljenosti;

    @GET
    public Response posaljiStatus() {
        Response odgovor;

        OdgovorStatusUdaljenost adresaIPort = servisUdaljenosti.posaljiStatus();

        if (adresaIPort != null) {
            odgovor = Response.ok(adresaIPort).build();
        } else {
            odgovor = Response
                    .status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new RestOdgovor(false, "Nije uspostavljena komunikacija sa serverom udaljenosti"))
                    .build();
        }

        return odgovor;
    }

    @GET
    @Path("{komanda}")
    public Response posaljiNaredbu(@PathParam("komanda") String komanda) {

        Response odgovor;

        boolean uspjesanPronalazakKomande;
        ServerUdaljenostiNaredba naredba = null;

        try {
            naredba = ServerUdaljenostiNaredba.valueOf(komanda.toUpperCase());
            uspjesanPronalazakKomande = true;
        } catch (Exception e) {
            uspjesanPronalazakKomande = false;
        }

        if (komanda.equals("load") == false && uspjesanPronalazakKomande) {
            try {
                servisUdaljenosti.izvrsiNaredbu(naredba, null);
                odgovor = Response.ok(new RestOdgovor(true, "Naredba je izvršena")).build();
            } catch (ServerUdaljenostiIznimka ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
            } catch (SocketException ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
                Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE,
                        "Dogodio se problem pri pokušaju slanja komande na ServerUdaljenosti", ex);
            } catch (IOException ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false,
                                "Dogodio se problem pri povezivanju na ServerUdaljenosti"))
                        .build();
                Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE,
                        "Dogodio se problem pri povezivanju na ServerUdaljenosti", ex);
            }
            return odgovor;
        } else {
            String poruka;
            if (komanda.equals("load")) {
                poruka = "LOAD komanda dozvoljena je samo POST metodom.";
            } else {
                poruka = "Komanda nije ispravna.";
            }
            odgovor = Response.status(Status.METHOD_NOT_ALLOWED)
                    .entity(new RestOdgovor(false, poruka))
                    .build();
        }

        return odgovor;
    }

    @POST
    @Path("load")
    public Response posaljiLOAD(String tijeloPoruke) {
        Response odgovor;
        int brojAerodromaZaUcitavanje = -1;

        Gson gsonCitac = new Gson();
        try {
            Aerodrom[] aerodromi = gsonCitac.fromJson(tijeloPoruke, Aerodrom[].class);
            brojAerodromaZaUcitavanje = aerodromi.length;

            try {

                String odgovorServera = servisUdaljenosti.izvrsiNaredbu(
                        ServerUdaljenostiNaredba.LOAD, new String[] { tijeloPoruke });
                int brojUcitanihAerodroma = Integer.parseInt(odgovorServera.split(" ")[1]);

                if (brojAerodromaZaUcitavanje == brojUcitanihAerodroma) {
                    odgovor = Response.ok(new RestOdgovor(true, "Naredba je izvršena")).build();
                } else {
                    odgovor = Response.serverError()
                            .entity(new RestOdgovor(true, "Broj učitanih nije jednak broju poslanih")).build();
                }

            } catch (ServerUdaljenostiIznimka ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
            } catch (SocketException ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
                Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE,
                        "Dogodio se problem pri pokušaju slanja komande na ServerUdaljenosti", ex);
            } catch (IOException ex) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false,
                                "Dogodio se problem pri povezivanju na ServerUdaljenosti"))
                        .build();
                Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE,
                        "Dogodio se problem pri povezivanju na ServerUdaljenosti", ex);
            }

        } catch (JsonSyntaxException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, "Tijelo zahtjeva nije ispravan JSON format"))
                    .build();
        }

        return odgovor;
    }
}
