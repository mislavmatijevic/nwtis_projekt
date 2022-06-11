package org.foi.nwtis.mmatijevi.projekt.aplikacija_3.servisi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.baza.Baza;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.iznimke.AerodromVecPracenException;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_3.modeli.InformacijeLeta;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServisAerodroma {

    @Inject
    Baza baza;

    /** 
     * Dohvaća sve aerodrome.
     * @return List<Aerodrom> Svi aerodromi.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<Aerodrom> dohvatiAerodrome()
            throws ClassNotFoundException, SQLException {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();

        try (Connection veza = baza.dohvatiVezu();
                Statement izrazSvi = veza.createStatement();
                ResultSet rsSvi = izrazSvi.executeQuery("SELECT * FROM airports")) {
            while (rsSvi.next()) {
                Airport airport = stvoriAirportObjekt(rsSvi);
                aerodromi.add(mapirajAirportZaAerodrom(airport));
            }
        }

        return aerodromi;
    }

    /** 
     * Dohvaća samo jedan aerodrom po icao oznaci.
     * @param icao
     * @return Aerodrom
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Aerodrom dohvatiAerodrom(String icao)
            throws ClassNotFoundException, SQLException {
        Aerodrom aerodrom = null;

        try (Connection veza = baza.dohvatiVezu()) {
            aerodrom = dohvatiAerodromNaPostojecojVezi(icao, veza);
        }

        return aerodrom;
    }

    /** 
     * Vraća sve praćene aerodrome.
     * @return List<Aerodrom>
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<Aerodrom> dohvatiPraceneAerodrome()
            throws ClassNotFoundException, SQLException {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();

        try (Connection veza = baza.dohvatiVezu()) {
            aerodromi = dohvatiPraceneAerodromeNaPostojecojVezi(veza);
        }

        return aerodromi;
    }

    /** 
     * Unosi podatke o letovima (dolascima/polascima) u bazu.
     * <p>Korisnik bira u koju od dvije ponuđene tablice treba unijeti podatke:
     * <ul>
        <li>AERODROMI_POLASCI</li>
        <li>AERODROMI_DOLASCI</li>
     * </ul>
     * <p>Metoda u tu odabranu tablicu unosi sve podatke, a atribut 'stored' generira se iz trenutnog vremena.
     * @param polazak
     * @param tablicaUnos
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void unesiPodatkeAerodroma(AvionLeti polazak, VrstaTablice tablicaUnos)
            throws ClassNotFoundException, SQLException {

        String nazivTablice = tablicaUnos.name();

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izraz = veza.prepareStatement(
                        "INSERT INTO " + nazivTablice + " (icao24, firstSeen, estDepartureAirport, lastSeen, " +
                                "estArrivalAirport, callsign, estDepartureAirportHorizDistance, " +
                                "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, " +
                                "estArrivalAirportVertDistance, departureAirportCandidatesCount, " +
                                "arrivalAirportCandidatesCount, `stored`) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {

            izraz.setString(1, polazak.getIcao24());
            izraz.setInt(2, polazak.getFirstSeen());
            izraz.setString(3, polazak.getEstDepartureAirport());
            izraz.setInt(4, polazak.getLastSeen());
            izraz.setString(5, polazak.getEstArrivalAirport());
            izraz.setString(6, polazak.getCallsign());
            izraz.setInt(7, polazak.getEstDepartureAirportHorizDistance());
            izraz.setInt(8, polazak.getEstDepartureAirportVertDistance());
            izraz.setInt(9, polazak.getEstArrivalAirportHorizDistance());
            izraz.setInt(10, polazak.getEstArrivalAirportVertDistance());
            izraz.setInt(11, polazak.getDepartureAirportCandidatesCount());
            izraz.setInt(12, polazak.getArrivalAirportCandidatesCount());
            izraz.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            izraz.execute();
        }
    }

    /** 
     * Unosi aerodrom po ICAO oznaci u bazu kao aerodrom za praćenje.
     * @param aerodrom Aerodrom kojemu je dovoljno da je unesen samo 'icao'.
     * @return Ako ICAO oznaka ne pripada niti jednome aerodromu u bazi, vraća se <pre>false</pre>
     */
    public boolean unesiAerodromZaPratiti(String icao)
            throws ClassNotFoundException, SQLException, AerodromVecPracenException {
        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izrazUnosPraceni = veza.prepareStatement(
                        "INSERT INTO AERODROMI_PRACENI (ident, `stored`) VALUES (?, ?)");) {

            if (dohvatiAerodromNaPostojecojVezi(icao, veza) == null) {
                return false;
            }

            List<Aerodrom> vecPraceniAerodromi = dohvatiPraceneAerodromeNaPostojecojVezi(veza);
            for (Aerodrom aerodrom : vecPraceniAerodromi) {
                if (aerodrom.getIcao().equals(icao)) {
                    throw new AerodromVecPracenException(
                            "Aerodrom s oznakom '" + aerodrom.getIcao() + "' već jest u listi praćenja!");
                }
            }

            izrazUnosPraceni.setString(1, icao);
            izrazUnosPraceni.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            izrazUnosPraceni.execute();
        }

        return true;
    }

    /** 
     * Dohvaća polaska <strong>ILI</strong> odlaske jednog aerodroma.
     * @param icao ICAO oznaka aerodroma.
     * @param datum Datum za koji treba dohvatiti podatke.
     * @param datumOd Prvi relevantni datum.
     * @param datumDo Zadnji relevantni datum.
     * @param nazivTablice Odabir tablice polazaka/dolazaka.
     * @return Aerodrom Objekt aerodroma.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<InformacijeLeta> dohvatiPraceneLetoveZaAerodrom(
            String icao, Date datumOd, Date datumDo, VrstaTablice nazivTablice)
            throws ClassNotFoundException, SQLException {

        List<InformacijeLeta> aktivnostiLetova = new ArrayList<InformacijeLeta>();

        long datumPocetniUNIX = datumOd.getTime() / 1000;
        long datumZavrsniUNIX = datumDo.getTime() / 1000;

        if (datumPocetniUNIX == datumZavrsniUNIX) {
            datumZavrsniUNIX = datumPocetniUNIX + (60 * 60 * 24);
        }

        String stupac = nazivTablice == VrstaTablice.AERODROMI_DOLASCI
                ? "estArrivalAirport"
                : "estDepartureAirport";

        String kriterij = nazivTablice == VrstaTablice.AERODROMI_DOLASCI
                ? "lastSeen > ? AND lastSeen < ?"
                : "firstSeen > ? AND firstSeen < ?";

        try (Connection veza = baza.dohvatiVezu();
                PreparedStatement izrazUnosPraceni = veza.prepareStatement(
                        "SELECT * FROM " + nazivTablice.name() + " WHERE "
                                + stupac + " = ? AND " + kriterij);) {

            izrazUnosPraceni.setString(1, icao);
            izrazUnosPraceni.setInt(2, (int) (datumPocetniUNIX));
            izrazUnosPraceni.setInt(3, (int) (datumZavrsniUNIX));

            try (ResultSet rs = izrazUnosPraceni.executeQuery()) {
                while (rs.next()) {
                    InformacijeLeta info = new InformacijeLeta(
                            rs.getString("estDepartureAirport").trim(),
                            rs.getString("estArrivalAirport").trim(),
                            new Date(1000 * Long.parseLong(rs.getString("firstSeen"))),
                            new Date(1000 * Long.parseLong(rs.getString("lastSeen"))),
                            rs.getString("callsign").trim());
                    aktivnostiLetova.add(info);
                }
            }
        }

        return aktivnostiLetova;
    }

    /** 
     * Privatna metoda koja se bavi dohvaćanjem samo jednog aerodroma po icao oznaci.
     * Smisao ove metode u kontekstu javne metode za dohvat aerodroma jest što ova koristi već postojeću vezu.
     * @param icao
     * @return Aerodrom
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Aerodrom dohvatiAerodromNaPostojecojVezi(String icao, Connection postojecaVeza)
            throws ClassNotFoundException, SQLException {
        Aerodrom aerodrom = null;

        try (PreparedStatement izraz = postojecaVeza.prepareStatement("SELECT * FROM airports WHERE ident = ?")) {
            izraz.setString(1, icao);

            try (ResultSet rezultat = izraz.executeQuery()) {
                if (rezultat.next()) {
                    Airport airport = stvoriAirportObjekt(rezultat);
                    aerodrom = mapirajAirportZaAerodrom(airport);
                }
            }

        }

        return aerodrom;
    }

    /** 
     * Privatna metoda koja se bavi dohvaćanjem svih aerodroma.
     * Smisao ove metode u kontekstu javne metode za dohvat svih aerodroma jest što ova koristi već postojeću vezu.
     * @param icao
     * @return Aerodrom
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private List<Aerodrom> dohvatiPraceneAerodromeNaPostojecojVezi(Connection postojecaVeza)
            throws ClassNotFoundException, SQLException {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();

        try (Statement izrazPraceni = postojecaVeza.createStatement();
                ResultSet rsPraceni = izrazPraceni.executeQuery(
                        "SELECT svi.*, praceni.ident FROM airports as svi, " +
                                "AERODROMI_PRACENI as praceni WHERE praceni.ident = svi.ident")) {
            while (rsPraceni.next()) {
                Airport airport = stvoriAirportObjekt(rsPraceni);
                aerodromi.add(mapirajAirportZaAerodrom(airport));
            }
        }

        return aerodromi;
    }

    /** 
     * Pretvara 'Airport' objekt u jednostavniji 'Aerodrom' objekt.
     * <p>Preslika dobro dođe jer 'Airport' sadržava cijeli slog iz baze,
     * a 'Aerodrom' je prilagođeniji prikazu tih podataka.
     * @param airport Objekt klase 'Airport'.
     * @return Aerodrom Objekt klase 'Aerodrom' s podacima objekta parametra.
     */
    private static Aerodrom mapirajAirportZaAerodrom(Airport airport) {
        Aerodrom aerodrom;
        Lokacija lokacija = new Lokacija(airport.getCoordinates().split(", ")[0],
                airport.getCoordinates().split(", ")[1]);
        aerodrom = new Aerodrom(airport.getIdent(), airport.getName(), airport.getIso_country(),
                lokacija);
        return aerodrom;
    }

    /** 
     * Stvara Java objekt iz rezultata upita baze. 
     * @param rs Jedan slog iz baze 'airports'.
     * @return Airport Java objekt aerodroma toga sloga.
     * @throws SQLException
     */
    private static Airport stvoriAirportObjekt(ResultSet rs) throws SQLException {
        return new Airport(rs.getString("ident"), rs.getString("type"), rs.getString("name"),
                rs.getString("elevation_ft"), rs.getString("continent"), rs.getString("iso_country"),
                rs.getString("iso_region"), rs.getString("municipality"), rs.getString("gps_code"),
                rs.getString("iata_code"), rs.getString("local_code"), rs.getString("coordinates"));
    }

    /**
     * Enumeracija s vrijednostima dvije u principu iste tablice, s različitim svrhama.
     * <p>Bitno da su nazivi ovih tablica sanitizirani i da točno odgovaraju nazivima odgovarajućih tablica.
     */
    public enum VrstaTablice {
        AERODROMI_POLASCI,
        AERODROMI_DOLASCI
    }
}