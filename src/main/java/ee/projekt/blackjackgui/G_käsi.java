package ee.projekt.blackjackgui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;


/**
 * G_käsi kuvab kaardikäe TilePane'is.
 * Kaardid ilmuvad horisontaalselt pooleldi kattudes.
 * Võimaldab näidata skoori valge suurema fondiga.
 */
public class G_käsi {
    private final GridPane gridPane;
    private final TilePane käsiPane;
    private final Label skoorSilt;
    private final List<Kaart> kaardid;
    private List<G_kaart> p_kaardid= new ArrayList<>();
    private PlayerConfig config;

    /**
     * @param initialCards algkaardid
     * @param showScore kas näidata skoori silt
     */

    public G_käsi(List<Kaart> initialCards, boolean showScore, PlayerConfig conf) {
        this.config = conf;
        // Loo gridpane kahte ritta (skoor ja käed)
        gridPane = new GridPane();
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().add(cc);
        gridPane.getRowConstraints().addAll(
                new RowConstraints(), // rida skoorile
                new RowConstraints()  // rida käe vaatele
        );

        // Skoori silt
        skoorSilt = new Label();
        skoorSilt.setTextFill(Color.WHITE);
        skoorSilt.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
        if (showScore) {
            gridPane.add(skoorSilt, 0, 0);
        }

        // Käe pane: kaardid reas pooleldi kattudes
        käsiPane = new TilePane(Orientation.HORIZONTAL);
        // overlappuse jaoks negatiivne hgap
        if (showScore) {
            käsiPane.setHgap(-60);
        } else {
            käsiPane.setHgap(5);
        }
        käsiPane.setAlignment(Pos.CENTER);
        gridPane.add(käsiPane, 0, showScore ? 1 : 0);

        // Init kaardid
        kaardid = new ArrayList<>(initialCards);
        for (Kaart k : initialCards) {
            käsiPane.getChildren().add(new G_kaart(k));
        }
        if (showScore) arvutaSkoor();
    }

    /** Lisab uue kaardi */
    public void lisaKaart(Kaart kaart) {
        //not hidden by default
        kaardid.add(kaart);
        G_kaart k = new G_kaart(kaart);
        käsiPane.getChildren().add(k);
        arvutaSkoor();
    }
    //optional argument
    public void lisaKaart(Kaart kaart, boolean hidden) {
        if(hidden) {
            kaardid.add(kaart);
            G_kaart k = new G_peidetudKaart(kaart);
            p_kaardid.add(k);
            käsiPane.getChildren().add(k);
            arvutaSkoor();
        }else{
            lisaKaart(kaart);
        }
    }

    public void näitaPeidetud(){
        for(G_kaart p : p_kaardid){
                int index = käsiPane.getChildren().indexOf(p);
                G_kaart k = new G_kaart(p.getKaart());
                //un hide ja muuda pilt ära
                //asenda samas kohas (samad dimensioonid jms anyway)
                käsiPane.getChildren().set(index,k);
        }
        gridPane.requestLayout();
    }

    /** Tühjendab käe ja skoori */
    public void clear() {
        kaardid.clear();
        p_kaardid.clear();
        käsiPane.getChildren().clear();
        skoorSilt.setText("");
    }

    /** Tagastab kaardid mudelina */
    public List<Kaart> getKaardid() {
        return kaardid;
    }

    /** Tagastab JavaFX sõlme, mida on võimalik GUI-s kuvada */
    public Node getNode() {
        return gridPane;
    }

    /** Uuendab skoori silt’i teksti */
    private void arvutaSkoor() {
        skoorSilt.setText("Skoor: " + Käsi.sum(kaardid));
    }

    public String getName(){
        //avoid null pointer exception
        if(config != null){
            return config.name;
        }else{
            return "Diiler";
        }
    }

    public boolean isBot(){
        //avoid null pointer exception
        if(config != null){
            return config.isBot;
        }
        return false;
    }

    public Kaart getKaart(int index){
        return kaardid.get(index);
    }
}
