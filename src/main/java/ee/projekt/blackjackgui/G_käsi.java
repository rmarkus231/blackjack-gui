package ee.projekt.blackjackgui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**G_käsi loob horisontaalse TilePane’i, kuhu lisatakse G_kaart-vormingus kaardid.
 * Kaardid on üksteisele osaliselt kattuvad, et imiteerida reaalse käe efekti.
 */
public class G_käsi {
    private final TilePane käsi;
    private final GridPane grid;
    private final Label skoorSilt;
    private final List<Kaart> kaardid;

    //esialgselt jagatud kaartide loend
    public G_käsi(List<Kaart> initialCards, boolean overlapped) {
        this.kaardid = new ArrayList<>(initialCards);
        käsi = new TilePane();
        skoorSilt = new Label();
        grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        skoorSilt.setStyle(CssUtil.getCss("skoor-silt"));
        käsi.getStyleClass().add("tile-pane");

        // NEGATIIVNE vahe, et kaartide pooled kattuksid
        käsi.setStyle(CssUtil.getCss("tile-pane"));
        käsi.setVgap(0);
        käsi.setTileAlignment(Pos.CENTER);
        käsi.setAlignment(Pos.CENTER);
        käsi.setOrientation(Orientation.HORIZONTAL);

        // Lisa esialgsed kaardid
        for (Kaart kaart : initialCards) {
            käsi.getChildren().add(new G_kaart(kaart));
        }

        ColumnConstraints colC = new ColumnConstraints();
        colC.setHgrow(Priority.ALWAYS);
        RowConstraints rowC = new RowConstraints();
        rowC.setVgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(colC);
        grid.getRowConstraints().addAll(rowC);
        grid.add(skoorSilt, 0, 0);
        grid.add(käsi, 0, 1);
    }
    public G_käsi(List<Kaart> initialCards) {
        this(initialCards, true);
    }

    //Lisab jooksvalt ühe kaardi nii GUI-sse kui ka sisemisse loendisse.
    public void lisaKaart(Kaart kaart) {
        kaardid.add(kaart);
        arvutaSkoor();
        käsi.getChildren().add(new G_kaart(kaart));
    }

    // tagastab kaardid selles käes
    public List<Kaart> getKaardid() {
        return kaardid;
    }

    public Node getNode() {
        return grid;
    }

    public void arvutaSkoor(){
        skoorSilt.setText("Skoor: " + Käsi.sum(kaardid));
        return;
    }
}