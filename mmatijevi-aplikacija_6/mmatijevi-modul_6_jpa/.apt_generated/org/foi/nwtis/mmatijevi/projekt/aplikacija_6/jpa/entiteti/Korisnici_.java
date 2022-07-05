package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.processing.Generated;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Grupe;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Putovanja;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-06-17T20:01:40", comments="EclipseLink-3.0.2.v20210716-re8d4b571c9")
@StaticMetamodel(Korisnici.class)
public class Korisnici_ { 

    public static volatile SingularAttribute<Korisnici, String> ime;
    public static volatile SingularAttribute<Korisnici, String> prezime;
    public static volatile SingularAttribute<Korisnici, String> lozinka;
    public static volatile ListAttribute<Korisnici, Putovanja> putovanjas;
    public static volatile ListAttribute<Korisnici, Grupe> grupes;
    public static volatile SingularAttribute<Korisnici, String> email;
    public static volatile SingularAttribute<Korisnici, String> korisnik;

}