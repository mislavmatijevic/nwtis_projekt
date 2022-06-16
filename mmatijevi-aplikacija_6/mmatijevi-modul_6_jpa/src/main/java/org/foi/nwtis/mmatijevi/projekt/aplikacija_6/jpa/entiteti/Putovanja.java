package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the PUTOVANJA database table.
 * 
 */
@Entity
@Table(name = "PUTOVANJA")
@NamedQuery(name = "Putovanja.findAll", query = "SELECT p FROM Putovanja p")
public class Putovanja implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private int id;

	@Column(nullable = false, length = 10)
	private String aerodromPocetni;

	@Column(nullable = false, length = 10)
	private String aerodromZavrsni;

	@Column(nullable = false)
	private int vrijemePrvogLeta;

	//bi-directional many-to-one association to Korisnici
	@ManyToOne
	@JoinColumn(name = "korisnik", nullable = false)
	private Korisnici korisnici;

	//bi-directional many-to-one association to PutovanjaLetovi
	@OneToMany(mappedBy = "putovanja")
	private List<PutovanjaLetovi> putovanjaLetovis;

	public Putovanja() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAerodromPocetni() {
		return this.aerodromPocetni;
	}

	public void setAerodromPocetni(String aerodromPocetni) {
		this.aerodromPocetni = aerodromPocetni;
	}

	public String getAerodromZavrsni() {
		return this.aerodromZavrsni;
	}

	public void setAerodromZavrsni(String aerodromZavrsni) {
		this.aerodromZavrsni = aerodromZavrsni;
	}

	public int getVrijemePrvogLeta() {
		return this.vrijemePrvogLeta;
	}

	public void setVrijemePrvogLeta(int vrijemePrvogLeta) {
		this.vrijemePrvogLeta = vrijemePrvogLeta;
	}

	public Korisnici getKorisnici() {
		return this.korisnici;
	}

	public void setKorisnici(Korisnici korisnici) {
		this.korisnici = korisnici;
	}

	public List<PutovanjaLetovi> getPutovanjaLetovis() {
		return this.putovanjaLetovis;
	}

	public void setPutovanjaLetovis(List<PutovanjaLetovi> putovanjaLetovis) {
		this.putovanjaLetovis = putovanjaLetovis;
	}

	public PutovanjaLetovi addPutovanjaLetovi(PutovanjaLetovi putovanjaLetovi) {
		getPutovanjaLetovis().add(putovanjaLetovi);
		putovanjaLetovi.setPutovanja(this);

		return putovanjaLetovi;
	}

	public PutovanjaLetovi removePutovanjaLetovi(PutovanjaLetovi putovanjaLetovi) {
		getPutovanjaLetovis().remove(putovanjaLetovi);
		putovanjaLetovi.setPutovanja(null);

		return putovanjaLetovi;
	}

}