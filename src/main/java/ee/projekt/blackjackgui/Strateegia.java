package ee.projekt.blackjackgui;
import java.util.stream.IntStream;

public class Strateegia {
    //funktsioonid mida robot mängija kasutaks
    static public int valik(Käsi ise, Kaart diiler){
        int sum = ise.sum();
        boolean pehme = ise.Pehme();
        int diiler_sum = diiler.getTugevus_i();
        String[] A7 = {"A","7"};
        if(diiler_sum >=7 && sum < 17){
            return 1;
        }else if(sum < 12){
            return 1;
        }else if((diiler_sum == 2 || diiler_sum == 3) && sum == 12){
            return 1;
        } else if (pehme && sum <= 17) {
                return 1;
        }else if(ise.sisaldab(A7) && diiler_sum >= 9){
            return 1;
        }
        return 2;
    }

    //might be used later
    static public double tõenäosusN(String t, int[] kaardid_mängus) throws Exception {
        //tõenäosus, et tuleb kaart N
        int otsitav = 0;
        if(t.equals("A")){
            otsitav = 10;
        }else{
            otsitav = Kaart.tugevusTeisendus(t)-1;
        }
        int alles = kaardid_mängus[otsitav];
        int kokku = IntStream.of(kaardid_mängus).sum();
        return (double)alles / (double)kokku;
    }

    static public double tõenäosusBust(int sum, int[] kaardid_mängus){
        if(sum >= 21){
            return 1;
        }
        double kaarte = 0;
        double kokku = IntStream.of(kaardid_mängus).sum();
        for(int i = 1; i < kaardid_mängus.length; i++){
            if(i+1+sum > 21){
                kaarte += kaardid_mängus[i];
            }
        }
        return kaarte / kokku;
    }
}