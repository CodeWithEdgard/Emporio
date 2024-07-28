package com.loja;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o arquivo FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/ProductView.fxml")));

        // título da janela
        primaryStage.setTitle("Emporio São José");

        // tamanho desejado da janela
        Scene scene = new Scene(root, 800, 800); // largura e altura desejadas

        // cena para o stage
        primaryStage.setScene(scene);

        // tamanho mínimo da janela
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        // Exibe a Janela
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
