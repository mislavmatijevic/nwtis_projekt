package org.foi.nwtis.mmatijevi.projekt.modeli;

import java.util.List;

import org.foi.nwtis.podaci.Aerodrom;

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
    private int aktivnaStranica;
    private int brojStranica;
    private int dostupnoAerodroma;
    private int kolicinaPodataka;
    private List<Aerodrom> podaciAerodroma;
}
