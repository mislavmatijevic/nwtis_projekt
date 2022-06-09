package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci;

import java.io.Serializable;

/**
 * Ova klasa služi za prikaz problema iz baze.
 */
public class ProblemDTO implements Serializable {
    private String ident;
    private String description;
    private long stored;

    public String getIdent() {
        return this.ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStored() {
        return this.stored;
    }

    public void setStored(long stored) {
        this.stored = stored;
    }
}
