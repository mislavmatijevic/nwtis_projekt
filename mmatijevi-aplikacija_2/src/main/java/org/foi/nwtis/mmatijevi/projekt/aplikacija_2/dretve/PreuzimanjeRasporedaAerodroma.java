package org.foi.nwtis.mmatijevi.projekt.aplikacija_2.dretve;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci.BazaAerodromi;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci.BazaAerodromi.VrstaTablice;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_2.podaci.ProblemDTO;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.servlet.ServletContext;

/**
 * Ova klasa predstavlja dretvu koja u ciklusima ažurira podatke u bazi o polascima i dolascima.
 */
public class PreuzimanjeRasporedaAerodroma extends Thread {

	private final int msJedanSat = 60 * 60 * 1000;

	private long preuzimanjeOdmakMs;
	private int vrijemePauza;
	private long preuzimanjeOdMs;
	private long preuzimanjeDoMs;
	private long pomakGornjeGraniceMs;
	private long vrijemeCiklusaMs;
	private int ciklusKorekcija;
	private OSKlijent osKlijent;
	private KonfiguracijaBP konfig;

	private long ciklusStvarni;
	private long ciklusVirtualni;

	private boolean dozvolaPisanjaUBazu = true;

	/**
	 * U konstruktor se proslijeđuje objekt konteksta
	 * @param context Servletov kontekst s konfiguracijom zapisanom u atribut "postavke"
	 */
	public PreuzimanjeRasporedaAerodroma(ServletContext context) {
		this.konfig = (KonfiguracijaBP) context.getAttribute("postavke");
	}

	/**
	 * Čita konfiguraciju i započinje rad dretve.
	 */
	@Override
	public synchronized void start() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		try {
			this.osKlijent = new OSKlijent(this.konfig.dajPostavku("OpenSkyNetwork.korisnik"),
					this.konfig.dajPostavku("OpenSkyNetwork.lozinka"));
			this.vrijemeCiklusaMs = 1000 * Long.parseLong(this.konfig.dajPostavku("ciklus.vrijeme"));
			this.ciklusKorekcija = Integer.parseInt(this.konfig.dajPostavku("ciklus.korekcija"));
			int odmakDani = Integer.parseInt(this.konfig.dajPostavku("preuzimanje.odmak"));
			this.preuzimanjeOdmakMs = 24 * msJedanSat * odmakDani;
			this.vrijemePauza = Integer.parseInt(this.konfig.dajPostavku("preuzimanje.vrijeme"));
			this.preuzimanjeOdMs = sdf.parse(this.konfig.dajPostavku("preuzimanje.od")).getTime();
			this.preuzimanjeDoMs = sdf.parse(this.konfig.dajPostavku("preuzimanje.do")).getTime();
			this.pomakGornjeGraniceMs = msJedanSat * Integer.parseInt(this.konfig.dajPostavku("preuzimanje.vrijeme"));

			long trenutnoVrijeme = (new Date()).getTime();

			if (preuzimanjeOdMs > trenutnoVrijeme - preuzimanjeOdmakMs) {
				throw new Exception(
						"Vrijeme 'preuzimanjeOd' nije u skladu s minimalnim odmakom od " + odmakDani + " dana ("
								+ sdf.format(new Date(preuzimanjeOdMs)) + " > "
								+ sdf.format(new Date((trenutnoVrijeme - preuzimanjeOdmakMs))) + ")");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(
					"Neuspjelo pokretanje dretve za preuzimanje rasporeda aerodroma! " + ex.getLocalizedMessage());
			return;
		}

		super.start();
	}

	/**
	 * Osnova dretve, radi u ciklusima.
	 * <p>U svakome ciklusu preuzme se segment podataka sa servisa o letovima.
	 * <p>Svaki ciklus napuni bazu za određeni broj dolazaka i polazaka.
	 */
	@Override
	public void run() {
		long preuzimanjeTrenutniPocetak = this.preuzimanjeOdMs;
		long preuzimanjeTrenutnaGranica = this.preuzimanjeOdMs + this.pomakGornjeGraniceMs;

		if (preuzimanjeTrenutnaGranica > this.preuzimanjeDoMs) {
			preuzimanjeTrenutnaGranica = this.preuzimanjeDoMs;
		}

		this.ciklusStvarni = 0;
		this.ciklusVirtualni = 0;

		while (preuzimanjeTrenutniPocetak < this.preuzimanjeDoMs && dozvolaPisanjaUBazu) {
			boolean spavala = false;
			long vrijemePocetka = (new Date()).getTime();

			if (preuzimanjeTrenutniPocetak > vrijemePocetka - this.preuzimanjeOdmakMs) {
				System.out.println("Preuzimanje je dovršeno za danas! Spavam 1 dan.");
				try {
					sleep(24 * msJedanSat);
					spavala = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("Prekinuto čekanje sutrašnjih novih podataka. Prekidam rad.");
					return;
				}
			}

			List<Aerodrom> aerodromiPraceni;
			try (BazaAerodromi baza = BazaAerodromi.dajInstancu();
					Connection veza = baza.stvoriVezu(this.konfig)) {

				aerodromiPraceni = baza.dohvatiPraceneAerodrome(veza);

				for (Aerodrom praceniAerodrom : aerodromiPraceni) {
					if (dozvolaPisanjaUBazu) {
						dohvatiIPohraniPolaskeUBazu(baza, preuzimanjeTrenutniPocetak, preuzimanjeTrenutnaGranica,
								praceniAerodrom);
						dohvatiIPohraniDolaskeUBazu(baza, preuzimanjeTrenutniPocetak, preuzimanjeTrenutnaGranica,
								praceniAerodrom);
						pauzirajPreuzimanje();
					} else {
						return;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println("Neuspješan dohvat praćenih aerodroma iz baze!");
				return;
			}

			preuzimanjeTrenutniPocetak = preuzimanjeTrenutnaGranica;
			preuzimanjeTrenutnaGranica += this.pomakGornjeGraniceMs;
			odspavaj(vrijemePocetka, spavala);
		}
	}

	/** 
	 * Metoda dohvaća polaska aviona.
	 * @param preuzimanjeTrenutniPocetak Trenutna milisekunda od koje treba preuzimati podatke.
	 * @param preuzimanjeTrenutnaGranica Milisekunda koja predstavlja granicu ažurnosti podataka.
	 * @param praceniAerodrom Aerodrom za koji se dohvaćaju podaci.
	 */
	private void dohvatiIPohraniPolaskeUBazu(BazaAerodromi baza, long preuzimanjeTrenutniPocetak,
			long preuzimanjeTrenutnaGranica,
			Aerodrom praceniAerodrom) {
		try {
			List<AvionLeti> avioniPolasci = osKlijent.getDepartures(praceniAerodrom.getIcao(),
					preuzimanjeTrenutniPocetak / 1000, preuzimanjeTrenutnaGranica / 1000);
			if (avioniPolasci != null) {
				for (AvionLeti polazak : avioniPolasci) {
					if (polazak.getEstArrivalAirport() != null) {
						try {
							baza.unesiPodatkeAerodroma(baza.dajVezu(), polazak, VrstaTablice.AERODROMI_POLASCI);
						} catch (Exception ex) {
							throw new Exception("Neuspjelo zapisivanje polaska aviona " + polazak.getCallsign() + ". ");
						}
					}
				}
			}
		} catch (Exception ex) {
			ProblemDTO problemDTO = new ProblemDTO();
			problemDTO.setIdent(praceniAerodrom.getIcao());
			problemDTO.setDescription(ex.getMessage());
			unosProblemaUBazu(problemDTO);
		}
	}

	/** 
	 * Metoda dohvaća dolaske aviona.
	 * @param preuzimanjeTrenutniPocetak Trenutna milisekunda od koje treba preuzimati podatke.
	 * @param preuzimanjeTrenutnaGranica Milisekunda koja predstavlja granicu ažurnosti podataka.
	 * @param praceniAerodrom Aerodrom za koji se dohvaćaju podaci.
	 */
	private void dohvatiIPohraniDolaskeUBazu(BazaAerodromi baza, long preuzimanjeTrenutniPocetak,
			long preuzimanjeTrenutnaGranica,
			Aerodrom praceniAerodrom) {
		try {
			List<AvionLeti> avioniDolasci = osKlijent.getArrivals(praceniAerodrom.getIcao(),
					preuzimanjeTrenutniPocetak / 1000, preuzimanjeTrenutnaGranica / 1000);
			if (avioniDolasci != null) {
				for (AvionLeti dolazak : avioniDolasci) {
					if (dolazak.getEstDepartureAirport() != null) {
						try {
							baza.unesiPodatkeAerodroma(baza.dajVezu(), dolazak, VrstaTablice.AERODROMI_DOLASCI);
						} catch (Exception ex) {
							throw new Exception("Neuspjelo zapisivanje dolaska aviona " + dolazak.getCallsign() + ". ");
						}
					}
				}
			}
		} catch (Exception ex) {
			ProblemDTO problemDTO = new ProblemDTO();
			problemDTO.setIdent(praceniAerodrom.getIcao());
			problemDTO.setDescription(ex.getMessage());
			unosProblemaUBazu(problemDTO);
		}
	}

	/** 
	 * U bazu unosi opis problema pri dohvaćanju polazaka/dolazaka.
	 * @param problemDTO Opisni objekt/preslika baze napunjen sa svim podacima osim <pre>stored</pre>
	 */
	private void unosProblemaUBazu(ProblemDTO problemDTO) {
		try (BazaAerodromi baza = BazaAerodromi.dajInstancu();
				Connection veza = baza.stvoriVezu(this.konfig)) {
			baza.unesiProblem(veza, problemDTO);
		} catch (Exception exInner) {
			exInner.printStackTrace();
			System.err.println("Neuspjeh pri unosu problema u bazu.\n" +
					"\t- ICAO leta: " + problemDTO.getIdent() + "\n" +
					"\t- Opis problema: " + problemDTO.getDescription());
		}
	}

	/**
	 * Pauzira preuzimanje podataka o aerodromima između seta svaka dva aerodroma.
	 * <p>Zahtijeva globalnu varijablu <pre>vrijemePauza
	 */
	private void pauzirajPreuzimanje() {
		try {
			sleep(this.vrijemePauza);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Dretva nije uspješno pauzirala rad između preuzimanja!");
		}
	}

	/** 
	 * Određuje koliko dretva mora spavati do idućeg ciklusa rada i aktivira spavanje.
	 * @param vrijemePocetka Vrijeme početka proteklog ciklusa.
	 * @param spavala Informacija je li dretva spavala neposredno prije trenutnog ciklusa.
	 */
	private void odspavaj(long vrijemePocetka, boolean spavala) {
		long vrijemeObrade = (new Date()).getTime() - vrijemePocetka;
		long vrijemeSpavanja;

		if (spavala) {
			this.ciklusVirtualni += (24 * msJedanSat) / this.vrijemeCiklusaMs;
			ciklusStvarni++;
			vrijemeSpavanja = this.vrijemeCiklusaMs - vrijemeObrade;
		} else {
			if (vrijemeObrade > this.vrijemeCiklusaMs) {
				long prviUmnozak = vrijemeObrade / this.vrijemeCiklusaMs + 1;
				vrijemeSpavanja = this.vrijemeCiklusaMs * prviUmnozak - vrijemeObrade;
				this.ciklusVirtualni += prviUmnozak;
			} else {
				vrijemeSpavanja = this.vrijemeCiklusaMs - vrijemeObrade;
				this.ciklusVirtualni++;
			}

			ciklusStvarni++;

			if (ciklusStvarni % this.ciklusKorekcija == 0) {
				vrijemeSpavanja -= 1;
			}
		}

		try {
			sleep(vrijemeSpavanja);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Dretva nije odspavala!");
		}

		System.out.println("Spavanje završeno! Stanje ciklusa:\n" +
				"\t > ciklusVirtualni: " + ciklusVirtualni + "\n" +
				"\t > ciklusStvarni: " + ciklusStvarni + "\n");
	}

	@Override
	public void interrupt() {
		dozvolaPisanjaUBazu = false;
		super.interrupt();
	}
}
