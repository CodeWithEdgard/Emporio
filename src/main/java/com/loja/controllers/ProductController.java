package com.loja.controllers;

import com.loja.models.Product;
import com.loja.utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, String> supplierColumn;
    @FXML
    private TableColumn<Product, LocalDate> expirationDateColumn;
    @FXML
    private TableColumn<Product, LocalDate> purchaseDateColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    }

    @FXML
    private void handleLoadProducts(ActionEvent event) {
        loadProducts();
    }

    private void loadProducts() {
        productTableView.setItems(getAllProducts());
    }

    public ObservableList<Product> getAllProducts() {
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

                int quantity = rs.getInt("quantity");

                products.add(new Product(id, name, category, supplier, expirationDate, quantity));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return FXCollections.observableArrayList(products);
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

        if (expirationDate == null) {
            System.out.println("Data de vencimento é obrigatória.");
            return;
        }

        Product product = new Product(0, name, category, supplier, expirationDate, quantity);
        addProduct(product);

        loadProducts();
        clearFields();
    }

    private void addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, supplier, expiration_date, purchase_date, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setInt(6, product.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar o produto: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        categoryField.clear();
        supplierField.clear();
        expirationDateField.clear();
        purchaseDateField.clear();
        quantityField.clear();
    }

    @FXML
    private void handleEditProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
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

            if (expirationDate == null) {
                System.out.println("Data de vencimento é obrigatória.");
                return;
            }

            Product updatedProduct = new Product(selectedProduct.getId(), name, category, supplier, expirationDate, quantity);
            updateProduct(updatedProduct);
            loadProducts();
            clearFields();
        } else {
            showAlert(AlertType.WARNING, "Nenhum produto selecionado", "Por favor, selecione um produto para editar.");
        }
    }

    @FXML
    private void handleDeleteProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Você tem certeza que deseja excluir este produto?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    deleteProduct(selectedProduct.getId());
                    loadProducts();
                    clearFields();
                }
            });
        } else {
            showAlert(AlertType.WARNING, "Nenhum produto selecionado", "Por favor, selecione um produto para excluir.");
        }
    }

    private void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, supplier = ?, expiration_date = ?, purchase_date = ?, quantity = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setInt(6, product.getQuantity());
            pstmt.setInt(7, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar o produto: " + e.getMessage());
        }
    }

    private void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao excluir o produto: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
