package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisAerodroma;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi.ServisAerodroma.VrstaTablice;
import org.foi.nwtis.mmatijevi.projekt.iznimke.AerodromVecPracenException;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ServerUdaljenostiIznimka;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovor;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorAerodrom;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorPodaciLetova;
import org.foi.nwtis.mmatijevi.projekt.odgovori.RestOdgovorUzPodatke;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti;
import org.foi.nwtis.mmatijevi.projekt.usluge.PosluziteljUdaljenosti.ServerUdaljenostiNaredba;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

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
    @Inject
    PosluziteljUdaljenosti servisUdaljenosti;

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
    public Response dajAerodrome(
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
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovorUzPodatke<>(true, "Dohvat uspješan!", aerodromi)).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new RestOdgovor(false, "Dogodio se problem: " + ex.getLocalizedMessage() + ".")).build();
        }

        return odgovor;
    }

    /** 
     * Metoda dodaje jedan aerodrom za praćenje.
     * <p>Aerodrom se dodaje tako da se u JSON tijelu pošalje vrijednost <pre>{"icao":"{icao}"}</pre>
     * @param context Kontekst Servleta
     * @param aerodrom Čita se iz zahtjeva, to je objekt aerodroma kojemu je nužno upisati samo 'icao'.
     * @return Response Odgovor korisniku.
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response dodajAerodromZaPratiti(Aerodrom aerodrom) {
        Response odgovor = null;

        if (aerodrom == null || aerodrom.getIcao() == null || aerodrom.getIcao().length() != 4) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, "ICAO oznaka mora biti postavljena!")).build();
        } else {
            try {
                if (servisAerodroma.unesiAerodromZaPratiti(aerodrom.getIcao())) {
                    odgovor = Response.status(Status.CREATED)
                            .entity(new RestOdgovor(true, "Aerodrom '" + aerodrom.getIcao() + "' dodan u praćenje."))
                            .build();
                } else {
                    odgovor = Response.status(Status.NOT_FOUND)
                            .entity(new RestOdgovor(false, "Aerodrom '" + aerodrom.getIcao() + "' nije pronađen."))
                            .build();
                }
            } catch (AerodromVecPracenException ex) {
                odgovor = Response.status(Status.FORBIDDEN)
                        .entity(new RestOdgovor(false, ex.getLocalizedMessage())).build();
            } catch (Exception ex) {
                ex.printStackTrace();
                odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new RestOdgovor(false, "Dogodio se problem: " + ex.getLocalizedMessage() + "."))
                        .build();
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
                odgovor = Response.status(Status.OK)
                        .entity(new RestOdgovorAerodrom(true, "Aerodrom [" + icao + "] dohvaćen", aerodrom)).build();
            } else {
                odgovor = Response.status(Status.NOT_FOUND)
                        .entity(new RestOdgovor(false, "Nema aerodroma '" + icao + "'")).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new RestOdgovor(false, "Dogodio se problem: " + ex.getLocalizedMessage() + ".")).build();
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
            @QueryParam("vrsta") String vrsta,
            @QueryParam("od") String vrijemeOd,
            @QueryParam("do") String vrijemeDo) {

        Response odgovor = dajAktivnostAerodroma(icao, VrstaTablice.AERODROMI_POLASCI, vrsta, vrijemeOd, vrijemeDo);
        return odgovor;

    }

    /** 
     * Metoda vraća sve dolaske na jedan aerodrom u JSON formatu.
     * @param icao ICAO oznaka aerodroma nad kojim metoda radi.
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{icao}/dolasci")
    public Response dajDolaskeAerodoma(
            @PathParam("icao") String icao,
            @QueryParam("vrsta") String vrsta,
            @QueryParam("od") String vrijemeOd,
            @QueryParam("do") String vrijemeDo) {

        Response odgovor = dajAktivnostAerodroma(icao, VrstaTablice.AERODROMI_DOLASCI, vrsta, vrijemeOd, vrijemeDo);
        return odgovor;

    }

    /** 
     * Metoda vraća sve dolaske na jedan aerodrom u JSON formatu.
     * @param icao ICAO oznaka aerodroma nad kojim metoda radi.
     * @return Response Odgovor korisniku.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{icao1}/{icao2}")
    public Response dajUdaljenostDvaAerodroma(
            @PathParam("icao1") String icao1,
            @PathParam("icao2") String icao2) {

        Response odgovor;

        try {

            String odgovorServera = servisUdaljenosti.izvrsiNaredbu(
                    ServerUdaljenostiNaredba.DISTANCE, new String[] { icao1, icao2 });

            int udaljenost = Integer.parseInt(odgovorServera.split("OK ")[1]);

            odgovor = Response.ok(new RestOdgovor(true, String.valueOf(udaljenost))).build();

        } catch (ServerUdaljenostiIznimka ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                    .build();
        } catch (NumberFormatException ex) {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false,
                            "Server je odgovorio neispravnom vrijednošću: " + ex.getLocalizedMessage().split("\"")[1]))
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
    }

    /**
     * Obavlja dohvat polazaka ili dolazaka iz tablice.
     * @param icao
     * @param aktivnost
     * @param vrsta
     * @param vrijemeOd
     * @param vrijemeDo
     * @param odgovor
     * @return
     */
    private Response dajAktivnostAerodroma(String icao, VrstaTablice relevantnaTablica,
            String vrstaZnakovna, String vrijemeOd, String vrijemeDo) {
        Response odgovor = null;

        int vrsta;

        try {
            vrsta = Integer.parseInt(vrstaZnakovna);
        } catch (Exception e) {
            vrsta = -1;
        }

        if ((vrsta == 0 || vrsta == 1) && icao != null && vrstaZnakovna != null && vrijemeOd != null
                && vrijemeDo != null) {

            Date datumOd = null;
            Date datumDo = null;

            switch (vrsta) {
                case 0:
                    try {
                        datumOd = izvuciDatumCitljivi(vrijemeOd);
                        datumDo = izvuciDatumCitljivi(vrijemeDo);
                    } catch (DateTimeParseException ex) {
                        odgovor = Response.status(Status.BAD_REQUEST)
                                .entity(new RestOdgovor(false, ex.getLocalizedMessage()))
                                .build();
                    }
                    break;
                case 1:
                    try {
                        datumOd = izvuciDatumUnix(vrijemeOd);
                        datumDo = izvuciDatumUnix(vrijemeDo);
                    } catch (NumberFormatException ex) {
                        odgovor = Response.status(Status.BAD_REQUEST)
                                .entity(new RestOdgovor(false,
                                        "Vrijednost " + ex.getLocalizedMessage().split("\"")[1] + " nije ispravna"))
                                .build();
                    }
                    break;
                default:
                    odgovor = Response.status(Status.BAD_REQUEST)
                            .entity(new RestOdgovor(false, "Dodajte parametar '?vrsta' s vrijednošću 0 ili 1."))
                            .build();
                    break;
            }

            if (datumOd.compareTo(datumDo) > 0) {
                odgovor = Response.status(Status.BAD_REQUEST)
                        .entity(new RestOdgovor(false,
                                "Početak vremenskog raspona ('od') ne može biti nakon kraja vremenskog raspona ('do')."))
                        .build();
            }

            if (odgovor == null) {
                try {
                    List<AvionLeti> letovi = servisAerodroma.dohvatiPraceneLetoveZaAerodrom(
                            icao, datumOd, datumDo, relevantnaTablica);
                    if (letovi.size() > 0) {
                        odgovor = Response
                                .status(Status.OK)
                                .entity(new RestOdgovorPodaciLetova(true,
                                        "Dohvaćeni podaci iz tablice " + relevantnaTablica + " za vremenski raspon "
                                                + datumOd + " - " + datumDo,
                                        letovi))
                                .build();
                    } else {
                        odgovor = Response.status(Status.NOT_FOUND)
                                .entity(new RestOdgovor(false,
                                        "Za te parametre nisu pronađeni rezultati za aerodrom '" + icao + "'."))
                                .build();
                    }
                } catch (DateTimeParseException ex) {
                    odgovor = Response.status(Status.BAD_REQUEST)
                            .entity(new RestOdgovor(false, ex.getLocalizedMessage())).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    odgovor = Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new RestOdgovor(false, "Dogodio se problem: " + ex.getLocalizedMessage() + "."))
                            .build();
                }
            }

        } else {
            odgovor = Response.status(Status.BAD_REQUEST)
                    .entity(new RestOdgovor(false,
                            "Zahtjev nije ispravan bez vremenskog intervala zadanog parametrima 'vrsta', 'od' i 'do'."))
                    .build();
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
        long trenutakBrojcani = Long.parseLong(trenutak);
        return new Date(trenutakBrojcani * 1000);
    }
}
