package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("aerodromi")
public class RestAerodromi {

    @GET
    @Path("bok")
    public Response bok() {
        return Response.ok("Bok").build();
    }
}
