package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.json.bind.annotation.JsonbProperty;

public class AvionLetiPrikaz extends AvionLeti {

    @JsonbProperty("icao24")
    private String icao24;
    @JsonbProperty("first_seen")
    private int firstSeen;
    @JsonbProperty("est_departure_airport")
    private String estDepartureAirport;
    @JsonbProperty("last_seen")
    private int lastSeen;
    @JsonbProperty("est_arrival_airport")
    private String estArrivalAirport;
    @JsonbProperty("callsign")
    private String callsign;
    @JsonbProperty("est_departure_airport_horizDistance")
    private int estDepartureAirportHorizDistance;
    @JsonbProperty("est_departure_airport_vertDistance")
    private int estDepartureAirportVertDistance;
    @JsonbProperty("est_arrival_airport_horizDistance")
    private int estArrivalAirportHorizDistance;
    @JsonbProperty("est_arrival_airport_vertDistance")
    private int estArrivalAirportVertDistance;
    @JsonbProperty("departure_airport_candidates_count")
    private int departureAirportCandidatesCount;
    @JsonbProperty("arrival_airport_candidates_count")
    private int arrivalAirportCandidatesCount;

    public AvionLetiPrikaz() {
        super();
    }

    public AvionLetiPrikaz(String icao24, int firstSeen, String estDepartureAirport, int lastSeen,
            String estArrivalAirport, String callsign, int estDepartureAirportHorizDistance,
            int estDepartureAirportVertDistance, int estArrivalAirportHorizDistance, int estArrivalAirportVertDistance,
            int departureAirportCandidatesCount, int arrivalAirportCandidatesCount) {
        super(icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport, callsign,
                estDepartureAirportHorizDistance, estDepartureAirportVertDistance, estArrivalAirportHorizDistance,
                estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount);
    }
}