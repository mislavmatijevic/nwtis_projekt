package org.foi.nwtis.mmatijevi.projekt.modeli;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Autentifikacijski mehanizam.
 * Žeton predstavlja sredstvo identifikacije korisnika u ovakvom RESTful sustavu.
 */
@Getter
@Setter
@AllArgsConstructor
public class Zeton {
    /** Oznaka žetona */
    private int zeton;
    /**
     * Vrijeme u sekundama!
     */
    private int vrijeme;
}
