package org.foi.nwtis.mmatijevi.projekt.aplikacija_6.modeli;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JmsPoruka implements Serializable {
    private String poruka;
    private Date vrijeme;
}
