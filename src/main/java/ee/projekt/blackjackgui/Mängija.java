package ee.projekt.blackjackgui;
import java.util.ArrayList;

public class Mängija extends Käsi{
    private boolean päris;
    private boolean raske;

    public Mängija(String nimi, boolean päris, boolean keeruline){
        super(nimi);
        this.päris = päris;
        this.raske = keeruline;
    }

    private int valikRobot(Kaardipakk kaardipakk, Kaart diiler, ArrayList<Käsi> mängijad) throws Exception {
        if(raske) {
            int pakkid = kaardipakk.getPakkide_arv();
            int kaartide_arv = 54 * pakkid - 1; //-1 on diiler
            //teab ~ mitu igat kaarti on mängus
            int[] kaardid_mängus = {0, 4, 4, 4, 4, 4, 4, 4, 4, 16, 4};
            for (int i : kaardid_mängus) {
                i *= pakkid;
            }
            for (Käsi k : mängijad) {
                kaartide_arv -= k.kaartideArv();
                for (Kaart k2 : k.getKaardid()) {
                    if (k2.onÄss()) {
                        kaardid_mängus[10] -= 1;
                    } else {
                        kaardid_mängus[k2.getTugevus_i() - 1] -= 1;
                    }
                }
            }
            kaardid_mängus[diiler.getTugevus_i() - 1] -= 1;
            double bust_protsent = Strateegia.tõenäosusBust(this.sum(), kaardid_mängus);
            if(bust_protsent > 0.5){
                return 2;
            }
            return 1;
        }else{
            return Strateegia.valik(this,diiler);
        }
    }

    @Override
    public int tegutse(Kaardipakk kaardipakk, Kaart diiler, ArrayList<Käsi> mängijad) throws Exception {
        //tagastab kaardtide summa
        int valik;
        int sum = sum();
        while (true) {
            if(päris) {
                valik = this.getValik();
            }else{
                valik = valikRobot(kaardipakk, diiler, mängijad);
            }
            if (valik == 1) {
                Kaart uusKaart = kaardipakk.getKaart();
                lisa(uusKaart);
                System.out.println(nimi+" sai kaardi: " + uusKaart);
                //tahtsin näha skoore
                sum = sum();
                System.out.println(nimi+" käsi on: " + this.getKaardid()+ " skoor: " + sum);
                if (sum > 21) {
                    System.out.println( nimi+ " bustis");
                    bust = true;
                    break;
                } else if (sum() == 21) {
                    break;
                }
            } else if (valik == 2) {
                break;
            } else {
                System.out.println("Vale valik, proovi uuesti!");
            }
        }
        return sum;
    }

}
