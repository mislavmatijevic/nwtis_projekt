package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.klijenti.ProvjereKlijent;
import org.foi.nwtis.mmatijevi.projekt.iznimke.KorisnikNePostojiException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.mmatijevi.projekt.modeli.Zeton;
import org.primefaces.PrimeFaces;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

/**
 * Omogućuje prijavu korisnika u sustav.
 * Izrađeno po službenoj PrimeFaces dokumentaciji.
 * @see https://www.primefaces.org/showcase-v8/ui/overlay/dialog/loginDemo.xhtml
 */
@SessionScoped
@Named("prijavaZrno")
public class PrijavaZrno implements Serializable {
	@Inject
	ServletContext kontekst;

	@EJB
	KorisniciJpa korisniciJpa;

	private String korime;

	private String lozinka;

	public String getKorime() {
		return korime;
	}

	public void setKorime(String korime) {
		this.korime = korime;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public void prijaviSe() {
		FacesMessage poruka = null;

		boolean pronadjenLokalno = pronadjiKorisnikaLokalno();

		if (pronadjenLokalno) {
			ProvjereKlijent provjereKlijent = new ProvjereKlijent(kontekst);
			try {
				Zeton zeton = provjereKlijent.prijaviKorisnika(korime, lozinka);
				PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik(korime, lozinka, zeton.getZeton());
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				Map<String, Object> sesija = externalContext.getSessionMap();

				String porukaDobrodoslice;
				if (sesija.get("korisnik") == null) {
					porukaDobrodoslice = "Dobrodošli!";
				} else {
					porukaDobrodoslice = "Vaša sesija je produžena!";
				}
				sesija.put("korisnik", prijavljeniKorisnik);
				poruka = new FacesMessage(FacesMessage.SEVERITY_INFO, porukaDobrodoslice, korime);
			} catch (KorisnikNePostojiException ex) {
				poruka = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problem", "Prijava neuspješna");
			}
		} else {
			poruka = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problem", "Takav korisnik nije pronađen!");
		}

		FacesContext.getCurrentInstance().addMessage(null, poruka);
	}

	private boolean pronadjiKorisnikaLokalno() {
		boolean pronadjenLokalno = false;
		List<Korisnici> korisnici = korisniciJpa.findAll();
		for (Korisnici korisnik : korisnici) {
			if (korisnik.getKorisnik().equals(korime) && korisnik.getLozinka().equals(lozinka)) {
				pronadjenLokalno = true;
				break;
			}
		}
		return pronadjenLokalno;
	}
}