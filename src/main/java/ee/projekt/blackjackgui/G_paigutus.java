package ee.projekt.blackjackgui;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;

public class G_paigutus extends GridPane {
    private int width;
    private int height;

    public G_paigutus(int width, int height) {
        //suurus kastides, soovitatult paaritu arv, et saaks keskele asju paigutada
        super();
        this.width = width;
        this.height = height;
        for (int i = 0; i < width; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            this.getColumnConstraints().add(col);
        }
        for (int i = 0; i < height; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(10);
            this.getRowConstraints().add(row);
        }
    }

    public void addObject(Pane node, int[] posSpan) {
        if(posSpan.length != 4) {
            throw new WrongArg("Argumendid peavad olema col,row,colSpan,rowSpan");
        }
        GridPane.setConstraints(node, posSpan[0], posSpan[1], posSpan[2], posSpan[3]);
        GridPane.setHalignment(node, HPos.CENTER);
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setHalignment(node, HPos.CENTER);
        node.prefWidthProperty().bind(this.widthProperty().divide(this.width));
        node.prefHeightProperty().bind(this.heightProperty().divide(this.height));
        this.getChildren().add(node);
    }
}
