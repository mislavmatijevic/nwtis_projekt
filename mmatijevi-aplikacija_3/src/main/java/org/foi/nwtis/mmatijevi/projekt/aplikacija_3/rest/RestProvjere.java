package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.net.Authenticator;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NeovlasteniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NepostojeciZetonException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NovaOznakaNedostupnaException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisKorisnika;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisZetona;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("provjere")
public class RestProvjere extends Authenticator {

    @Inject
    ServisKorisnika servisPrijava;

    @Inject
    ServisZetona servisZetona;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticiraj(
            @HeaderParam("korisnik") String korime) {

        Response odgovor;

        try {
            Zeton zeton = servisZetona.stvoriNoviZeton(korime);

            if (zeton != null) {
                odgovor = Response.status(Status.OK).entity(zeton).build();
            } else {
                odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new RestOdgovor(false, "Žeton nije mogla biti stvoren."))
                        .build();
            }
        } catch (NovaOznakaNedostupnaException e) {
            odgovor = Response.status(Status.SERVICE_UNAVAILABLE)
                    .entity(new RestOdgovor(false, "Oznaka nije mogla biti stvorena."))
                    .build();
        }

        return odgovor;
    }

    @GET
    @Path("{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response provjeriToken(@PathParam("token") String token,
            @HeaderParam("korisnik") String korime) {

        Response odgovor;

        int zeton = -1;

        try {
            zeton = Integer.parseInt(token);
            boolean provjera = servisZetona.provjeriZeton(zeton, korime);
            if (provjera) {
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovor(true, "Žeton je valjan"))
                        .build();
            } else {
                odgovor = Response.status(Status.REQUEST_TIMEOUT)
                        .entity(new RestOdgovor(false, "Žeton je istekao"))
                        .build();
            }
        } catch (NumberFormatException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, "Žeton nije valjan"))
                    .build();
        } catch (NepostojeciZetonException ex) {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }

    @DELETE
    @Path("{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deaktivirajToken(@PathParam("token") String token,
            @HeaderParam("korisnik") String korime) {

        Response odgovor;

        int zeton = -1;

        try {
            zeton = Integer.parseInt(token);
            boolean provjera = servisZetona.provjeriZeton(zeton, korime);
            if (provjera) {
                servisZetona.deaktivirajZeton(zeton);
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovor(true, "Žeton je deaktiviran"))
                        .build();
            } else {
                odgovor = Response.status(Status.REQUEST_TIMEOUT)
                        .entity(new RestOdgovor(false, "Žeton je istekao"))
                        .build();
            }
        } catch (NumberFormatException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, "Žeton nije valjan"))
                    .build();
        } catch (NepostojeciZetonException ex) {
            odgovor = Response.status(Status.NOT_FOUND)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }

    @DELETE
    @Path("korisnik/{korisnik}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deaktivirajSveTokene(@PathParam("korisnik") String korime,
            @HeaderParam("korisnik") String aktivator) {

        Response odgovor;

        try {
            int brojDeaktiviranih = servisZetona.deaktivirajSveZetone(aktivator, korime);
            if (brojDeaktiviranih > 0) {
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovor(true,
                                brojDeaktiviranih + " žetona je deaktivirano za korisnika " + korime + ""))
                        .build();
            } else {
                odgovor = Response.status(Status.NOT_FOUND)
                        .entity(new RestOdgovor(false, "Korisnik nije imao aktivnih žetona"))
                        .build();
            }
        } catch (NumberFormatException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, "Žeton nije valjan"))
                    .build();
        } catch (NeovlasteniKorisnik ex) {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        } catch (NepostojeciZetonException ex) {
            odgovor = Response.status(Status.NOT_FOUND)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }
}
