package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;

import jakarta.inject.Singleton;

/**
 * Ova klasa omogućuje stvaranje i korištenje veze s pravom bazom podataka na siguran način.
 */
@Singleton
public class Baza {

    Connection veza = null;

    /** 
     * Po konfiguracijskoj datoteci stvara vezu na bazu.
     * Očekuje se da korisnik klase zatvori vezu nakon završetka.
     * @param bp Konfiguracijska datoteka baze.
     * @return Connection Ostvarena veza na bazu.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection stvoriVezu(KonfiguracijaBP bp) throws ClassNotFoundException, SQLException {
        if (this.veza == null || this.veza.isClosed()) {
            Class.forName(bp.getDriverDatabase(bp.getServerDatabase()));
            this.veza = DriverManager.getConnection(
                    bp.getServerDatabase() + bp.getUserDatabase(),
                    bp.getUserUsername(), bp.getUserPassword());
        }
        return this.veza;
    }

    /** 
     * Dohvaća već stvorenu vezu.
     */
    public Connection dajVezu() throws ClassNotFoundException, SQLException {
        return this.veza;
    }
}
