package ee.projekt.blackjackgui;
public class terminal_main {
    /*TODO
    - game loop
    - erroritega tegelemine
    - teha mingi klass selleks, et handle player käes olevaid kaarte
    võimalik, et mingi player class
    - Kaardipakkile constructor mis teeb kaardipakki kus on segamini näiteks
    N kaardipakki (ei saa kaarte nii hästi lugeda)
    - mingi AI kelle vastu mängid vb ja kui aega on siis talle mingi
    raskus tasemed, ehk kui statistiliselt parima valiku ta võtab
    - suhtlus mängijaga
    */

    public static void main(String[] args) {
        try{
            Mäng mäng = new Mäng();
            mäng.start();

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
