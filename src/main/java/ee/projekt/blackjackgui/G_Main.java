package ee.projekt.blackjackgui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/** G_Main käivitab Blackjack mängu graafilise kasutajaliidese.
 * Esmalt mainmenu ja seaded, seejärel korduv turnipõhine mäng.
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
        //kuvaSeadistusVaade();
        kuvaMainMenu();
    }

    private Stage primaryStage;

    // kuvab ja uuendab esialgsete seadete vaate
    private void uuendaMangijateSeaded(VBox box, int count) {
        box.getChildren().clear();
        for (int i = 1; i <= count; i++) {
            Label lbl = new Label("Mängija " + i + " nimi:");
            TextField nameField = new TextField("Mängija" + i);
            CheckBox botCheck = new CheckBox("robot?");
            box.getChildren().add(new HBox(10, lbl, nameField, botCheck));
        }
    }

    private void kuvaMainMenu(){
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            kuvaSeadistusVaade();
        });

        Button helpButton = new Button("Help");
        helpButton.setOnAction(e -> {
            kuvaHelp();
        });

        Button infoButton = new Button("Info");
        infoButton.setOnAction(e -> {
            kuvaInfo();
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            Platform.exit();
        });
        VBox root = new VBox(15, startButton, helpButton, infoButton, exitButton);
        root.setPadding(new Insets(20));
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    private void kuvaInfo(){
        //TODO lisada siia tekst mis kirjeldab kuidas mängu mängida
        Label infoLabel = new Label("Info selle kohta kes mängu tegid");
        Button backButton = new Button("Main menu");
        backButton.setOnAction(e -> {
            kuvaMainMenu();
        });
        VBox root = new VBox(15, infoLabel, backButton);
        root.setPadding(new Insets(20));
        primaryStage.setTitle("Blackjack - info");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    private void kuvaHelp(){
        //TODO lisada siia tekst mis kirjeldab kuidas mängu mängida
        Label tekstLabel = new Label("Lisa siia tekst kuidas mängida.");
        Button backButton = new Button("Main menu");
        backButton.setOnAction(e -> {
            kuvaMainMenu();
        });
        VBox root = new VBox(15, tekstLabel, backButton);
        root.setPadding(new Insets(20));
        primaryStage.setTitle("Blackjack - help");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    private void kuvaSeadistusVaade() {
        Label decksLabel = new Label("Pakkide arv:");
        decksLabel.setStyle(CssUtil.getCss("seade-silt"));
        Spinner<Integer> decksSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1)
        );
        HBox decksBox = new HBox(10, decksLabel, decksSpinner);
        decksBox.setAlignment(Pos.CENTER_LEFT);

        Label playersLabel = new Label("Mängijate arv:");
        playersLabel.setStyle(CssUtil.getCss("seade-silt"));
        Spinner<Integer> playersSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 2)
        );
        HBox playersBox = new HBox(10, playersLabel, playersSpinner);
        playersBox.setAlignment(Pos.CENTER_LEFT);

        VBox playersConfigBox = new VBox(10);
        uuendaMangijateSeaded(playersConfigBox, playersSpinner.getValue());
        playersSpinner.valueProperty().addListener((obs, oldV, newV) ->
                uuendaMangijateSeaded(playersConfigBox, newV)
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
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    // loogelised UI elemendid
    private BorderPane gameRoot;
    private VBox gameControlsBox, endControlsBox;
    private Button hitBtn, standBtn;
    private Button confBtn, restartBtn;
    private Label tulemused;

    //see loob ainult objektid mängu alustamiseks
    //mänguloogika on liigutatud meetodisse
    //runGame(), selleks, et restartides ei oleks vaja luua kõik objektid uuesti.
    private void initGame(Stage stage, int deckCount) {
        try {
            deck = new Kaardipakk(deckCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dealerView = new G_käsi(new ArrayList<>(), false);
        playerHands = new ArrayList<>();
        currentPlayer = 0;

        // Ülakeha (diiler)
        Label dealerLabel = new Label("Diiler");
        dealerLabel.setStyle(CssUtil.getCss("nime-silt"));
        GridPane dealerPane = (GridPane)dealerView.getNode();
        dealerPane.setStyle(CssUtil.getCss("card-hand")); dealerPane.setScaleX(0.9); dealerPane.setScaleY(0.9);
        VBox topBox = new VBox(5, dealerLabel, dealerPane);
        topBox.setStyle(CssUtil.getCss("vbox"));
        topBox.setAlignment(Pos.CENTER);

        // Allosas mängijad
        HBox playersBox = new HBox(20);
        playersBox.setAlignment(Pos.CENTER);
        for (PlayerConfig pc : configs) {
            G_käsi hand = new G_käsi(new ArrayList<>(), true);
            playerHands.add(hand);
            GridPane pPane = (GridPane)hand.getNode();
            pPane.setStyle(CssUtil.getCss("card-hand"));
            pPane.setScaleX(0.6); pPane.setScaleY(0.6);
            Label nameLbl = new Label(pc.name);
            nameLbl.setStyle(CssUtil.getCss("nime-silt"));
            VBox box = new VBox(5, nameLbl, pPane);
            box.setAlignment(Pos.CENTER);
            playersBox.getChildren().add(box);
        }

        //hit button
        hitBtn = new Button("Hit"); hitBtn.setMinWidth(150);
        hitBtn.setStyle(CssUtil.getCss("nuppud"));
        hitBtn.setMaxWidth(Double.MAX_VALUE); hitBtn.setMaxWidth(Double.MAX_VALUE);
        hitBtn.setOnAction(e -> onHit());
        //stand button
        standBtn = new Button("Stand"); standBtn.setMinWidth(150);
        standBtn.setMaxWidth(Double.MAX_VALUE); standBtn.setMaxWidth(Double.MAX_VALUE);
        standBtn.setStyle(CssUtil.getCss("nuppud"));
        standBtn.setOnAction(e -> onStand());
        //restart button
        restartBtn = new Button("Restart samade sättetega");restartBtn.setMinWidth(230);
        restartBtn.setStyle(CssUtil.getCss("nuppud"));
        restartBtn.setMaxWidth(Double.MAX_VALUE); restartBtn.setMaxWidth(Double.MAX_VALUE);
        restartBtn.setOnAction(e -> {restartGame();});
        //config button
        confBtn = new Button("Muuda mängu sätteid");
        confBtn.setStyle(CssUtil.getCss("nuppud"));confBtn.setMinWidth(230);
        confBtn.setMaxWidth(Double.MAX_VALUE); confBtn.setMaxWidth(Double.MAX_VALUE);
        confBtn.setOnAction(e -> {kuvaSeadistusVaade();});
        //controlsbox mängu controls jaoks
        gameControlsBox = new VBox(10,hitBtn,standBtn);
        gameControlsBox.setAlignment(Pos.TOP_CENTER);
        gameControlsBox.setPadding(new Insets(10));
        gameControlsBox.setPrefWidth(120);
        //controlsbox peale mängu nuppude jaoks
        endControlsBox = new VBox(10,restartBtn,confBtn);
        endControlsBox.setAlignment(Pos.TOP_CENTER);
        endControlsBox.setPadding(new Insets(10));
        endControlsBox.setPrefWidth(120);
        endControlsBox.setDisable(true);
        endControlsBox.setVisible(false);

        // Layout
        gameRoot = new BorderPane();
        gameRoot.setTop(topBox);
        gameRoot.setBottom(playersBox);
        gameRoot.setRight(gameControlsBox);
        //gameRoot.setBackground(new Background(new BackgroundFill(
        //        Color.web("#5c442d"), CornerRadii.EMPTY, Insets.EMPTY)));
        gameRoot.setStyle(CssUtil.getCss("root"));
        stage.setTitle("Blackjack");
        stage.setScene(new Scene(gameRoot, 800, 600));
        stage.show();

        runGame();

        updateControls();
    }

    private void runGame(){
        try {
            deck.reset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dealerView.clear();
        dealerView.lisaKaart(deck.jaga());
        dealerView.lisaKaart(deck.jaga());
        for(G_käsi gk : playerHands){
            gk.clear();
            gk.lisaKaart(deck.jaga());
            gk.lisaKaart(deck.jaga());
        }
    }

    private void restartGame(){
        gameControlsBox.setDisable(false);
        gameControlsBox.setVisible(true);
        endControlsBox.setVisible(false);
        gameRoot.setRight(gameControlsBox);
        gameRoot.setCenter(null);
        gameRoot.layout();
        currentPlayer = 0;
        runGame();
    }

    //Käib läbi Hit toimingu praegusel mängijal.
    private void onHit() {
        G_käsi hand = playerHands.get(currentPlayer);
        hand.lisaKaart(deck.jaga());
        if (arvutaSumma(hand.getKaardid()) >= 21) {
            onStand();
        }
    }

    //Käib läbi Stand toimingu praegusel mängijal ja liigub järgmise juurde või diileri juurde.
    private void onStand() {
        currentPlayer++;
        if (currentPlayer < playerHands.size()) {
            updateControls();
        } else {
            gameControlsBox.setVisible(false);
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

    //Uuendab nupud ja sildid järgmiseks mängijaks.
    private void updateControls() {
        PlayerConfig pc = configs.get(currentPlayer);
        hitBtn.setText("Hit: " + pc.name);
        standBtn.setText("Stand: " + pc.name);
        gameControlsBox.setDisable(pc.isBot);
        // robotid saavad automaatse käigu ehk kohe Stand
        if (pc.isBot) {
            onStand();
        }
    }

    //Arvutab käe väärtuse Blackjack reeglite järgi.
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

    //Abiklass mängija nime ja roboti oleku hoidmiseks.
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

        if(tulemused == null){
            tulemused = new Label(
                    String.format("Mäng läbi! Võitja: %s (%d)", winner, best)
            );
        }else{
            tulemused.setVisible(true);
            tulemused.setText(
                    String.format("Mäng läbi! Võitja: %s (%d)", winner, best)
            );
        }
        endControlsBox.setDisable(false);
        endControlsBox.setVisible(true);
        tulemused.setStyle(CssUtil.getCss("result"));
        BorderPane.setAlignment(tulemused, Pos.CENTER);
        gameRoot.setCenter(tulemused);
        gameRoot.setRight(endControlsBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}