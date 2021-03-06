package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.net.Authenticator;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisKorisnika;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisZetona;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeovlastenException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.NovaOznakaNedostupnaException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovor;

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
@Produces(MediaType.APPLICATION_JSON)
public class RestProvjere extends Authenticator {

    @Inject
    ServisKorisnika servisPrijava;

    @Inject
    ServisZetona servisZetona;

    @GET
    public Response autenticiraj(
            @HeaderParam("korisnik") String korime) {

        Response odgovor;

        try {
            Zeton zeton = servisZetona.stvoriNoviZeton(korime);

            if (zeton != null) {
                odgovor = Response.status(Status.OK).entity(zeton).build();
            } else {
                odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new RestOdgovor(false, "Žeton nije mogao biti stvoren."))
                        .build();
            }
        } catch (NovaOznakaNedostupnaException e) {
            odgovor = Response.status(Status.SERVICE_UNAVAILABLE)
                    .entity(new RestOdgovor(false, "Žeton nije mogao biti stvorena."))
                    .build();
        }

        return odgovor;
    }

    @GET
    @Path("{token}")
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
        } catch (ZetonNePostojiException ex) {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }

    @DELETE
    @Path("{token}")
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
        } catch (ZetonNePostojiException ex) {
            odgovor = Response.status(Status.NOT_FOUND)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }

    @DELETE
    @Path("korisnik/{korisnik}")
    public Response deaktivirajSveTokene(@PathParam("korisnik") String korime,
            @HeaderParam("korisnik") String aktivator) {

        Response odgovor;

        try {
            int brojDeaktiviranih = servisZetona.deaktivirajSveZetone(aktivator, korime);
            if (brojDeaktiviranih > 0) {
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovor(true,
                                brojDeaktiviranih + " žeton"
                                        + (brojDeaktiviranih % 10 == 1 && brojDeaktiviranih % 100 != 11
                                                ? " je deaktiviran"
                                                : "a je deaktivirano")
                                        + " za korisnika " + korime + ""))
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
        } catch (KorisnikNeovlastenException ex) {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        } catch (ZetonNePostojiException ex) {
            odgovor = Response.status(Status.NOT_FOUND)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }
}
