package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.util.Date;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.KriviKorisnikException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.NovaOznakaNedostupnaException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Odgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.OdgovorObjekt;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisPrijava;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisZetona;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("provjere")
public class RestProvjere {

    @Inject
    ServisPrijava servisPrijava;

    @Inject
    ServisZetona servisZetona;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticiraj(@HeaderParam("korisnik") String korime, @HeaderParam("lozinka") String lozinka) {
        Response odgovor;
        boolean uspjehPrijave = servisPrijava.prijaviKorisnika(korime, lozinka);

        if (uspjehPrijave) {
            try {
                Zeton zeton = servisZetona.stvoriNoviZeton(korime);

                if (zeton == null) {
                    odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Odgovor(false, "Žeton nije mogla biti stvoren."))
                            .build();
                } else {

                    long vrijemeMs = new Date().getTime();
                    int vrijeme = (int) (vrijemeMs / 1000);

                    odgovor = Response.status(Status.OK)
                            .entity(new OdgovorObjekt<Zeton>(true,
                                    "Žeton vrijedi " + (zeton.getVrijeme() - vrijeme) + " sekundi.", zeton))
                            .build();
                }
            } catch (NovaOznakaNedostupnaException e) {
                odgovor = Response.status(Status.SERVICE_UNAVAILABLE)
                        .entity(new Odgovor(false, "Oznaka nije mogla biti stvorena."))
                        .build();
            }
        } else {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new Odgovor(false, "Takav korisnik nije pronađen"))
                    .build();
        }

        return odgovor;
    }

    @GET
    @Path("{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response provjeriToken(@HeaderParam("korisnik") String korime, @PathParam("token") String token) {
        Response odgovor;

        int zeton = -1;

        try {
            zeton = Integer.parseInt(token);
            boolean provjera = servisZetona.provjeriZeton(zeton, korime);
            if (provjera) {
                odgovor = Response.status(Status.OK)
                        .entity(new Odgovor(true, "Žeton je valjan"))
                        .build();
            } else {
                odgovor = Response.status(Status.REQUEST_TIMEOUT)
                        .entity(new Odgovor(false, "Žeton je istekao"))
                        .build();
            }
        } catch (NumberFormatException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new Odgovor(false, "Broj nije valjan"))
                    .build();
        } catch (KriviKorisnikException ex) {
            odgovor = Response.status(Status.UNAUTHORIZED)
                    .entity(new Odgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }
}
