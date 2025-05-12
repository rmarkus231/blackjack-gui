module ee.projekt.blackjackgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ee.projekt.blackjackgui to javafx.fxml;
    exports ee.projekt.blackjackgui;
}