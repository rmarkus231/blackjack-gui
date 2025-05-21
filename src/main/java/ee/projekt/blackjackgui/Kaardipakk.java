package ee.projekt.blackjackgui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/*Kaardipakk haldab ühte või mitut kaardipakki, segamist ja kaartide jagamist.
 */
public class Kaardipakk {
    private ArrayList<Kaart> kaardid;
    public static char[] mastid = {'♣','♦','♠','♥'};
    public static String[] tugevused = {"A","K","Q","J","10","9","8","7","6","5","4","3","2"};
    private final int pakkide_arv;

    /*Loob ja segab uue Kaardipaki vastavalt antud pakkide arvule.
     */
    public Kaardipakk(int pakkideArv) throws Exception {
        this.pakkide_arv = pakkideArv;
        this.kaardid = new ArrayList<>();
        for (int i = 0; i < pakkideArv; i++) {
            create();
        }
        shuffle();
    }

    public int getPakkide_arv() {
        return pakkide_arv;
    }

    /*Lisab kaardid massiivist vastavalt mastidele ja tugevustele.
     */
    private void create() throws Exception {
        for (char m : mastid) {
            for (String t : tugevused) {
                kaardid.add(new Kaart(m, t));
            }
        }
    }

    /*Segab kaardipaki juhuslikkuse saavutamiseks.
     */
    public void shuffle() {
        Collections.shuffle(kaardid);
    }

    /*Taastab paki algseisundisse, kasutades sama pakkide arvu.
     */
    public void reset() throws Exception {
        kaardid.clear();
        for (int i = 0; i < pakkide_arv; i++) {
            create();
        }
        shuffle();
    }

    /*Võtab pakist esimese kaardi ja tagastab selle. Visatakse erind, kui pakk on tühi.
     */
    public Kaart jaga() {
        if (kaardid.isEmpty()) {
            throw new NoSuchElementException("Kaardipakk on tühi!");
        }
        return kaardid.remove(0);
    }

    /*Alias vana nime säilitamiseks.
     */
    public Kaart getKaart() {
        return jaga();
    }

    /*Konverteerib masti-sümboli tekstiliseks nimeks pildifailide jaoks.
     */
    public static String mastConv(char m) {
        switch (m) {
            case '♣': return "risti";
            case '♦': return "ruutu";
            case '♠': return "poti";
            case '♥': return "ärtu";
        }
        throw new IllegalArgumentException("Tundmatu mast: " + m);
    }
}