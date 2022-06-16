package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@SessionScoped
@Named("korisniciZrno")
public class KorisniciZrno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EJB
	KorisniciJpa korisniciJpa;

	List<Korisnici> korisnici = new ArrayList<>();

	public List<Korisnici> getKorisnici() {
		return this.dajSveKorisnike();
	}

	public void setKorisnici(List<Korisnici> korisnici) {
		this.korisnici = korisnici;
	}

	private List<Korisnici> dajSveKorisnike() {
		List<Korisnici> lKorisnicii = (List<Korisnici>) korisniciJpa.findAll();

		return lKorisnicii;
	}

}