package ee.projekt.blackjackgui;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class G_peidetudKaart extends G_kaart{

    public G_peidetudKaart(Kaart kaart) {
        this.kaart = kaart;
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
        Text center = new Text("??");
        center.setFont(Font.font("Segoe UI", 36));
        center.setFill(Color.DARKGRAY); // kui tegemist tundmatu kaardiga siis mast ja kaks ?? sinist on keskel

        // Lõplik kokkupanek
        this.setPrefSize(WIDTH, HEIGHT);
        this.setMaxSize(WIDTH, HEIGHT);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(bg, center);
    }
}
