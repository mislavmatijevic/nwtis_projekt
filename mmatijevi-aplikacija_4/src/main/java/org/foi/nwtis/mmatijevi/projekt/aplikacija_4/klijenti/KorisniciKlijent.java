package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import jakarta.servlet.ServletContext;

/**
 * Ova klasa služi kao klijent između ove aplikacije i aplikacije 3 u pozadini.
 * Ova klasa brine se za slanje i primanje informacija o korisnicima.
 */
public class KorisniciKlijent extends PristupServisu {
	public KorisniciKlijent(ServletContext context) {
		super("korisnici", context);
	}
}
