package ee.projekt.blackjackgui;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * G_kaart renderdab mängukaardi JavaFX-i kujunduselementidega:
 * pehme pastel-taust, masti ja tugeva teksti ning nurgasümbolid.
 */
public class G_kaart extends StackPane {
    private static final double WIDTH = 100;
    private static final double HEIGHT = 150;
    private static final double ARC = 10;
    private static final double SHADOW_RADIUS = 4;

    public G_kaart(Kaart kaart) {
        // 1) Taust: helehall aluskast ja pastel-sinine äär
        Rectangle bg = new Rectangle(WIDTH, HEIGHT);
        bg.setFill(Color.web("#FAFAFA"));         // helehall
        bg.setStroke(Color.web("#CFD8DC"));       // sinakashall äär
        bg.setArcWidth(ARC);
        bg.setArcHeight(ARC);
        bg.setEffect(new DropShadow(SHADOW_RADIUS, Color.gray(0, 0.2)));

        // 2) Keskmine tekst: masti sümbol + tugevus
        String suit = String.valueOf(kaart.getMast());
        String rank = kaart.getTugevus();
        Text center = new Text(suit + rank);
        center.setFont(Font.font("Segoe UI", 36));
        center.setFill(isRedSuit(kaart.getMast())
                ? Color.web("#E57373")
                : Color.web("#455A64")
        );

        // 3) Nurgatekst ülemises vasakus
        Text topLeft = new Text(rank + "\n" + suit);
        topLeft.setFont(Font.font("Segoe UI", 14));
        topLeft.setFill(center.getFill());
        topLeft.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        topLeft.setTranslateX(-WIDTH/2 + 8);
        topLeft.setTranslateY(-HEIGHT/2 + 16);

        // 4) Pööratud nurgatekst alumises paremas
        Text bottomRight = new Text(rank + "\n" + suit);
        bottomRight.setFont(Font.font("Segoe UI", 14));
        bottomRight.setFill(center.getFill());
        bottomRight.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        bottomRight.setRotate(180);
        bottomRight.setTranslateX(WIDTH/2 - 8);
        bottomRight.setTranslateY(HEIGHT/2 - 16);

        // Lõplik kokkupanek
        this.setPrefSize(WIDTH, HEIGHT);
        this.setMaxSize(WIDTH, HEIGHT);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(bg, center, topLeft, bottomRight);
    }

    private boolean isRedSuit(char mast) {
        return mast == '♥' || mast == '♦';
    }
}

class testG_kaart {
    public static void main(String[] args) throws Exception {
        //kasutasin path leidmise täätamise kontrollimiseks
        System.out.println();
        G_kaart gk = new G_kaart(new Kaart('♥',"9"));
    }
}
