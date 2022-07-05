package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.processing.Generated;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.PutovanjaLetovi;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-06-17T20:01:40", comments="EclipseLink-3.0.2.v20210716-re8d4b571c9")
@StaticMetamodel(Putovanja.class)
public class Putovanja_ { 

    public static volatile ListAttribute<Putovanja, PutovanjaLetovi> putovanjaLetovis;
    public static volatile SingularAttribute<Putovanja, Korisnici> korisnici;
    public static volatile SingularAttribute<Putovanja, String> aerodromPocetni;
    public static volatile SingularAttribute<Putovanja, Integer> vrijemePrvogLeta;
    public static volatile SingularAttribute<Putovanja, Integer> id;
    public static volatile SingularAttribute<Putovanja, String> aerodromZavrsni;

}