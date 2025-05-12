package ee.projekt.blackjackgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * G_Main käivitab Blackjack mängu graafilise kasutajaliidese.
 * Esmalt konfiguratsioon, seejärel korduv turnipõhine mäng.
 */
public class G_Main extends Application {
    private Kaardipakk deck;
    private G_käsi dealerView;
    private List<G_käsi> playerHands;
    private List<PlayerConfig> configs;
    private int currentPlayer = 0;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        kuvaSeadistusVaade();
    }

    private Stage primaryStage;

    /**
     * Kuvab konfigureerimise vaate.
     */
    /**
     * Uuendab dünaamiliselt mängijate nime- ja robotivälju konfiguratsioonivaates.
     * @param box konteiner väljade jaoks
     * @param count mängijate arv
     */
    private void uuendaMangujateKonfiguratsioonivaljad(VBox box, int count) {
        box.getChildren().clear();
        for (int i = 1; i <= count; i++) {
            Label lbl = new Label("Mängija " + i + " nimi:");
            TextField nameField = new TextField("Mängija" + i);
            CheckBox botCheck = new CheckBox("robot?");
            box.getChildren().add(new HBox(10, lbl, nameField, botCheck));
        }
    }

    private void kuvaSeadistusVaade() {
        Label decksLabel = new Label("Pakkide arv:");
        Spinner<Integer> decksSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1)
        );
        HBox decksBox = new HBox(10, decksLabel, decksSpinner);
        decksBox.setAlignment(Pos.CENTER_LEFT);

        Label playersLabel = new Label("Mängijate arv:");
        Spinner<Integer> playersSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 2)
        );
        HBox playersBox = new HBox(10, playersLabel, playersSpinner);
        playersBox.setAlignment(Pos.CENTER_LEFT);

        VBox playersConfigBox = new VBox(10);
        uuendaMangujateKonfiguratsioonivaljad(playersConfigBox, playersSpinner.getValue());
        playersSpinner.valueProperty().addListener((obs, oldV, newV) ->
                uuendaMangujateKonfiguratsioonivaljad(playersConfigBox, newV)
        );

        Button startBtn = new Button("Alusta mängu");
        startBtn.setOnAction(e -> {
            int deckCount = decksSpinner.getValue();
            configs = new ArrayList<>();
            for (var node : playersConfigBox.getChildren()) {
                HBox hb = (HBox) node;
                TextField tf = (TextField) hb.getChildren().get(1);
                CheckBox cb = (CheckBox) hb.getChildren().get(2);
                configs.add(new PlayerConfig(tf.getText(), cb.isSelected()));
            }
            initGame(primaryStage, deckCount);
        });

        VBox root = new VBox(15, decksBox, playersBox, playersConfigBox, startBtn);
        root.setPadding(new Insets(20));
        primaryStage.setTitle("Blackjack - Konfiguratsioon");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    // loogelised UI elemendid
    private BorderPane gameRoot;
    private VBox controlsBox;
    private Button hitBtn, standBtn;

    /**
     * Käivitab mänguvoo ja kuvab mängu akna.
     */
    private void initGame(Stage stage, int deckCount) {
        try {
            deck = new Kaardipakk(deckCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dealerView = new G_käsi(new ArrayList<>());
        playerHands = new ArrayList<>();
        currentPlayer = 0;

        // Ülakeha (diiler)
        Label dealerLabel = new Label("Diiler");
        // Dealer gets two initial cards
        dealerView.lisaKaart(deck.jaga());
        dealerView.lisaKaart(deck.jaga());
        Pane dealerPane = dealerView.getNode();
        dealerPane.setId("dealer-hand"); dealerPane.setScaleX(0.6); dealerPane.setScaleY(0.6);
        VBox topBox = new VBox(5, dealerLabel, dealerPane);
        topBox.setAlignment(Pos.CENTER);

        // Allkorpus (mängijad)
        HBox playersBox = new HBox(20);
        playersBox.setAlignment(Pos.CENTER);
        for (PlayerConfig pc : configs) {
            G_käsi hand = new G_käsi(new ArrayList<>());
            playerHands.add(hand);
            Pane pPane = hand.getNode();
            pPane.setId(pc.isBot?"dealer-hand":"player-hand");
            pPane.setScaleX(0.6); pPane.setScaleY(0.6);
            Label nameLbl = new Label(pc.name);
            VBox box = new VBox(5, nameLbl, pPane);
            box.setAlignment(Pos.CENTER);
            playersBox.getChildren().add(box);
            // esimesed kaardid
            hand.lisaKaart(deck.jaga());
            hand.lisaKaart(deck.jaga());
        }

        // Nupud
        hitBtn = new Button("Hit");
        standBtn = new Button("Stand");
        hitBtn.setMaxWidth(Double.MAX_VALUE); standBtn.setMaxWidth(Double.MAX_VALUE);
        controlsBox = new VBox(10, hitBtn, standBtn);
        controlsBox.setAlignment(Pos.TOP_CENTER);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setPrefWidth(120);

        hitBtn.setOnAction(e -> onHit());
        standBtn.setOnAction(e -> onStand());

        // Layout
        gameRoot = new BorderPane();
        gameRoot.setTop(topBox);
        gameRoot.setBottom(playersBox);
        gameRoot.setRight(controlsBox);
        gameRoot.setBackground(new Background(new BackgroundFill(
                Color.web("#F5F5F5"), CornerRadii.EMPTY, Insets.EMPTY)));

        stage.setTitle("Blackjack");
        stage.setScene(new Scene(gameRoot, 800, 600));
        stage.show();

        updateControls();
    }

    /**
     * Käib läbi Hit toimingu praegusel mängijal.
     */
    private void onHit() {
        G_käsi hand = playerHands.get(currentPlayer);
        hand.lisaKaart(deck.jaga());
        if (arvutaSumma(hand.getKaardid()) >= 21) {
            onStand();
        }
    }

    /**
     * Käib läbi Stand toimingu praegusel mängijal ja liigub järgmise juurde või diileri juurde.
     */
    private void onStand() {
        currentPlayer++;
        if (currentPlayer < playerHands.size()) {
            updateControls();
        } else {
            controlsBox.setVisible(false);
            // Robotmängijad
            for (int i = 1; i < playerHands.size(); i++) {
                G_käsi ph = playerHands.get(i);
                while (arvutaSumma(ph.getKaardid()) < 17) {
                    ph.lisaKaart(deck.jaga());
                }
            }
            // Diiler
            while (arvutaSumma(dealerView.getKaardid()) < 17) {
                dealerView.lisaKaart(deck.jaga());
            }
            // **Kutsu tulemuse kuvamine**
            showResult();
        }
    }

    /**
     * Uuendab nupud ja sildid järgmiseks mängijaks.
     */
    private void updateControls() {
        PlayerConfig pc = configs.get(currentPlayer);
        hitBtn.setText("Hit: " + pc.name);
        standBtn.setText("Stand: " + pc.name);
        hitBtn.setDisable(pc.isBot);
        standBtn.setDisable(pc.isBot);
        // robotid saavad automaatse käigu ehk kohe Stand
        if (pc.isBot) {
            onStand();
        }
    }

    /*
     * Arvutab käe väärtuse Blackjack reeglite järgi.
     */
    private int arvutaSumma(List<Kaart> kaardid) {
        int sum = 0, aces = 0;
        for (Kaart k : kaardid) {
            String t = k.getTugevus();
            switch (t) {
                case "A": aces++; break;
                case "K": case "Q": case "J": case "10": sum += 10; break;
                default: sum += Integer.parseInt(t);
            }
        }
        for (int i = 0; i < aces; i++) sum += (sum + 11 <= 21 ? 11 : 1);
        return sum;
    }

    /**
     * Abiklass mängija nime ja roboti oleku hoidmiseks.
     */
    private static class PlayerConfig {
        final String name;
        final boolean isBot;
        PlayerConfig(String name, boolean isBot) {
            this.name = name;
            this.isBot = isBot;
        }
    }
    private void showResult() {
        int dealerSum = arvutaSumma(dealerView.getKaardid());
        String winner = "Diiler";
        int best = dealerSum <= 21 ? dealerSum : 0;

        for (int i = 0; i < playerHands.size(); i++) {
            int s = arvutaSumma(playerHands.get(i).getKaardid());
            if (s <= 21 && s > best) {
                best = s;
                winner = configs.get(i).name;
            }
        }

        Label resultLabel = new Label(
                String.format("Mäng läbi! Võitja: %s (%d)", winner, best)
        );
        resultLabel.getStyleClass().add("result");
        BorderPane.setAlignment(resultLabel, Pos.CENTER);
        gameRoot.setCenter(resultLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}