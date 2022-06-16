package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.util.Date;

import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna.jms.PosiljateljPoruke;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TimerService;

@Singleton
@Startup
public class TocnoVrijeme {
	@Resource
	TimerService timerService;

	@EJB
	PosiljateljPoruke posiljateljPoruke;

	@Schedule(minute = "*/1", hour = "*", persistent = false)
	public void slanjePoruke() {
		posiljateljPoruke.noviPoruka("Sada je: " + new Date().toString());
	}

}