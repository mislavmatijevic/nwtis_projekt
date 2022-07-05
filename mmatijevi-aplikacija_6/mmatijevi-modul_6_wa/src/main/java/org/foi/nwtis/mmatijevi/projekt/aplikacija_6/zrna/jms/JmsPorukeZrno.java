package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.modeli.JmsPoruka;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Named;

@Startup
@Singleton
@Named("jmsPorukeZrno")
public class JmsPorukeZrno implements Serializable {
    private LinkedList<JmsPoruka> listaPoruka = new LinkedList<JmsPoruka>();

    public LinkedList<JmsPoruka> getListaPoruka() {
        return listaPoruka;
    }

    public void setListaPoruka(LinkedList<JmsPoruka> listaPoruka) {
        this.listaPoruka = listaPoruka;
    }

    public void zabiljeziPoruku(String poruka) {
        listaPoruka.addFirst(new JmsPoruka(poruka, new Date()));
    }

    public void ocistiPoruke() {
        listaPoruka.clear();
    }
}
