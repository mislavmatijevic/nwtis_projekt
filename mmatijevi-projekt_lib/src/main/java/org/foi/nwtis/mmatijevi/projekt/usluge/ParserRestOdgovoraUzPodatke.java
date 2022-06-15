package org.foi.nwtis.mmatijevi.projekt.usluge;

import org.foi.nwtis.mmatijevi.projekt.modeli.RestOdgovorUzPodatke;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParserRestOdgovoraUzPodatke {
    public static <T> T dohvatiPodatkeIzRestOdgovora(String jsonOdgovor) {
        TypeToken<RestOdgovorUzPodatke<T>> odgovorToken = new TypeToken<RestOdgovorUzPodatke<T>>() {
        };
        RestOdgovorUzPodatke<T> odgovorParsirani = parsirajJson(jsonOdgovor, odgovorToken.getType());
        return odgovorParsirani.getPodaci();
    }

    private static <T> T parsirajJson(String jsonOdgovor, java.lang.reflect.Type tipDobivenihPodataka) {
        Gson gson = new Gson();
        return gson.fromJson(jsonOdgovor, tipDobivenihPodataka);
    }
}
