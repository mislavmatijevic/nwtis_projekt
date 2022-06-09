package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli;

import jakarta.json.bind.annotation.JsonbProperty;
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
    @JsonbProperty(value = "zeton")
    private int oznaka;
    /**
     * Vrijeme u sekundama!
     */
    @JsonbProperty(value = "vrijeme")
    private int vrijeme;
}
