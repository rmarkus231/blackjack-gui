package ee.projekt.blackjackgui;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Käsi implements Comparable<Käsi> {
    private ArrayList<Kaart> kaardid;
    public String nimi;
    private boolean pehme = false;
    protected boolean bust = false;

    public Käsi(String nimi){
        this.kaardid = new ArrayList<>();
        this.nimi = nimi;
    }

    public void lisa(Kaart kaart){
        kaardid.add(kaart);
    }

    public abstract int tegutse(Kaardipakk kaardipakk, Kaart Diiler, ArrayList<Käsi> mängijad) throws Exception;

    public int getValik() throws Exception{
        Scanner sc = Mäng.sc;
        System.out.println("\n" + nimi + ", sinu käik!");
        System.out.println("1 - Võta kaart juurde (Hit).");
        System.out.println("2 - Jää seisma (Stand).");
        int valik;
        while (true) {
            System.out.println("Valik: ");
            try {
                valik = Integer.parseInt(Mäng.sc.nextLine());
                if (valik == 1 || valik == 2) {
                    return valik;
                } else {
                    System.out.print("Vale valik. Sisesta number 1 või 2!");
                }
            } catch (NumberFormatException e) {
                System.out.print("Sisesta number 1 või 2!");
            }
        }
    }

    public int sum(){
        //lisasin asja mis loeb ässa kui 1 juhul kui skoor on üle 21.
        int sum = 0;
        int ässad = getÄssad();
        for(int i = 0; i <= ässad;i++){
            sum = sumArvestadesÄssad(i);
            if (sum <= 21) {
                break;
            }
        }
        return sum;
    }
    private int sumArvestadesÄssad(int ässade_arv){
        //võtab summa arvestades, et N ässa on väärtusega 1
        int sum = 0;
        int i = 0;
        for(Kaart kaart : kaardid){
            if(kaart.onÄss() && i < ässade_arv){
                this.pehme = false;
                sum += 1;
                i++;
            }else{
                sum += kaart.getTugevus_i();
            }
        }
        return sum;
    }

    public int getÄssad(){
        int sum = 0;
        for(Kaart kaart : kaardid){
            if(kaart.onÄss()){
                pehme = true;
                sum++;
            }
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(this.nimi + " kaardid: ");
        for(Kaart kaart : kaardid){
            ret.append(kaart).append(" ");
        }
        return ret.toString();
    }

    public boolean Pehme() {
        return pehme;
    }

    public void clear(){
        kaardid.clear();
        this.pehme = false;
        this.bust = false;
    }

    @Override
    public int compareTo(Käsi o) {
        int sum1 = sum();
        int sum2 = o.sum();
        return Integer.compare(sum1, sum2);
    }

    public boolean sisaldab(String[] t){
        //vaatab kas see käsi sisaldab märgitud kaarte, sisend []
        int sum = 0;
        ArrayList<Kaart> pakk_koopia = (ArrayList<Kaart>) this.kaardid.clone();
        for(String s : t){
            for(Kaart k : pakk_koopia){
                if(k.samaTugevus(s)){
                    sum++;
                    //et ei oleks kordusi
                    pakk_koopia.remove(k);
                    break;
                }
            }
        }
        if(sum == t.length){
            return true;
        }
        return false;
    }

    public int kaartideArv(){
        return kaardid.size();
    }

    public void printSkoor(){
        System.out.println("\n" +this+" skoor: "+sum());
    }
    public ArrayList<Kaart> getKaardid() {
        return new ArrayList<>(kaardid);
    }

    public boolean väljas() {
        return bust;
    }
}