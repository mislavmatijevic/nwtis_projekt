package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.filteri;

import java.io.IOException;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.ZetonNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisKorisnika;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisZetona;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilterZahtjeva implements ContainerRequestFilter {

    @Inject
    ServisKorisnika servisPrijava;
    @Inject
    ServisZetona servisZetona;

    /**
     * Obavlja filtriranje zahtjeva s obzirom na putanju.
     * <ul>
     *  <li> Ako je putanja "provjere", provjerava korisnika i lozinku iz zaglavlja.
     *  <li> Ako je putanja neka druga, provjerava korisnika i žeton iz zaglavlja.
     * </ul>
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String putanja = requestContext.getUriInfo().getRequestUri().getPath();
        String servis = putanja.split("/")[3];
        if (servis.equals("provjere")) {

            String korime = requestContext.getHeaderString("korisnik");
            String lozinka = requestContext.getHeaderString("lozinka");
            boolean prijavaUspjesna = false;

            if (korime != null && lozinka != null && !korime.isBlank() && !lozinka.isBlank()) {
                prijavaUspjesna = servisPrijava.prijaviKorisnika(korime, lozinka);
            }

            if (!prijavaUspjesna) {
                Response odgovorNeuspjeh = Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new RestOdgovor(false, "Prijava neuspješna!"))
                        .build();
                requestContext.abortWith(odgovorNeuspjeh);
            }
        } else {
            Response odgovorNeuspjeh = null;

            String korime = requestContext.getHeaderString("korisnik");
            String token = requestContext.getHeaderString("token");

            if (korime != null && !korime.isBlank() && token != null && !token.isBlank()) {
                try {
                    int zeton = Integer.parseInt(token);
                    boolean provjera = servisZetona.provjeriZeton(zeton, korime);
                    if (!provjera) {
                        odgovorNeuspjeh = Response.status(Status.REQUEST_TIMEOUT)
                                .entity(new RestOdgovor(false, "Žeton je istekao"))
                                .build();
                    }
                } catch (NumberFormatException ex) {
                    odgovorNeuspjeh = Response.status(Status.BAD_REQUEST)
                            .entity(new RestOdgovor(false, "Žeton nije valjan"))
                            .build();
                } catch (ZetonNePostojiException ex) {
                    odgovorNeuspjeh = Response.status(Status.UNAUTHORIZED)
                            .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                            .build();
                }
            }

            if (odgovorNeuspjeh != null) {
                requestContext.abortWith(odgovorNeuspjeh);
            }
        }
    }
}
