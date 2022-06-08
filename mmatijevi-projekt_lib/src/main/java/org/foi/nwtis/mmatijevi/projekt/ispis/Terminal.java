package org.foi.nwtis.mmatijevi.projekt.ispis;

/**
 * <h3>Statička klasa za konzolni ispis</h3>
 * <p>Ova klasa omogućuje ispis poruka u raznim bojama.
 * <p>Njezine metode su:
 * <ul>
 * <li>normalIspis - bijelo
 * <li>infoIspis - plavo
 * <li>uspjehIspis - zeleno
 * <li>pozorIspis - žuto
 * <li>greskaIspis - crveno
 * </ul>
 * @author Mislav Matijević
 */
public final class Terminal {

    private static final String ANSI_NORMALNO = "\u001B[0m";
    private static final String ANSI_PLAVO = "\u001B[36m";
    private static final String ANSI_ZELENO = "\u001B[32m";
    private static final String ANSI_ZUTO = "\u001B[33m";
    private static final String ANSI_CRVENO = "\u001B[31m";

    /** 
     * Vraća ispis na bijelu boju nakon obojenog ispisa.
     */
    private static void vratiNaNormalno() {
        normalIspis("");
    }

    /** 
     * Ispisuje poruku bijelom bojom. Izvršava "flush" naredbu.
     * @param porukaPogreske Poruka za ispis.
     */
    public static void normalIspis(String porukaPogreske) {
        System.out.println(ANSI_NORMALNO + porukaPogreske);
        System.out.flush();
    }

    /** 
     * Ispisuje poruku plavom bojom.
     * @param porukaPogreske Poruka za ispis.
     */
    public static void infoIspis(String porukaPogreske) {
        System.out.println(ANSI_PLAVO + porukaPogreske);
        vratiNaNormalno();
    }

    /** 
     * Ispisuje poruku zelenom bojom.
     * @param porukaPogreske Poruka za ispis.
     */
    public static void uspjehIspis(String porukaPogreske) {
        System.out.println(ANSI_ZELENO + porukaPogreske);
        vratiNaNormalno();
    }

    /** 
     * Ispisuje poruku žutom bojom.
     * @param porukaPogreske Poruka za ispis.
     */
    public static void pozorIspis(String porukaPogreske) {
        System.out.println(ANSI_ZUTO + porukaPogreske);
        vratiNaNormalno();
    }

    /** 
     * Ispisuje poruku crvenom bojom.
     * @param porukaPogreske Poruka za ispis.
     */
    public static void greskaIspis(String porukaPogreske) {
        System.out.println(ANSI_CRVENO + porukaPogreske);
        vratiNaNormalno();
    }
}
