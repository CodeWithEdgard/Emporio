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

        // Define o título da janela
        primaryStage.setTitle("Emporio São José");

        // Cria a cena com o tamanho desejado
        Scene scene = new Scene(root, 800, 800); // Define a largura e altura desejadas

        // Define a cena para o stage
        primaryStage.setScene(scene);

        // Define o tamanho mínimo da janela (opcional)
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        // Mostra a janela
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
