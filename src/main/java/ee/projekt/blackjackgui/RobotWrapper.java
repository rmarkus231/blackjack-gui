package ee.projekt.blackjackgui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class RobotWrapper {
    //see klass laseb robotil käituda nagu mängija
    //sellel klassil ei ole konstruktoreid

    public static int run(G_käsi bot, ArrayList<G_käsi> prevPlayers, G_käsi dealer,Kaardipakk deck) {
        //return 1 for hit
        //returns 0 for stand
        int pakkid = deck.getPakkide_arv();
        int kaartide_arv = 54 * pakkid - 1; //-1 on diiler
        //teab ~ mitu igat kaarti on mängus
        int[] kaardid_mängus = {0, 4, 4, 4, 4, 4, 4, 4, 4, 16, 4};
        for (int i : kaardid_mängus) {
            i *= pakkid;
        }
        for (G_käsi k : prevPlayers) {
            kaartide_arv -= k.getKaardid().size();
            for (Kaart k2 : k.getKaardid()) {
                if (k2.onÄss()) {
                    kaardid_mängus[10] -= 1;
                } else {
                    kaardid_mängus[k2.getTugevus_i() - 1] -= 1;
                }
            }
        }
        kaardid_mängus[dealer.getKaart(0).getTugevus_i() - 1] -= 1;
        double bust_protsent = Strateegia.tõenäosusBust(G_Main.arvutaSumma(bot.getKaardid()), kaardid_mängus);
        if(bust_protsent > 0.5){
            return 1;
        }
        return 0;
    }
}
