package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * The persistent class for the PUTOVANJA_LETOVI database table.
 * 
 */
@Entity
@Table(name = "PUTOVANJA_LETOVI")
@NamedQuery(name = "PutovanjaLetovi.findAll", query = "SELECT p FROM PutovanjaLetovi p")
public class PutovanjaLetovi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private int id;

	@Column(nullable = false, length = 30)
	private String avion;

	@Column(nullable = false)
	private int vrijemeLeta;

	//bi-directional many-to-one association to Putovanja
	@ManyToOne
	@JoinColumn(name = "putovanje", nullable = false)
	private Putovanja putovanja;

	public PutovanjaLetovi() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAvion() {
		return this.avion;
	}

	public void setAvion(String avion) {
		this.avion = avion;
	}

	public int getVrijemeLeta() {
		return this.vrijemeLeta;
	}

	public void setVrijemeLeta(int vrijemeLeta) {
		this.vrijemeLeta = vrijemeLeta;
	}

	public Putovanja getPutovanja() {
		return this.putovanja;
	}

	public void setPutovanja(Putovanja putovanja) {
		this.putovanja = putovanja;
	}

}