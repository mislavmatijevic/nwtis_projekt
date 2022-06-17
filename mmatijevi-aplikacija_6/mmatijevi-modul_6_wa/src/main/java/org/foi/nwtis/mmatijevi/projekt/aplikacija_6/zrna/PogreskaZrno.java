package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.zrna;

import java.io.Serializable;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;

@ViewScoped
@Named("pogreskaZrno")
public class PogreskaZrno implements Serializable {
    private boolean preusmjeren;

    public void setPreusmjeren(boolean preusmjeren) {
        this.preusmjeren = preusmjeren;
    }

    public boolean getPreusmjeren() {
        otkrijiJeLiZahtjevPreusmjeren();
        return preusmjeren;
    }

    private void otkrijiJeLiZahtjevPreusmjeren() {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getResponse();
        preusmjeren = response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode();
    }
}
