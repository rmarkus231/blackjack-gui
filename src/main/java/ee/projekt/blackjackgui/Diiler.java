package ee.projekt.blackjackgui;
import java.util.ArrayList;

public class Diiler extends Käsi{
    public Diiler(String nimi) {
        super(nimi);
    }

    @Override
    public int tegutse(Kaardipakk kaardipakk, Kaart Diiler, ArrayList<Käsi> mängijad) throws Exception {
        int sum = sum();
        while (sum < 17) {
            Kaart uusKaart = kaardipakk.getKaart();
            lisa(uusKaart);
            System.out.println("Diiler võtab kaardi: " + uusKaart);
            sum = sum();
        }
        if (sum > 21) {
            this.bust = true;
        }
        return sum;
    }
}
