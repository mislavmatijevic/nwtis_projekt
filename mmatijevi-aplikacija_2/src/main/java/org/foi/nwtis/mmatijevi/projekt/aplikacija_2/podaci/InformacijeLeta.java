package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci;

import java.util.Date;

/**
 * Ova klasa sadržava informacije o letu.
 */
public class InformacijeLeta {
    private String icaoPolazak;
    private String icaoDolazak;
    private Date pocetakLeta;
    private Date krajLeta;
    private String callsign;

    public InformacijeLeta() {
    }

    public InformacijeLeta(String icaoPolazak, String icaoDolazak, Date pocetakLeta, Date krajLeta, String callsign) {
        this.icaoPolazak = icaoPolazak;
        this.icaoDolazak = icaoDolazak;
        this.pocetakLeta = pocetakLeta;
        this.krajLeta = krajLeta;
        this.callsign = callsign;
    }

    public String getIcaoPolazak() {
        return this.icaoPolazak;
    }

    public void setIcaoPolazak(String icaoPolazak) {
        this.icaoPolazak = icaoPolazak;
    }

    public String getIcaoDolazak() {
        return this.icaoDolazak;
    }

    public void setIcaoDolazak(String icaoDolazak) {
        this.icaoDolazak = icaoDolazak;
    }

    public Date getPocetakLeta() {
        return this.pocetakLeta;
    }

    public void setPocetakLeta(Date pocetakLeta) {
        this.pocetakLeta = pocetakLeta;
    }

    public Date getKrajLeta() {
        return this.krajLeta;
    }

    public void setKrajLeta(Date krajLeta) {
        this.krajLeta = krajLeta;
    }

    public String getCallsign() {
        return this.callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

}
