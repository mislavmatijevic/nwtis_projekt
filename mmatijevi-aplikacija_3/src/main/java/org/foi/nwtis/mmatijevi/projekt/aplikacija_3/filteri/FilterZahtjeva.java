package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.filteri;

import java.io.IOException;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.sigurnost.ServisKorisnika;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilterProvjere implements ContainerRequestFilter {

    @Inject
    ServisKorisnika servisPrijava;

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
        }
    }

}
