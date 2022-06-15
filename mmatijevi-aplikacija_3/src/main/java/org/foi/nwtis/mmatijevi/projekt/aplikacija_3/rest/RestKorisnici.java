package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisKorisnika;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNeispravanException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikVecPostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikPrikaz;
import org.foi.nwtis.mmatijevi.projekt.modeli.KorisnikRegistracija;
import org.foi.nwtis.mmatijevi.projekt.modeli.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.modeli.RestOdgovorUzPodatke;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("korisnici")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestKorisnici {

    @Inject
    ServisKorisnika servisKorisnika;

    @GET
    public Response dohvatiSveKorisnike() {
        Response odgovor;

        List<KorisnikPrikaz> korisnici = servisKorisnika.dajSveKorisnike();
        if (korisnici != null) {
            odgovor = Response.ok(new RestOdgovorUzPodatke<List<KorisnikPrikaz>>(
                    true, "Dohvat svih korisnika uspješan", korisnici))
                    .build();
        } else {
            odgovor = Response.serverError()
                    .entity(new RestOdgovor(false, "Korisnici nisu mogli biti dohvaćeni"))
                    .build();
        }

        return odgovor;
    }

    @POST
    public Response registrirajNovogKorisnika(KorisnikRegistracija korisnik) {
        Response odgovor = null;

        if (korisnik != null) {
            try {
                boolean uspjeh = servisKorisnika.registrirajNovogKorisnika(korisnik);
                if (uspjeh) {
                    odgovor = Response.ok(new RestOdgovor(true, "Korisnik je dodan")).build();
                } else {
                    odgovor = Response.serverError()
                            .entity(new RestOdgovor(false, "Korisnik nije mogao biti dodan"))
                            .build();
                }
            } catch (KorisnikNeispravanException ex) {
                odgovor = Response.status(Response.Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
            } catch (KorisnikVecPostojiException ex) {
                odgovor = Response.status(Response.Status.CONFLICT)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                        .build();
            }
        }

        return odgovor;
    }

    @GET
    @Path("{korisnik}")
    public Response dohvatiKorisnika(@PathParam("korisnik") String korime) {
        Response odgovor;

        KorisnikPrikaz pronadjeniKorisnik;
        try {
            pronadjeniKorisnik = servisKorisnika.dohvatiJednogKorisnika(korime);
            if (pronadjeniKorisnik != null) {
                odgovor = Response.ok(new RestOdgovorUzPodatke<KorisnikPrikaz>(
                        true, "Dohvat korisnika uspješan", pronadjeniKorisnik))
                        .build();
            } else {
                odgovor = Response.serverError()
                        .entity(new RestOdgovor(false, "Korisnik nije mogao biti pronađen."))
                        .build();
            }
        } catch (KorisnikNePostojiException ex) {
            odgovor = Response.status(Response.Status.NOT_FOUND)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }

    @GET
    @Path("{korisnik}/grupe")
    public Response dohvatiGrupeKorisnika(@PathParam("korisnik") String korime) {
        Response odgovor;

        List<String> pronadjeneGrupe;
        try {
            pronadjeneGrupe = servisKorisnika.dohvatiGrupeKorisnika(korime);
            if (pronadjeneGrupe != null) {
                if (pronadjeneGrupe.size() > 0) {
                    odgovor = Response.ok(new RestOdgovorUzPodatke<List<String>>(
                            true, "Dohvat grupa korisnika uspješan", pronadjeneGrupe))
                            .build();
                } else {
                    odgovor = Response.ok(new RestOdgovor(true, "Korisnik nije niti u jednoj grupi"))
                            .build();
                }
            } else {
                odgovor = Response.serverError()
                        .entity(new RestOdgovor(false, "Grupe nisu mogle biti pronađene."))
                        .build();
            }
        } catch (KorisnikNePostojiException ex) {
            odgovor = Response.serverError()
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        }

        return odgovor;
    }
}
