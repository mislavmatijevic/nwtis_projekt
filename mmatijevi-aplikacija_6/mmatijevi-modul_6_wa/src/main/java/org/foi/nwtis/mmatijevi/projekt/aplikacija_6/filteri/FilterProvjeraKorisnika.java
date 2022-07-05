package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.filteri;

import java.io.IOException;
import java.util.Date;

import org.foi.nwtis.mmatijevi.projekt.ispis.Terminal;
import org.foi.nwtis.mmatijevi.projekt.modeli.PrijavljeniKorisnik;

import jakarta.faces.application.ResourceHandler;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(servletNames = { "Faces Servlet" })
public class FilterProvjeraKorisnika implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
            chain.doFilter(request, response);
            return;
        }

        Terminal.infoIspis(req.getLocalName());

        PrijavljeniKorisnik prijavljeniKorisnik = null;
        HttpSession sjednica = req.getSession();
        if (sjednica != null) {
            prijavljeniKorisnik = (PrijavljeniKorisnik) sjednica.getAttribute("korisnik");
            if (prijavljeniKorisnik != null) {
                long trenutnoVrijeme = new Date().getTime() / 1000;
                if (prijavljeniKorisnik.getVrijemeTrajanjaZetona() < trenutnoVrijeme) {
                    sjednica.setAttribute("korisnik", null);
                    prijavljeniKorisnik = null;
                } else {
                    req.setAttribute("korisnik", prijavljeniKorisnik);
                }
            }
        }

        if (prijavljeniKorisnik == null &&
                !req.getServletPath().equals("/index.xhtml") &&
                !req.getServletPath().equals("/prijava.xhtml") &&
                !req.getServletPath().equals("/pogreska.xhtml")) {
            res.sendRedirect(req.getContextPath() + "/pogreska.xhtml");
        } else {
            chain.doFilter(req, res);
        }
    }
}
