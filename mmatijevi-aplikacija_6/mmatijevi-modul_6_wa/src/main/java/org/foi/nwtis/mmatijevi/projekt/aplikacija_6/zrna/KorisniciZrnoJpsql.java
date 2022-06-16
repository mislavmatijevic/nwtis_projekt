package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.jpsql.KorisniciJpaJpsql;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@RequestScoped
@Named("korisniciZrnoJpsql")
public class KorisniciZrnoJpsql {
	@EJB
	KorisniciJpaJpsql korisniciJpa;

	List<Korisnici> korisnici = new ArrayList<>();

	public List<Korisnici> getKorisnici() {
		return korisnici = this.dajSveKorisnike();
	}

	public void setKorisnici(List<Korisnici> korisnici) {
		this.korisnici = korisnici;
	}

	public List<Korisnici> dajSveKorisnike() {

		List<Korisnici> lKorisnicii = (List<Korisnici>) korisniciJpa.findAll();

		return lKorisnicii;
	}
}