package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.io.Serializable;

import org.primefaces.PrimeFaces;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Omogućuje prijavu korisnika u sustav.
 * Izrađeno po službenoj PrimeFaces dokumentaciji.
 * @see https://www.primefaces.org/showcase-v8/ui/overlay/dialog/loginDemo.xhtml
 */
@SessionScoped
@Named("prijavaZrno")
public class PrijavaZrno implements Serializable {
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
		boolean prijavljen = false;

		if (korime != null && korime.equals("admin") && lozinka != null && lozinka.equals("admin")) {
			prijavljen = true;
			poruka = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dobrodošli!", korime);
		} else {
			prijavljen = false;
			poruka = new FacesMessage(FacesMessage.SEVERITY_WARN, "Problem", "Neispravni podaci.");
		}

		FacesContext.getCurrentInstance().addMessage(null, poruka);
		PrimeFaces.current().ajax().addCallbackParam("prijavljen", prijavljen);
	}
}