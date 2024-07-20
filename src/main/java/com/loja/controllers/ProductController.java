package com.loja.controllers;

import com.loja.models.Product;
import com.loja.utils.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    private static final DateTimeFormatter EXPIRATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter PURCHASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField supplierField;
    @FXML
    private TextField expirationDateField;
    @FXML
    private TextField purchaseDateField;
    @FXML
    private TextField quantityField;

    // Método para adicionar produto
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, supplier, expiration_date, purchase_date, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setDate(5, product.getPurchaseDate() != null ? Date.valueOf(product.getPurchaseDate()) : null);
            pstmt.setInt(6, product.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Em vez de apenas imprimir a pilha de erros, considere mostrar uma mensagem para o usuário
            System.out.println("Erro ao adicionar o produto: " + e.getMessage());
        }
    }

    // Método para listar todos os produtos
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String supplier = rs.getString("supplier");
                LocalDate expirationDate = rs.getDate("expiration_date") != null ? rs.getDate("expiration_date").toLocalDate() : null;
                LocalDate purchaseDate = rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null;
                int quantity = rs.getInt("quantity");

                products.add(new Product(id, name, category, supplier, expirationDate, purchaseDate, quantity));
            }
        } catch (SQLException e) {
            // Mostrar uma mensagem amigável ao usuário
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return products;
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        String name = nameField.getText();
        String category = categoryField.getText();
        String supplier = supplierField.getText();

        LocalDate expirationDate = null;
        LocalDate purchaseDate = null;

        try {
            if (!expirationDateField.getText().isEmpty()) {
                expirationDate = LocalDate.parse(expirationDateField.getText(), EXPIRATION_DATE_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data de vencimento inválido. Use o formato dd/MM/yyyy.");
            return;
        }

        try {
            if (!purchaseDateField.getText().isEmpty()) {
                purchaseDate = LocalDate.parse(purchaseDateField.getText(), PURCHASE_DATE_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data de compra inválido. Use o formato dd/MM/yyyy.");
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida.");
            return;
        }

        // Verifica se expirationDate é nulo
        if (expirationDate == null) {
            System.out.println("Data de vencimento é obrigatória.");
            return;
        }

        Product product = new Product(0, name, category, supplier, expirationDate, purchaseDate, quantity);
        addProduct(product);

        // Limpar os campos depois de adicionar
        nameField.clear();
        categoryField.clear();
        supplierField.clear();
        expirationDateField.clear();
        purchaseDateField.clear();
        quantityField.clear();
    }
}
