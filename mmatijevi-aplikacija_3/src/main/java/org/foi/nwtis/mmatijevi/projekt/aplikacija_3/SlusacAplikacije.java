package org.foi.nwtis.mmatijevi.projekt.aplikacija_3;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.KonfiguracijaBP;
import org.foi.nwtis.mmatijevi.projekt.konfiguracije.bazePodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Slupač - implementacijska klasa SlusacAplikacije
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private KonfiguracijaBP konfig = null;

    public SlusacAplikacije() {
    }

    /**
     * Stvaranje servera.
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {

        Terminal.uspjehIspis("Aplikacija 3 se pali!");

        ServletContext context = sce.getServletContext();

        String nazivDatoteke = context.getInitParameter("konfiguracija");
        String putanja = context.getRealPath("/WEB-INF") + File.separator;
        nazivDatoteke = putanja + nazivDatoteke;

        konfig = new PostavkeBazaPodataka(nazivDatoteke);

        try {
            konfig.ucitajKonfiguraciju();
            context.setAttribute("postavke", konfig);
            System.out.println("Učitani parametri konfiguracije!");
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE,
                    "Aplikacija 3 nije uspjela pročitati konfiguraciju!", ex);
            ;
            return;
        }

        ServletContextListener.super.contextInitialized(sce);

        Terminal.uspjehIspis("Aplikacija 3 podignuta!");
    }

    /**
     * Gašenje servera.
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        Terminal.pozorIspis("Aplikacija 3 se gasi!");
    }

}
