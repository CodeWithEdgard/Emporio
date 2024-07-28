package com.loja.controllers;

import com.loja.models.Product;
import com.loja.utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField supplierField;
    @FXML
    private TextField expirationDateField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField totalValueField;
    @FXML
    private TextField soldQuantityField;
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
    private TableColumn<Product, String> expirationDateColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Double> unitPriceColumn;
    @FXML
    private TableColumn<Product, Double> totalValueColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        supplierColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSupplier()));
        expirationDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExpirationDate().toString()));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        unitPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        totalValueColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalValue()).asObject());
    }

    @FXML
    private void handleSellProduct() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                int soldQuantity = Integer.parseInt(soldQuantityField.getText());
                int currentQuantity = selectedProduct.getQuantity();

                if (soldQuantity > 0 && soldQuantity <= currentQuantity) {
                    selectedProduct.setQuantity(currentQuantity - soldQuantity);
                    productTableView.refresh();
                    updateProductInDatabase(selectedProduct);
                } else {
                    showAlert("Quantidade Inválida", "A quantidade vendida deve ser maior que 0 e menor ou igual ao estoque atual.");
                }
            } catch (NumberFormatException e) {
                showAlert("Entrada Inválida", "Por favor, insira um número válido para a quantidade vendida.");
            }
        } else {
            showAlert("Nenhum Produto Selecionado", "Por favor, selecione um produto na tabela.");
        }
    }

    private void updateProductInDatabase(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, supplier = ?, expiration_date = ?, quantity = ?, unit_price = ?, total_value = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setInt(5, product.getQuantity());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getTotalValue());
            pstmt.setInt(8, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Atualizar", "Erro ao atualizar o produto no banco de dados: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
                double unitPrice = rs.getDouble("unit_price");

                products.add(new Product(id, name, category, supplier, expirationDate, quantity, unitPrice));
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

        try {
            if (!expirationDateField.getText().isEmpty()) {
                expirationDate = LocalDate.parse(expirationDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data de vencimento inválido. Use o formato dd/MM/yyyy.");
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida.");
            return;
        }

        double unitPrice = 0;
        try {
            unitPrice = Double.parseDouble(unitPriceField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Preço unitário inválido.");
            return;
        }

        if (expirationDate == null) {
            System.out.println("Data de vencimento é obrigatória.");
            return;
        }

        Product product = new Product(0, name, category, supplier, expirationDate, quantity, unitPrice);
        addProduct(product);

        loadProducts();
        clearFields();
    }

    private void addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, supplier, expiration_date, quantity, unit_price, total_value) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setInt(5, product.getQuantity());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getTotalValue());
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
        quantityField.clear();
        unitPriceField.clear();
        totalValueField.clear();
        soldQuantityField.clear();
    }

    @FXML
    private void handleEditProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Preencher os campos com os dados do produto selecionado
            nameField.setText(selectedProduct.getName());
            categoryField.setText(selectedProduct.getCategory());
            supplierField.setText(selectedProduct.getSupplier());
            expirationDateField.setText(selectedProduct.getExpirationDate() != null ? selectedProduct.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            quantityField.setText(String.valueOf(selectedProduct.getQuantity()));
            unitPriceField.setText(String.valueOf(selectedProduct.getUnitPrice()));

            // Bloquear campos não editáveis
            totalValueField.setText(String.valueOf(selectedProduct.getTotalValue()));
            totalValueField.setEditable(false);
            soldQuantityField.clear();
        } else {
            showAlert(Alert.AlertType.WARNING, "Nenhum produto selecionado", "Por favor, selecione um produto para editar.");
        }
    }

    @FXML
    private void handleUpdateProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            String name = nameField.getText();
            String category = categoryField.getText();
            String supplier = supplierField.getText();
            LocalDate expirationDate = null;

            try {
                if (!expirationDateField.getText().isEmpty()) {
                    expirationDate = LocalDate.parse(expirationDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            } catch (DateTimeParseException e) {
                showAlert(Alert.AlertType.ERROR, "Formato de Data Inválido", "Data de vencimento deve estar no formato dd/MM/yyyy.");
                return;
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Quantidade Inválida", "Por favor, insira uma quantidade válida.");
                return;
            }

            double unitPrice = 0;
            try {
                unitPrice = Double.parseDouble(unitPriceField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Preço Inválido", "Por favor, insira um preço válido.");
                return;
            }

            if (expirationDate == null) {
                showAlert(Alert.AlertType.ERROR, "Data de Vencimento Necessária", "Data de vencimento é obrigatória.");
                return;
            }

            double totalValue = quantity * unitPrice;  // Calculo Valor Total

            Product updatedProduct = new Product(selectedProduct.getId(), name, category, supplier, expirationDate, quantity, unitPrice);


            updateProductInDatabase(updatedProduct);  // Atualização do banco de dados
            loadProducts();  // Recarregue produtos
            clearFields();  // Limpe os campos
        } else {
            showAlert(Alert.AlertType.WARNING, "Nenhum produto selecionado", "Por favor, selecione um produto para editar.");
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
        String sql = "UPDATE products SET name = ?, category = ?, supplier = ?, expiration_date = ?, quantity = ?, unit_price = ?, total_value = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getSupplier());
            pstmt.setDate(4, Date.valueOf(product.getExpirationDate()));
            pstmt.setInt(5, product.getQuantity());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getTotalValue());
            pstmt.setInt(8, product.getId());
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
