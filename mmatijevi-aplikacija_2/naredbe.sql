# Broj upisanih dolazaka i polazaka
SELECT (SELECT COUNT(*) FROM nwtis_bp_1.AERODROMI_POLASCI) AS 'Broj polazaka', (SELECT COUNT(*) FROM nwtis_bp_1.AERODROMI_DOLASCI) AS 'Broj dolazaka'

# Broj upisa po danima
SELECT COUNT(*) as "Broj dolazaka na taj dan", DATE(from_unixtime(lastSeen)) as Datum FROM AERODROMI_DOLASCI GROUP BY Datum
SELECT COUNT(*) as "Broj polazaka na taj dan", DATE(from_unixtime(lastSeen)) as Datum FROM AERODROMI_POLASCI GROUP BY Datum


# Broj upisa dolazaka za sve aerodrome pojedinačno
SELECT COUNT(*) as "Broj dolazaka na taj dan", DATE(from_unixtime(lastSeen)) as Datum, estArrivalAirport FROM AERODROMI_DOLASCI GROUP BY Datum, estArrivalAirport
# Broj upisa polazaka za sve aerodrome pojedinačno
SELECT COUNT(*) as "Broj polazaka na taj dan", DATE(from_unixtime(lastSeen)) as Datum, estDepartureAirport FROM AERODROMI_POLASCI GROUP BY Datum, estDepartureAirport

# Broj preuzetih podataka po danima za odabrani aerodrom
SET @icao = "LDZA";
SELECT COUNT(*) as "Broj polazaka na taj dan", DATE(from_unixtime(firstSeen)) as Datum, estDepartureAirport FROM AERODROMI_POLASCI WHERE estDepartureAirport = @icao GROUP BY Datum, estDepartureAirport;
SELECT COUNT(*) as "Broj dolazaka na taj dan", DATE(from_unixtime(lastSeen)) as Datum, estArrivalAirport FROM AERODROMI_DOLASCI WHERE estArrivalAirport = @icao GROUP BY Datum, estArrivalAirport


