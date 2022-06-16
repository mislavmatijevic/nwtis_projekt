package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.jpsql;

import java.util.List;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class KorisniciJpaJpsql {
	@PersistenceContext(unitName = "NWTiS_mmatijevi-PU")
	private EntityManager em;

	public void create(Korisnici korisnici) {
		em.persist(korisnici);
	}

	public void edit(Korisnici korisnici) {
		em.merge(korisnici);
	}

	public void remove(Korisnici korisnici) {
		em.remove(em.merge(korisnici));
	}

	public Korisnici find(Object id) {
		return em.find(Korisnici.class, id);
	}

	public List<?> findAll() {
		return em.createQuery(
			    "SELECT k FROM Korisnici k")
			    .getResultList();
	}

	public List<?> findAll(String prezime, String ime) {
	    return em.createQuery(
	            "SELECT k FROM Korisnici k WHERE k.prezime LIKE :prezime AND k.ime like :ime")
	            .setParameter("prezime", prezime)
	            .setParameter("ime", ime)
	            .getResultList();
	}

	public List<?> findRange(int odBroja, int broj) {
		return em.createQuery(
			    "SELECT k FROM Korisnici k")
				.setMaxResults(broj)
				.setFirstResult(odBroja)
			    .getResultList();		
	}

	public int count() {
		return em.createQuery(
			    "SELECT count(k) FROM Korisnici k")
			    .getFirstResult();	
	}
}
