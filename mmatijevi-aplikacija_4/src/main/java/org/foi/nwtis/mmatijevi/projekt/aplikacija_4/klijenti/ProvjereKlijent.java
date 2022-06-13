package org.foi.nwtis.mmatijevi.projekt.aplikacija_4.klijenti;

import jakarta.servlet.ServletContext;

/**
 * Ova klasa služi kao klijent između ove aplikacije i aplikacije 3 u pozadini.
 * Ova klasa brine se za slanje i primanje informacija o provjerama i žetonima.
 */
public class ProvjereKlijent extends PristupServisu {
	public ProvjereKlijent(ServletContext context) {
		super("provjere", context);
	}
}
