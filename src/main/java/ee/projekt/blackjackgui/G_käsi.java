package ee.projekt.blackjackgui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * G_käsi loob horisontaalse TilePane’i, kuhu lisatakse G_kaart-vormingus kaardid.
 * Kaardid on üksteisele osaliselt kattuvad, et imiteerida reaalse käe efekti.
 */
public class G_käsi {
    private final TilePane käsi;
    private final List<Kaart> kaardid;

    /**
     * @param initialCards esialgselt jagatud kaartide loend
     */
    public G_käsi(List<Kaart> initialCards) {
        this.kaardid = new ArrayList<>(initialCards);
        käsi = new TilePane();
        käsi.setId("player-hand");
        käsi.getStyleClass().add("tile-pane");

        // NEGATIIVNE vahe, et kaartide pooled kattuksid
        käsi.setHgap(-40);
        käsi.setVgap(0);
        käsi.setTileAlignment(Pos.CENTER);
        käsi.setAlignment(Pos.CENTER);
        käsi.setOrientation(Orientation.HORIZONTAL);

        // Lisa esialgsed kaardid
        for (Kaart kaart : initialCards) {
            käsi.getChildren().add(new G_kaart(kaart));
        }
    }

    /**
     * Lisab jooksvalt ühe kaardi nii GUI-sse kui ka sisemisse loendisse.
     */
    public void lisaKaart(Kaart kaart) {
        kaardid.add(kaart);
        käsi.getChildren().add(new G_kaart(kaart));
    }

    /** @return Kaardid selles käes */
    public List<Kaart> getKaardid() {
        return kaardid;
    }

    /** @return JavaFX-solm, mida saab Scene’ile panna */
    public TilePane getNode() {
        return käsi;
    }
}