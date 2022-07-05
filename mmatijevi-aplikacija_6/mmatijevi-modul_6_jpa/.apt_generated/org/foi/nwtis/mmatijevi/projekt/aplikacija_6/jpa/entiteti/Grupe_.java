package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.processing.Generated;
import org.foi.nwtis.mmatijevi.projekt.aplikacija_6.jpa.entiteti.Korisnici;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-06-17T20:01:40", comments="EclipseLink-3.0.2.v20210716-re8d4b571c9")
@StaticMetamodel(Grupe.class)
public class Grupe_ { 

    public static volatile ListAttribute<Grupe, Korisnici> korisnicis;
    public static volatile SingularAttribute<Grupe, String> naziv;
    public static volatile SingularAttribute<Grupe, String> grupa;

}