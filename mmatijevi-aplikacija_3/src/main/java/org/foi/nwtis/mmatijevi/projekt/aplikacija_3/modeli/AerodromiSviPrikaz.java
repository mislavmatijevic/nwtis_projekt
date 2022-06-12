package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import java.util.List;

import org.foi.nwtis.podaci.Aerodrom;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasa koja služi za prikaz odgovora na zahtjev dohvata svih aerodroma.
 * Najbitnije kod nje jest to što sadržava informacije o straničenju.
 */
@Getter
@Setter
@AllArgsConstructor
public class AerodromiSviPrikaz {
    @JsonbProperty("aktivna_stranica")
    private int trenutnaStranica;
    @JsonbProperty("broj_stranica")
    private int ukupnoStranica;
    @JsonbProperty("broj_aerodroma")
    private int ukupnoAerodroma;
    @JsonbProperty("kolicina_podataka")
    private int dohvacenoAerodroma;
    @JsonbProperty("podaci")
    private List<Aerodrom> aerodromi;
}
