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
    public KonfiguracijaBP konfig = null;

    /** 
     * Po konfiguracijskoj datoteci stvara vezu na bazu.
     * Očekuje se da korisnik klase zatvori vezu nakon završetka.
     * @return Connection Ostvarena veza na bazu.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection stvoriVezu() throws ClassNotFoundException, SQLException {
        if (this.veza == null || this.veza.isClosed()) {
            Class.forName(konfig.getDriverDatabase(konfig.getServerDatabase()));
            this.veza = DriverManager.getConnection(
                    konfig.getServerDatabase() + konfig.getUserDatabase(),
                    konfig.getUserUsername(), konfig.getUserPassword());
        }
        return this.veza;
    }
}
