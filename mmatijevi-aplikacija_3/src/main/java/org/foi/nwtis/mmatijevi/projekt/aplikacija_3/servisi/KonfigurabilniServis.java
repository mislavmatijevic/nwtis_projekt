package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;

public abstract class KonfigurabilniServis {

    protected KonfiguracijaBP konfig = null;

    public void postaviKonfiguraciju(KonfiguracijaBP konfig) {
        this.konfig = konfig;
    }
}
