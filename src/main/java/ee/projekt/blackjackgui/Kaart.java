package ee.projekt.blackjackgui;

import javafx.scene.Node;

public class Kaart extends Node implements Comparable<Kaart>{
    private char mast;
    private String tugevus;
    //kasutatud aritmeetikas ja võrdluses
    private int tugevus_i;

    public static int tugevusTeisendus(String t) throws Exception{
        //vaatab kas char tüüp on vahemikus unicode 0-9
        try{
            Integer i = Integer.valueOf(t);
            return i.intValue();
        }catch (NumberFormatException e){
            //kui NumberFormatException siis tegemist A-J
            switch (t){
                case "A":
                    return 11;
                case "K", "Q", "J":
                    return 10;
                default:
                    throw new Exception("Ebasobiv kaardi väärtus");
            }
        }
    }

    public Kaart(char mast, String tugevus) throws Exception{
        //kõik viskab objection ja siis nendega tegetakse mainis,
        //generally good practice n.ö chainida exceptions üles.
        this.mast = mast;
        this.tugevus = tugevus;
        this.tugevus_i = tugevusTeisendus(tugevus);
    }

    public boolean onÄss(){
        return this.tugevus.equals("A");
    }

    public int getTugevus_i() {
        return tugevus_i;
    }

    @Override
    public String toString() {
        return mast+tugevus;
    }

    public boolean samaTugevus(String t){
        return this.tugevus.equals(t);
    }

    @Override
    public int compareTo(Kaart o) {
        if(this.mast == o.getTugevus_i()){
            return 0;
        } else if (this.mast < o.getTugevus_i()) {
            return -1;
        }else{
            return 1;
        }
    }

    public char getMast() {
        return mast;
    }

    public String getTugevus() {
        return tugevus;
    }
}
