package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.klijenti.KorisniciKlijent;
import org.foi.nwtis.mmatijevi.projekt.iznimke.ZetonIstekaoException;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
@Named("korisniciZrno")
public class KorisniciZrno implements Serializable {
	@Inject
	ServletContext kontekst;
	@Inject
	HttpServletRequest request;

	@EJB
	KorisniciJpa korisniciJpa;

	List<Korisnik> korisnici = new ArrayList<>();
	List<Korisnik> neSinkroniziraniKorisnici = new ArrayList<>();

	public List<Korisnik> getKorisnici() {
		return this.dajSveKorisnike();
	}

	public void setKorisnici(List<Korisnik> korisnici) {
		this.korisnici = korisnici;
	}

	private List<Korisnik> dajSveKorisnike() {

		FacesMessage porukaPogreske = null;

		try {
			KorisniciKlijent korisniciKlijent = new KorisniciKlijent(kontekst);
			korisnici = korisniciKlijent.dohvatiSveKorisnike((PrijavljeniKorisnik) request.getAttribute("korisnik"));
			prijaviNesinkroniziraneKorisnike();
		} catch (ZetonIstekaoException e) {
			porukaPogreske = new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Sesija je istekla",
					"Prijavite se ponovno u sustav!");
		}

		if (porukaPogreske != null) {
			FacesContext.getCurrentInstance().addMessage(null, porukaPogreske);
		}

		return korisnici;
	}

	public boolean odrediNesinkroniziranost(Korisnik korisnik) {
		return neSinkroniziraniKorisnici.contains(korisnik);
	}

	private void prijaviNesinkroniziraneKorisnike() {
		List<Korisnici> korisniciLokalni = null;
		korisniciLokalni = (List<Korisnici>) korisniciJpa.findAll();

		for (Korisnici korisnikLokalni : korisniciLokalni) {
			Korisnik korisnikGlobalniPronadjeni = null;
			for (Korisnik korisnikGlobalni : korisnici) {
				if (korisnikGlobalni.getKorIme().equals(korisnikLokalni.getKorisnik())) {
					korisnikGlobalniPronadjeni = korisnikGlobalni;
					break;
				}
			}
			if (korisnikGlobalniPronadjeni != null) {
				neSinkroniziraniKorisnici.add(korisnikGlobalniPronadjeni);
			}
		}
	}

}