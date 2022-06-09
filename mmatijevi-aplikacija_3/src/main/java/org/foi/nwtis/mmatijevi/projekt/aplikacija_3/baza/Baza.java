package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;

/**
 * Ova klasa omogućuje stvaranje i korištenje veze s pravom bazom podataka na siguran način.
 */
public class Baza implements AutoCloseable {

    Connection veza = null;

    private Baza() {
    }

    private static Baza instanca = null;

    public static Baza dajInstancu() {
        if (instanca == null) {
            instanca = new Baza();
        }
        return instanca;
    }

    /** 
     * Po konfiguracijskoj datoteci stvara vezu na bazu.
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

    /**
     * Omogućava sigurno zatvaranje veze na bazu.
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        if (!this.veza.isClosed()) {
            this.veza.close();
        }
    }
}
