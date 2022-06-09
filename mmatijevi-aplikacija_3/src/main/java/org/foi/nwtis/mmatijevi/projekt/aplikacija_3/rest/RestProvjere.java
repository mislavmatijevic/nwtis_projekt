package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.util.Date;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Odgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.OdgovorObjekt;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.Zeton;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.GeneratorOznakaZetona.NovaOznakaNedostupnaException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisPrijava;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisZetona;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("provjere")
public class RestProvjere {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticiraj(@HeaderParam("korisnik") String korime, @HeaderParam("lozinka") String lozinka) {
        Response odgovor = null;
        boolean uspjehPrijave = ServisPrijava.dajInstancu().prijaviKorisnika(korime, lozinka);

        if (uspjehPrijave) {
            try {
                Zeton zeton = ServisZetona.dajInstancu().stvoriNoviZeton(korime);

                if (zeton == null) {
                    odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Odgovor(false, "Žeton nije mogla biti stvoren."))
                            .build();
                } else {

                    long vrijemeMs = new Date().getTime();
                    int vrijeme = (int) (vrijemeMs / 1000);

                    odgovor = Response.status(Status.SERVICE_UNAVAILABLE)
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
}
