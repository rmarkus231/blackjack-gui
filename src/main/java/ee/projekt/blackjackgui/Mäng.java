package ee.projekt.blackjackgui;
import java.util.ArrayList;
import java.util.Scanner;

public class Mäng {
    //game loop ja seotud asjad siia
    private Käsi diiler;
    private ArrayList<Käsi> mängijad;
    private Kaardipakk kaardipakk;
    public static Scanner sc = new Scanner(System.in);

    public Mäng() throws Exception {
        int n = pakkideArv();
        this.kaardipakk = new Kaardipakk(n);
        this.diiler = new Diiler("Diiler");
        this.mängijad = mängijad();
    }

    public ArrayList<Käsi> mängijad(){
        int valik = 1;
        ArrayList<Käsi> ret = new ArrayList<>();
        System.out.println("Sisestage mängus olevate mängijate arv:");
        while(true) {
            try {
                valik = Integer.parseInt(sc.nextLine());
                if (valik > 0) {
                    break;
                } else {
                    System.out.println("Mängijaid peab olema vähemalt 1!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Peab olema täisarv");
            }
        }
        int temp;
        for (int i = 0; i < valik; i++) {
            String nimi;
            boolean bot = false;
            boolean keeruline = false;

            System.out.println((i + 1) + ". mängija nimi: ");
            nimi = sc.nextLine();

            int robotivalik;
            while (true) {
                System.out.println("On robot? 1-ei, 2-jah");
                try {
                    robotivalik = Integer.parseInt(sc.nextLine());
                    if (robotivalik == 1 || robotivalik == 2) {
                        bot = robotivalik == 2;
                        break;
                    } else {
                        System.out.println("Sisesta 1 või 2!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Sisesta number 1 või 2!");
                }
            }
            if (bot) {
                int raskusvalik;
                while (true) {
                    System.out.println("Roboti raskustase? 1-kerge, 2-raske");
                    try {
                        raskusvalik = Integer.parseInt(sc.nextLine());
                        if (raskusvalik == 1 || raskusvalik == 2) {
                            keeruline = raskusvalik == 2;
                            break;
                        } else {
                            System.out.println("Sisesta 1 või 2!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Sisesta number 1 või 2!");
                    }
                }
            }
            ret.add(new Mängija(nimi, !bot, keeruline));
        }
        return ret;
    }

    public int pakkideArv(){
        int valik;
        System.out.println("Sisestage mängus olevate kaardipakkide arv:");
        while(true) {
            try {
                valik = Integer.parseInt(sc.nextLine());
                if (valik > 0) {
                    return valik;
                } else {
                    System.out.println("Pakkide arv peab olema suurem kui 0!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Peab olema täisarv");
            }
        }
    }

    public void start() throws Exception {
        boolean mängiUuesti = true; //tõeväärtus mängu uuesti mängimiseks

        // Mängija käik
        while (mängiUuesti) {

            // mängu alguses mängija ja diileri kaardid tagasi ning kaardipaki segamine
            kaardipakk.reset();
            kaardipakk.shuffle();
            mängijad.forEach(Käsi::clear);
            diiler.clear();

            // mängu alustamine ja kaardide jagamine. Mõlemale 2 kaarti.
            for(Käsi m : mängijad){
                m.lisa(kaardipakk.getKaart());
                m.lisa(kaardipakk.getKaart());
            }

            diiler.lisa(kaardipakk.getKaart());
            diiler.lisa(kaardipakk.getKaart());
            System.out.println("Diileri nähtav kaart: " + diiler.getKaardid().getFirst());

            for(Käsi m : mängijad){
                m.printSkoor();
            }
            for(Käsi m : mängijad){
                m.tegutse(kaardipakk,diiler.getKaardid().getFirst(),mängijad);
            }
            diiler.tegutse(kaardipakk,diiler.getKaardid().getFirst(),mängijad);


            // Võitja kontroll
            // Diileri summa ja väljaprint
            int diilersum = diiler.sum();
            diiler.printSkoor();
            //kõik suurima skooriga mängijad, juhul kui sama skooriga mängijaid on mitu
            int m_skoor = 0;
            ArrayList<Käsi> võitjad = new ArrayList<>();
            int max = 0;
            for(Käsi m : mängijad){
                m_skoor = m.sum();
                if(!m.väljas()){
                    if (m_skoor > max) {
                        max = m_skoor;
                        võitjad.clear();
                        võitjad.add(m);
                    } else if (m_skoor == max) {
                        võitjad.add(m);
                    }
                }
            }
            if (võitjad.isEmpty() && diiler.väljas()) {
                System.out.println("Keegi ei võitnud.");
            } else if (!võitjad.isEmpty() && (diiler.väljas() || max > diilersum)) {
                if (võitjad.size() > 1) {
                    System.out.println("Viiki jäid: ");
                    for (Käsi m : võitjad) {
                        System.out.printf("%s, skoor: %d\n", m.nimi, m.sum());
                    }
                } else {
                    Käsi võitja = võitjad.getFirst();
                    System.out.printf("Võitis %s, skoor: %d\n", võitja.nimi, võitja.sum());
                }
            } else if (max == diilersum) {
                System.out.println("Viik diileriga.");
            } else {
                System.out.printf("Võitis %s, skoor: %d\n", diiler.nimi, diilersum);
            }

            mängiUuesti = jätkaValik() == 1;
        }

        System.out.println("Aitäh mängimast!");
    }

    public static int jätkaValik(){
        System.out.println("\nKas soovid uuesti mängida?");
        System.out.println("1 - Jätka.");
        System.out.println("2 - Lõpeta mäng.");
        int valik;
        while (true) {
            System.out.println("Valik: ");
            try {
                valik = Integer.parseInt(sc.nextLine());
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
}
