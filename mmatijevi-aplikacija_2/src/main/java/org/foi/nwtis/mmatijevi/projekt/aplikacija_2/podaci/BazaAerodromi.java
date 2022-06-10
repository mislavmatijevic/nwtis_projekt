package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

import jakarta.inject.Singleton;

/**
 * Klasa koja se brine pohranom podataka o aerodromima u bazu.
 */
@Singleton
public class BazaAerodromi {

    public KonfiguracijaBP konfig = null;
    private Connection veza = null;

    /** 
     * Po konfiguracijskoj datoteci stvara vezu na bazu.
     * @return Ostvarena ili već postojeća (nezatvorena) veza na bazu.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection dajVezu() throws ClassNotFoundException, SQLException {
        if (this.veza == null || this.veza.isClosed()) {
            Class.forName(konfig.getDriverDatabase(konfig.getServerDatabase()));
            this.veza = DriverManager.getConnection(
                    konfig.getServerDatabase() + konfig.getUserDatabase(),
                    konfig.getUserUsername(), konfig.getUserPassword());
        }
        return this.veza;
    }

    /** 
     * Dohvaća sve aerodrome.
     * @param konfig Konfiguracijska datoteka baze.
     * @return List<Aerodrom> Svi aerodromi.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<Aerodrom> dohvatiAerodrome(Connection veza)
            throws ClassNotFoundException, SQLException {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();

        try (Statement izrazSvi = veza.createStatement();
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
     * @param konfig Konfiguracijska datoteka baze.
     * @param icao
     * @return Aerodrom
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Aerodrom dohvatiAerodrom(Connection veza, String icao)
            throws ClassNotFoundException, SQLException {
        Aerodrom aerodrom = null;

        try (PreparedStatement izraz = veza.prepareStatement("SELECT * FROM airports WHERE ident = ?")) {
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
     * @param konfig Konfiguracijska datoteka baze.
     * @return List<Aerodrom>
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<Aerodrom> dohvatiPraceneAerodrome(Connection veza)
            throws ClassNotFoundException, SQLException {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();

        try (Statement izrazPraceni = veza.createStatement();
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
     * Unosi podatke o letovima (dolascima/polascima) u bazu.
     * <p>Korisnik bira u koju od dvije ponuđene tablice treba unijeti podatke:
     * <ul>
        <li>AERODROMI_POLASCI</li>
        <li>AERODROMI_DOLASCI</li>
     * </ul>
     * <p>Metoda u tu odabranu tablicu unosi sve podatke, a atribut 'stored' generira se iz trenutnog vremena.
     * @param konfig Konfiguracijska datoteka baze.
     * @param polazak
     * @param tablicaUnos
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void unesiPodatkeAerodroma(Connection veza, AvionLeti polazak, VrstaTablice tablicaUnos)
            throws ClassNotFoundException, SQLException {

        String nazivTablice = tablicaUnos.name();

        try (PreparedStatement izraz = veza.prepareStatement(
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
     * @param konfig Konfiguracijska datoteka baze.
     * @param aerodrom Aerodrom kojemu je dovoljno da je unesen samo 'icao'.
     * @return Ako ICAO oznaka ne pripada niti jednome aerodromu u bazi, vraća se <pre>false</pre>
     */
    public boolean unesiAerodromZaPratiti(Connection veza, String icao)
            throws ClassNotFoundException, SQLException, AerodromVecPracenException {
        try (PreparedStatement izrazUnosPraceni = veza.prepareStatement(
                "INSERT INTO AERODROMI_PRACENI (ident, `stored`) VALUES (?, ?)");) {

            if (dohvatiAerodrom(veza, icao) == null) {
                return false;
            }

            List<Aerodrom> vecPraceniAerodromi = dohvatiPraceneAerodrome(veza);
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
     * @param konfig Konfiguracijska datoteka baze.
     * @param icao ICAO oznaka aerodroma.
     * @param datum Datum za koji treba dohvatiti podatke.
     * @param nazivTablice Odabir tablice polazaka/dolazaka.
     * @return Aerodrom Objekt aerodroma.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<InformacijeOLetu> dohvatiPraceneLetoveZaAerodrom(
            Connection veza, String icao, Date datum, VrstaTablice nazivTablice)
            throws ClassNotFoundException, SQLException {

        List<InformacijeOLetu> aktivnostiLetova = new ArrayList<InformacijeOLetu>();

        long datumPocetniUNIX = datum.getTime() / 1000;
        long datumZavrsniUNIX = datumPocetniUNIX + (60 * 60 * 24);

        String stupac = nazivTablice == VrstaTablice.AERODROMI_DOLASCI
                ? "estArrivalAirport"
                : "estDepartureAirport";

        String kriterij = nazivTablice == VrstaTablice.AERODROMI_DOLASCI
                ? "lastSeen > ? AND lastSeen < ?"
                : "firstSeen > ? AND firstSeen < ?";

        try (PreparedStatement izrazUnosPraceni = veza.prepareStatement(
                "SELECT * FROM " + nazivTablice.name() + " WHERE "
                        + stupac + " = ? AND " + kriterij);) {

            izrazUnosPraceni.setString(1, icao);
            izrazUnosPraceni.setInt(2, (int) (datumPocetniUNIX));
            izrazUnosPraceni.setInt(3, (int) (datumZavrsniUNIX));

            try (ResultSet rs = izrazUnosPraceni.executeQuery()) {
                while (rs.next()) {
                    InformacijeOLetu info = new InformacijeOLetu(
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
     * Unosi objekt problema u bazu. Atribut <pre>stored</pre> nije nužan,
     * već se ta inforamcija o vremenu unosa automatski unosi.
     * @param konfig Konfiguracijska datoteka baze.
     * @param problemDTO Objekt s podacima za unos.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void unesiProblem(Connection veza, ProblemDTO problemDTO)
            throws ClassNotFoundException, SQLException {
        try (PreparedStatement izrazProblem = veza.prepareStatement(
                "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) VALUES (?, ?, ?)")) {
            izrazProblem.setString(1, problemDTO.getIdent());
            izrazProblem.setString(2, problemDTO.getDescription());
            izrazProblem.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            izrazProblem.execute();
        }
    }

    /** 
     * Vraća sve probleme zapisane u bazi.
     * @param konfig Konfiguracijska datoteka baze.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<ProblemDTO> dohvatiProbleme(Connection veza) throws ClassNotFoundException, SQLException {
        List<ProblemDTO> problemi = new ArrayList<ProblemDTO>();

        try (Statement izrazSvi = veza.createStatement();
                ResultSet rsSvi = izrazSvi.executeQuery("SELECT * FROM AERODROMI_PROBLEMI")) {
            while (rsSvi.next()) {
                ProblemDTO problem = stvoriProblemObjekt(rsSvi);
                problemi.add(problem);
            }
        }

        return problemi;
    }

    /** 
     * Vraća sve probleme iz baze za određeni aerodrom.
     * @param konfig Konfiguracijska datoteka baze.
     * @param icao ICAO oznaka aerodroma za koji se dohvaćaju problemi.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<ProblemDTO> dohvatiProblemeZaAerodrom(Connection veza, String icao)
            throws ClassNotFoundException, SQLException {
        List<ProblemDTO> problemi = new ArrayList<ProblemDTO>();

        try (PreparedStatement izraz = veza.prepareStatement("SELECT * FROM AERODROMI_PROBLEMI WHERE ident = ?");) {

            izraz.setString(1, icao);

            try (ResultSet rsSvi = izraz.executeQuery()) {
                while (rsSvi.next()) {
                    ProblemDTO problem = stvoriProblemObjekt(rsSvi);
                    problemi.add(problem);
                }
            }
        }

        return problemi;
    }

    /** 
     * Briše probleme za točno određeni aerodrom.
     * @param konfig Konfiguracijska datoteka baze.
     * @param icao ICAO oznaka aerodroma za koji obrisati podatke.
     * @return Uspjeh naredbe.
     */
    public void izbrisiProblemeZaAerodrom(Connection veza, String icao)
            throws ClassNotFoundException, SQLException {
        try (PreparedStatement izrazUnosPraceni = veza.prepareStatement(
                "DELETE FROM AERODROMI_PROBLEMI WHERE ident = ?")) {
            izrazUnosPraceni.setString(1, icao);
            izrazUnosPraceni.execute();
        }
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

    private static ProblemDTO stvoriProblemObjekt(ResultSet rs) throws SQLException {
        ProblemDTO problem = new ProblemDTO();
        problem.setIdent(rs.getString("ident"));
        problem.setDescription(rs.getString("description"));
        problem.setStored(rs.getTimestamp("stored").getTime());
        return problem;
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
