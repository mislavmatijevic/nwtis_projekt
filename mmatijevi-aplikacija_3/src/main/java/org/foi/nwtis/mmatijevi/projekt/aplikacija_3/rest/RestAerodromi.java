package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.AerodromVecPracenException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.InformacijeLeta;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisAerodroma;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisAerodroma.VrstaTablice;
import org.foi.nwtis.podaci.Aerodrom;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("aerodromi")
public class RestAerodromi {

    @Inject
    ServisAerodroma servisAerodroma;

    /** 
     * Metoda vraća sve aerodrome u JSON formatu <strong>ILI</strong> 
     * metoda vraća sve aerodrome za koje se preuzimaju podaci pozadinskom dretvom.
     * <p> - To ovisi o postojanju parametra "preuzimanje".
     * <p>Odgovor je u JSON formatu.
     * @param context Kontekst Servleta Kontekst Servleta
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response dajSveAerodrome(
            @QueryParam("preuzimanje") String preuzimanje,
            @QueryParam("stranica") String stranica) {
        Response odgovor;

        int stranicaBrojcana;

        try {
            Object aerodromi;
            if (preuzimanje == null) {

                try {
                    stranicaBrojcana = Integer.parseInt(stranica);
                } catch (Exception e) {
                    stranicaBrojcana = 1;
                }
                aerodromi = servisAerodroma.dohvatiAerodrome(stranicaBrojcana);

            } else {
                aerodromi = servisAerodroma.dohvatiPraceneAerodrome();
            }

            if (aerodromi == null) {
                odgovor = Response
                        .serverError()
                        .entity(new RestOdgovor(false, "Aerodromi nisu mogli biti dohvaćeni"))
                        .build();
            } else {
                odgovor = Response.status(Status.OK).entity(aerodromi).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Dogodio se problem: " + ex.getLocalizedMessage() + ".").build();
        }

        return odgovor;
    }

    /** 
     * Metoda dodaje jedan aerodrom za praćenje.
     * <p>Aerodrom se dodaje tako da se u JSON tijelu pošalje vrijednost <pre>"icao":"{icao}"</pre>
     * @param context Kontekst Servleta
     * @param aerodrom Čita se iz zahtjeva, to je objekt aerodroma kojemu je nužno upisati samo 'icao'.
     * @return Response Odgovor korisniku.
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response dodajAerodromZaPratiti(Aerodrom aerodrom) {
        Response odgovor = null;

        if (aerodrom == null || aerodrom.getIcao() == null || aerodrom.getIcao().length() != 4) {
            odgovor = Response.status(Status.BAD_REQUEST).entity("ICAO oznaka mora biti postavljena!").build();
        } else {
            try {
                if (servisAerodroma.unesiAerodromZaPratiti(aerodrom.getIcao())) {
                    odgovor = Response.status(Status.CREATED)
                            .entity("Aerodrom '" + aerodrom.getIcao() + "' dodan u praćenje.").build();
                } else {
                    odgovor = Response.status(Status.NOT_FOUND)
                            .entity("Aerodrom '" + aerodrom.getIcao() + "' nije pronađen.").build();
                }
            } catch (AerodromVecPracenException ex) {
                odgovor = Response.status(Status.FORBIDDEN)
                        .entity(ex.getLocalizedMessage()).build();
            } catch (Exception ex) {
                ex.printStackTrace();
                odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Dogodio se problem: " + ex.getLocalizedMessage() + ".").build();
            }
        }

        return odgovor;
    }

    /** 
     * Metoda vraća jedan aerodrom u JSON formatu.
     * @param context Kontekst Servleta
     * @param icao ICAO oznaka aerodroma nad kojim metoda radi.
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{icao}")
    public Response dajAerodrom(@PathParam("icao") String icao) {
        Response odgovor;

        try {
            Aerodrom aerodrom = servisAerodroma.dohvatiAerodrom(icao);
            if (aerodrom != null) {
                odgovor = Response.status(Status.OK).entity(aerodrom).build();
            } else {
                odgovor = Response.status(Status.NOT_FOUND).entity("Nema aerodroma '" + icao + "'").build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Dogodio se problem: " + ex.getLocalizedMessage() + ".").build();
        }

        return odgovor;
    }

    /** 
     * Metoda vraća sve polaske s jednoga aerodroma u JSON formatu.
     * @param icao ICAO oznaka aerodroma nad kojim metoda radi.
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{icao}/polasci")
    public Response dajPolaskeAerodoma(
            @PathParam("icao") String icao,
            @QueryParam("vrsta") int vrsta,
            @QueryParam("od") String vrijemeOd,
            @QueryParam("do") String vrijemeDo) {
        Response odgovor = null;

        Date datumOd = null;
        Date datumDo = null;

        switch (vrsta) {
            case 0:
                datumOd = izvuciDatumCitljivi(vrijemeOd);
                datumDo = izvuciDatumCitljivi(vrijemeDo);
                break;
            case 1:
                datumOd = izvuciDatumUnix(vrijemeOd);
                datumDo = izvuciDatumUnix(vrijemeDo);
                break;
            default:
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity("Dodajte parametar '?vrsta' s vrijednošću 0 ili 1.").build();
                break;
        }

        if (odgovor == null) {

            if (datumOd == null || datumDo == null) {
                String ispravanFormat = vrsta == 0 ? "{dd.mm.gggg}" : "{broj sekundi od 1.1.1970.}";
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity("Ispravno unesite parametre 'od' i 'do' u formatu " + ispravanFormat).build();
            } else {
                try {
                    List<InformacijeLeta> aktivnostiAerodroma = servisAerodroma.dohvatiPraceneLetoveZaAerodrom(
                            icao, datumOd, datumDo, VrstaTablice.AERODROMI_POLASCI);
                    if (aktivnostiAerodroma.size() != 0) {
                        odgovor = Response.status(Status.OK).entity(aktivnostiAerodroma).build();
                    } else {
                        odgovor = Response.status(Status.NOT_FOUND)
                                .entity("Za taj datum nisu pronađeni polasci s aerodroma '" + icao + "'.").build();
                    }
                } catch (DateTimeParseException ex) {
                    odgovor = Response.status(Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity("Dogodio se problem: " + ex.getLocalizedMessage() + ".").build();
                }
            }
        }

        return odgovor;
    }

    /** 
     * Metoda vraća sve dolaske s jednoga aerodroma u JSON formatu.
     * @param icao ICAO oznaka aerodroma nad kojim metoda radi.
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{icao}/dolasci")
    public Response dajDolaskeAerodoma(@PathParam("icao") String icao, @QueryParam("vrsta") int vrsta,
            @QueryParam("od") String vrijemeOd,
            @QueryParam("do") String vrijemeDo) {
        Response odgovor = null;

        Date datumOd = null;
        Date datumDo = null;

        switch (vrsta) {
            case 0:
                datumOd = izvuciDatumCitljivi(vrijemeOd);
                datumDo = izvuciDatumCitljivi(vrijemeDo);
                break;
            case 1:
                datumOd = izvuciDatumUnix(vrijemeOd);
                datumDo = izvuciDatumUnix(vrijemeDo);
                break;
            default:
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity("Dodajte parametar '?vrsta' s vrijednošću 0 ili 1.").build();
                break;
        }

        if (odgovor == null) {

            if (datumOd == null || datumDo == null) {
                String ispravanFormat = vrsta == 0 ? "{dd.mm.gggg}" : "{broj sekundi od 1.1.1970.}";
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity("Ispravno unesite parametre 'od' i 'do' u formatu " + ispravanFormat).build();
            } else {
                try {
                    List<InformacijeLeta> aktivnostiAerodroma = servisAerodroma.dohvatiPraceneLetoveZaAerodrom(
                            icao, datumOd, datumDo, VrstaTablice.AERODROMI_DOLASCI);
                    if (aktivnostiAerodroma.size() != 0) {
                        odgovor = Response.status(Status.OK).entity(aktivnostiAerodroma).build();
                    } else {
                        odgovor = Response.status(Status.NOT_FOUND)
                                .entity("Za taj datum nisu pronađeni dolasci na aerodrom '" + icao + "'.").build();
                    }
                } catch (DateTimeParseException ex) {
                    odgovor = Response.status(Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity("Dogodio se problem: " + ex.getLocalizedMessage() + ".").build();
                }
            }
        }

        return odgovor;
    }

    /** Izvlači datum u formatu 'dd.mm.gggg.' i pretvara ga u objekt klase Date (java.util).
     * @param datum Datum kao znakovni niz u formatu 'dd.mm.gggg.'
     * @return Date Objekt datuma iz znakovnog niza.
     * @throws Exception Neuspjela pretvorba znakovnog niza, vraća se poruka/uputa korisniku.
     */
    private Date izvuciDatumCitljivi(String datum) throws DateTimeParseException {
        Date datumObjekt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            datumObjekt = sdf.parse(datum);
        } catch (ParseException e) {
            throw new DateTimeParseException(
                    "Datum nije u ispravnom formatu. Provjerite da je vrijednost parametra 'dan' u formatu 'dd.mm.gggg'",
                    datum, 0);
        }
        return datumObjekt;
    }

    /** Izvlači datum u UNIX Timestamp formatu i pretvara ga u objekt klase Date (java.util).
     * @param trenutak Trenutak kao znakovni niz broja sekundi od 1.1.1970.
     * @return Date Objekt datuma iz znakovnog niza.
     * @throws Exception Neuspjela pretvorba znakovnog niza, vraća se poruka/uputa korisniku.
     */
    private Date izvuciDatumUnix(String trenutak) {
        Date datumObjekt = new Date();
        int trenutakBrojcani = Integer.parseInt(trenutak);
        datumObjekt.setTime(trenutakBrojcani * 1000);
        return datumObjekt;
    }
}
