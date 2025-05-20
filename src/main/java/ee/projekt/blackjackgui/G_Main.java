package ee.projekt.blackjackgui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.LinkedHashMap;
import java.util.Map;


import java.util.*;

public class G_Main extends Application {
    private Kaardipakk deck;
    private G_käsi dealerView;
    private List<G_käsi> playerHands;
    private List<PlayerConfig> configs;
    private int currentPlayer;
    private int previousDeckCount = 1;

    private Stage primaryStage;
    private BorderPane gameRoot;
    private HBox playersContainer;

    private Label turnLabel;
    private Label resultLabel;

    private Button hitBtn, standBtn;
    private Button restartBtn, confBtn, menuBtn;

    private HBox controlsBox, endControlsBox;
    private VBox bottomBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Blackjack");
        primaryStage.setResizable(true);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(900);
        kuvaMainMenu();
    }

    private void kuvaMainMenu() {
        Button startButton = new Button("Start");
        Button helpButton  = new Button("Help");
        Button infoButton  = new Button("Info");
        Button exitButton  = new Button("Exit");
        List<Button> buttons = List.of(startButton, helpButton, infoButton, exitButton);
        for (Button btn : buttons) {
            btn.setStyle(CssUtil.getCss("nupud"));
            btn.setPrefWidth(120);
        }
        startButton.setOnAction(e -> kuvaSeadistusVaade());
        helpButton .setOnAction(e -> kuvaHelp());
        infoButton .setOnAction(e -> kuvaInfo());
        exitButton .setOnAction(e -> Platform.exit());

        VBox root = new VBox(20, startButton, helpButton, infoButton, exitButton);
        root.setStyle(CssUtil.getCss("main-menu"));
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setFillWidth(false);
        primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.show();
    }

    private void kuvaHelp() {
        // Tekstiplokid (sarnaselt eelmisel korral)
        Text title1 = new Text("Blackjack mängureeglid");
        title1.setFont(Font.font("System", FontWeight.BOLD, 24));
        title1.setFill(Color.WHITE);

        Text rules = new Text(
                "1. Eesmärk on saada kaartide summa võimalikult lähedale 21-le, aga mitte ületada 21 („bust“).\n" +
                        "2. Alguses saavad kõik mängijad ja diiler kaks kaarti.\n" +
                        "   • Mängijad näevad kõiki oma kaarte.\n" +
                        "   • Diileril on üks kaart peidetud.\n" +
                        "3. Mängijad tegutsevad ükshaaval:\n" +
                        "   – „Hit“: võtta juurde kaart.\n" +
                        "   – „Stand“: lõpetada kaartide võtmine.\n" +
                        "   Kui summa ületab 21, mängija „bustib“ ja jääb mängust välja.\n" +
                        "4. Pärast mängijaid hakkab mängima diiler:\n" +
                        "   – Diiler võtab kaarte, kuni tema summa on vähemalt 17.\n" +
                        "   – Üle 21 → diiler „bustib“. Alles olevad mängijad võidavad.\n" +
                        "5. Võitja määramine:\n" +
                        "   – Kõik, kelle summa ≤21: suurima summaga võidab.\n" +
                        "   – Kui kõik bustivad, keegi ei võida."
        );
        rules.setFont(Font.font(14));
        rules.setFill(Color.WHITE);
        rules.setLineSpacing(4);

        VBox rulesBox = new VBox(6, title1, rules);
        rulesBox.setAlignment(Pos.TOP_LEFT);

        Text title2 = new Text("Nupud");
        title2.setFont(Font.font("System", FontWeight.BOLD, 24));
        title2.setFill(Color.WHITE);

        Text buttons = new Text(
                "• Hit: võta kaart juurde.\n" +
                        "• Stand: lõpetada kaartide võtmine.\n" +
                        "• Restart samade sätetega: alusta uut vooru samade mängijate ja pakkide arvuga.\n" +
                        "• Muuda seadistusi: naase seadistustele ja muuda mängijate arvu/nimesid/on-ei ole robot.\n" +
                        "• Main menu: naase peamenüüsse."
        );
        buttons.setFont(Font.font(14));
        buttons.setFill(Color.WHITE);
        buttons.setLineSpacing(4);

        VBox buttonsBox = new VBox(6, title2, buttons);
        buttonsBox.setAlignment(Pos.TOP_LEFT);

        Button backButton = new Button("Main menu");
        backButton.setStyle(CssUtil.getCss("nupud"));
        backButton.setOnAction(e -> kuvaMainMenu());

        VBox content = new VBox(20, rulesBox, buttonsBox, backButton);
        content.setStyle(CssUtil.getCss("tiled-background"));
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        // 3) Pane taust ja lisa scroll
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        content.minHeightProperty().bind(scroll.heightProperty());
        //scroll.setStyle(CssUtil.getCss("scroll")+CssUtil.getCss("help-background"));

        //lae üksteise peale, et background oleks sujuv
        StackPane kokku = new StackPane();
        kokku.setStyle(CssUtil.getCss("scroll")+CssUtil.getCss("tiled-background"));
        kokku.getChildren().addAll(content,scroll);

        primaryStage.setTitle("Blackjack – help");
        Scene helpScene = new Scene(kokku, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(helpScene);
    }

    private void kuvaInfo() {
        // Täpselt sama meetodina nagu kuvaHelp, aga hoida oma teksti
        Image bgImage = new Image(getClass().getResource("/green-cloth2.jpg").toExternalForm());
        BackgroundImage background = new BackgroundImage(
                bgImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT
        );

        Label info = new Label(
                "Mängu koostajad: Richard Markus Tins, Lauri Tõnisson"
        );
        info.setTextFill(Color.WHITE);
        info.setFont(Font.font(16));
        info.setWrapText(true);

        Button backButton = new Button("Main menu");
        backButton.setStyle(CssUtil.getCss("nupud"));
        backButton.setOnAction(e -> kuvaMainMenu());

        VBox content = new VBox(20, info, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setBackground(new Background(background));

        primaryStage.setTitle("Blackjack - info");
        primaryStage.setScene(new Scene(content, primaryStage.getWidth(), primaryStage.getHeight()));
    }

    private void kuvaSeadistusVaade() {
        Image bgImage = new Image(getClass().getResource("/green-cloth2.jpg").toExternalForm());
        BackgroundImage background = new BackgroundImage(
                bgImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT
        );
        // spinnerite algväärtused
        Spinner<Integer> decksSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, previousDeckCount)
        );
        decksSpinner.setPrefWidth(70);
        Label decksLabel = new Label("Pakkide arv:");
        decksLabel.setStyle( CssUtil.getCss("seade-silt"));
        HBox decksBox = new HBox(10, decksLabel, decksSpinner);
        decksBox.setAlignment(Pos.CENTER);

        int initialPlayers = (configs != null ? configs.size() : 2);
        Spinner<Integer> playersSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, initialPlayers)
        );
        playersSpinner.setPrefWidth(70);
        Label playersLabel = new Label("Mängijate arv:");
        playersLabel.setStyle(CssUtil.getCss("seade-silt"));
        HBox playersBox = new HBox(10, playersLabel, playersSpinner);
        playersBox.setAlignment(Pos.CENTER);

        VBox playersConfigBox = new VBox(10);
        playersConfigBox.setAlignment(Pos.CENTER);
        uuendaMangijateSeaded(playersConfigBox, playersSpinner.getValue());
        playersSpinner.valueProperty().addListener((obs, ov, nv) ->
                uuendaMangijateSeaded(playersConfigBox, nv)
        );

        Button backButton = new Button("Main menu");
        backButton.setStyle(CssUtil.getCss("nupud"));
        backButton.setOnAction(e -> kuvaMainMenu());

        Button startBtn = new Button("Alusta mängu");
        startBtn.setStyle(CssUtil.getCss("nupud"));
        startBtn.setOnAction(e -> {
            previousDeckCount = decksSpinner.getValue();
            configs = new ArrayList<>();
            for (Node node : playersConfigBox.getChildren()) {
                HBox hb = (HBox) node;
                TextField tf = (TextField) hb.getChildren().get(1);
                CheckBox cb = (CheckBox) hb.getChildren().get(2);
                configs.add(new PlayerConfig(tf.getText(), cb.isSelected()));
            }
            initGame(primaryStage, previousDeckCount);
        });

        HBox buttonBar = new HBox(20, backButton, startBtn);
        buttonBar.setAlignment(Pos.CENTER);

        VBox content = new VBox(30, decksBox, playersBox, playersConfigBox, buttonBar);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setBackground(new Background(background));

        primaryStage.setTitle("Blackjack - seadistused");
        primaryStage.setScene(new Scene(content, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.show();
    }

    private void initGame(Stage stage, int deckCount) {
        try { deck = new Kaardipakk(deckCount); } catch (Exception e) { throw new RuntimeException(e); }
        dealerView = new G_käsi(new ArrayList<>(), false,null);
        playerHands = new ArrayList<>();
        currentPlayer = 0;

        Label dealerLabel = new Label("Diiler");
        dealerLabel.setTextFill(Color.WHITE);
        dealerLabel.setStyle(CssUtil.getCss("nime-silt"));
        Node dealerNode = dealerView.getNode();
        ((Region)dealerNode).setStyle(CssUtil.getCss("card-hand"));
        dealerNode.setScaleX(0.9);
        dealerNode.setScaleY(0.9);
        HBox dealerBox = new HBox(dealerNode);
        dealerBox.setAlignment(Pos.CENTER);
        VBox topBox = new VBox(2, dealerLabel, dealerBox);
        topBox.setAlignment(Pos.CENTER);

        playersContainer = new HBox(20);
        playersContainer.setAlignment(Pos.CENTER);
        for (PlayerConfig pc : configs) {
            G_käsi hand = new G_käsi(new ArrayList<>(), true,pc);
            playerHands.add(hand);
            Node pnode = hand.getNode();
            ((Region)pnode).setStyle(CssUtil.getCss("card-hand"));
            pnode.setScaleX(0.6);
            pnode.setScaleY(0.6);
            Label nameLbl = new Label(pc.name);
            nameLbl.setTextFill(Color.WHITE);
            nameLbl.setStyle(CssUtil.getCss("nime-silt"));
            VBox box = new VBox(1);
            box.setSpacing(0);
            box.setPadding(new Insets(0));
            box.setAlignment(Pos.CENTER);
            box.getChildren().addAll(nameLbl, pnode);
            playersContainer.getChildren().add(box);
        }

        turnLabel = new Label();
        turnLabel.setTextFill(Color.WHITE);
        turnLabel.setStyle(CssUtil.getCss("turn-label"));
        hitBtn = new Button("Hit");
        hitBtn.setStyle(CssUtil.getCss("nupud"));
        hitBtn.setOnAction(e -> onHit());
        standBtn = new Button("Stand");
        standBtn.setStyle(CssUtil.getCss("nupud"));
        standBtn.setOnAction(e -> onStand());
        controlsBox = new HBox(20, hitBtn, standBtn);
        controlsBox.setAlignment(Pos.CENTER);

        bottomBox = new VBox(10, turnLabel, controlsBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        restartBtn = new Button("Restart samade sätetega");
        restartBtn.setStyle(CssUtil.getCss("nupud"));
        restartBtn.setOnAction(e -> restartGame());
        confBtn = new Button("Muuda seadistusi");
        confBtn.setStyle(CssUtil.getCss("nupud"));
        confBtn.setOnAction(e -> kuvaSeadistusVaade());
        menuBtn = new Button("Main menu");
        menuBtn.setStyle(CssUtil.getCss("nupud"));
        menuBtn.setOnAction(e -> kuvaMainMenu());
        endControlsBox = new HBox(20, restartBtn, confBtn, menuBtn);
        endControlsBox.setAlignment(Pos.CENTER);
        endControlsBox.setPadding(new Insets(10));
        endControlsBox.setVisible(false);

        gameRoot = new BorderPane();
        gameRoot.setTop(topBox);
        gameRoot.setCenter(playersContainer);
        gameRoot.setBottom(bottomBox);
        gameRoot.setStyle(CssUtil.getCss("root"));

        stage.setScene(new Scene(gameRoot, stage.getWidth(), stage.getHeight()));
        stage.show();

        runGame();
        updateControls();
    }

    private void updateControls() {
        for (int i = 0; i < playersContainer.getChildren().size(); i++) {
            Node node = playersContainer.getChildren().get(i);
            double scale = (i == currentPlayer ? 1.1 : 1.0);
            node.setScaleX(scale);
            node.setScaleY(scale);
        }
        G_käsi kelleKord = playerHands.get(currentPlayer);
        turnLabel.setText("Kelle kord: " + kelleKord.getName());
        controlsBox.setVisible(true);
        endControlsBox.setVisible(false);
        gameRoot.setBottom(bottomBox);
        if(kelleKord.isBot()){
            controlsBox.setVisible(false);
            ArrayList<G_käsi> prevPlayers = new ArrayList<>();
            for(int i = 0; i < currentPlayer;i++){
                prevPlayers.add(playerHands.get(i));
            }
            int botAction = RobotWrapper.run(kelleKord,prevPlayers,dealerView,deck);
            if(botAction == 0){
                onHit();
            }else {
                onStand();
            }
        }
    }

    private void showResult() {
        Map<String, Integer> validScores = new LinkedHashMap<>();
        for (int i = 0; i < playerHands.size(); i++) {
            int sum = arvutaSumma(playerHands.get(i).getKaardid());
            if (sum <= 21) {
                validScores.put(configs.get(i).name, sum);
            }
        }

        int dealerSum = arvutaSumma(dealerView.getKaardid());
        System.out.println("Diileri summa: " + dealerSum);
        if (dealerSum <= 21) {
            validScores.put("Diiler", dealerSum);
        }

        String resultText;
        if (validScores.isEmpty()) {
            resultText = "Mäng läbi! Keegi ei võitnud";
        } else {
            int maxScore = Collections.max(validScores.values());
            List<String> winners = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : validScores.entrySet()) {
                if (entry.getValue() == maxScore) {
                    winners.add(entry.getKey());
                }
            }

            if (winners.size() > 1) {
                resultText = String.format("Viik mängijate vahel: %s (%d)", String.join(", ", winners), maxScore);
            } else {
                resultText = String.format("Mäng läbi! Võitja: %s (%d)", winners.get(0), maxScore);
            }
        }

        // Veendume, et resultLabel on alati nähtav
        if (resultLabel == null) {
            resultLabel = new Label();
            resultLabel.setTextFill(Color.WHITE);
            resultLabel.setStyle(CssUtil.getCss("result"));
        }

        resultLabel.setText(resultText);
        resultLabel.setVisible(true);  // kindlasti nähtav

        Node dealerPane = gameRoot.getTop();
        VBox newTop = new VBox(8);
        newTop.setAlignment(Pos.CENTER);

        if (dealerPane instanceof VBox) {
            VBox existingVBox = (VBox) dealerPane;
            newTop.getChildren().addAll(existingVBox.getChildren());
        } else {
            newTop.getChildren().add(dealerPane);
        }

        if (!newTop.getChildren().contains(resultLabel)) {
            newTop.getChildren().add(resultLabel);
        }

        gameRoot.setTop(newTop);
        controlsBox.setVisible(false);
        endControlsBox.setVisible(true);
        gameRoot.setBottom(endControlsBox);
    }

    private void onHit() {
        G_käsi hand = playerHands.get(currentPlayer);
        hand.lisaKaart(deck.jaga());
        int sum = arvutaSumma(hand.getKaardid());
        if (sum > 21) {
            // leia see mängijabox
            VBox playerBox = (VBox) playersContainer.getChildren().get(currentPlayer);
            // nimi punaseks
            Label nameLbl = (Label) playerBox.getChildren().get(0);
            nameLbl.setTextFill(Color.RED);

            // lisame BUSTED vaid siis, kui seda veel pole
            boolean hasBusted = playerBox.getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .map(n -> ((Label) n).getText())
                    .anyMatch(t -> t.equals("BUSTED"));
            if (!hasBusted) {
                Label bustedLbl = new Label("BUSTED");
                bustedLbl.setTextFill(Color.WHITE);
                bustedLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");
                playerBox.getChildren().add(bustedLbl);
            }
            // liigume järgmise mängija peale
            onStand();
            return;
        }else if (sum == 21) {
            onStand();
        }else{
            updateControls();
        }
    }

    private void onStand() {
        currentPlayer++;
        if (currentPlayer < playerHands.size()) {
            updateControls();
        } else {
            // Diileri kaartide jagamine
            while (arvutaSumma(dealerView.getKaardid()) < 17) {
                dealerView.lisaKaart(deck.jaga());
            }
            dealerView.näitaPeidetud();
            showResult();
        }
    }

    private void restartGame() {
        // 1) Eemalda vanad BUSTED-sildid ja too nimed tagasi valgeks
        for (int i = 0; i < playersContainer.getChildren().size(); i++) {
            VBox playerBox = (VBox) playersContainer.getChildren().get(i);
            //nimi valgeks
            Label nameLbl = (Label) playerBox.getChildren().get(0);
            nameLbl.setTextFill(Color.WHITE);
            // eemaldame kõik BUSTED-label’id (või teised Label’id peale esimese kahe)
            playerBox.getChildren().removeIf(node ->
                    (node instanceof Label) && ((Label) node).getText().equals("BUSTED")
            );
        }

        Node top = gameRoot.getTop();
        if (top instanceof VBox) {
            VBox topBox = (VBox) top;
            if (topBox.getChildren().size() == 2 && topBox.getChildren().get(1) == resultLabel) {
                topBox.getChildren().remove(1);
            }
        }

        // Taasta põhivaade
        currentPlayer = 0;
        if (resultLabel != null) resultLabel.setVisible(false);
        gameRoot.setCenter(playersContainer);
        gameRoot.setBottom(bottomBox);

        // Lõpukontrollid peidame
        controlsBox.setVisible(true);
        endControlsBox.setVisible(false);

        // Jaga kaardid uuesti ja uuenda nupud
        runGame();
        updateControls();
    }

    private void runGame() {
        try { deck.reset(); } catch (Exception e) { throw new RuntimeException(e); }
        dealerView.clear();
        dealerView.lisaKaart(deck.jaga());
        dealerView.lisaKaart(deck.jaga(),true);
        for (G_käsi hand : playerHands) {
            hand.clear();
            hand.lisaKaart(deck.jaga());
            hand.lisaKaart(deck.jaga());
        }
    }

    private void uuendaMangijateSeaded(VBox box, int count) {
        box.getChildren().clear();
        for (int i = 1; i <= count; i++) {
            String defaultName = "Mängija" + i;
            boolean defaultBot = false;
            if (configs != null && i <= configs.size()) {
                defaultName = configs.get(i - 1).name;
                defaultBot  = configs.get(i - 1).isBot;
            }
            Label lbl = new Label("Mängija " + i + " nimi:");
            lbl.setTextFill(Color.WHITE);
            TextField tf = new TextField(defaultName);
            CheckBox cb = new CheckBox("robot?");
            cb.setTextFill(Color.WHITE);
            cb.setSelected(defaultBot);
            HBox row = new HBox(20, lbl, tf, cb);
            row.setAlignment(Pos.CENTER);
            box.getChildren().add(row);
        }
    }

    public static int arvutaSumma(List<Kaart> kaardid) {
        int sum = 0, aces = 0;
        for (Kaart k : kaardid) {
            switch (k.getTugevus()) {
                case "A": aces++; break;
                case "K": case "Q": case "J": case "10": sum += 10; break;
                default: sum += Integer.parseInt(k.getTugevus());
            }
        }
        for (int i = 0; i < aces; i++) {
            sum += (sum + 11 <= 21 ? 11 : 1);
        }
        return sum;
    }
}
