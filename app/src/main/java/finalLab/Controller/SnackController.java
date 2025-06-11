package finalLab.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import finalLab.Main;
import finalLab.Model.Snack;
import finalLab.Model.SnackItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SnackController {
    private BookingController bookingController;
    private Main mainApp;
    private List<SnackItem> selectedSnacks = new ArrayList<>();

    private PaymentController paymentController;
    public SnackController(Main mainApp) {
        this.mainApp = mainApp;
    }

    // Setter untuk BookingController (agar bisa di-inject dari Main)
    public void setBookingController(BookingController bookingController) {
        this.bookingController = bookingController;
    }

    public void setSelectedSnacks(List<SnackItem> selectedSnacks) {
        this.selectedSnacks = selectedSnacks;
    }

    public List<SnackItem> getSelectedSnacks() {
        return selectedSnacks;
    }
    public void setPaymentController(PaymentController paymentController) {
    this.paymentController = paymentController;
}

    public void showSnackSelection() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🍿 SELECT SNACKS & DRINKS");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label totalLabel = new Label("Total: Rp 0");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        ScrollPane scrollPane = new ScrollPane();
        VBox snacksContainer = new VBox(10);

        List<Snack> snacks = loadSnacks();
        selectedSnacks.clear(); // Clear previous selections

        for (Snack snack : snacks) {
            HBox snackBox = new HBox(15);
            snackBox.setAlignment(Pos.CENTER_LEFT);
            snackBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

            Label nameLabel = new Label(snack.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 150;");

            Label priceLabel = new Label("Rp " + String.format("%,.0f", snack.getPrice()));
            priceLabel.setStyle("-fx-font-size: 12px; -fx-pref-width: 100;");

            Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);
            quantitySpinner.setPrefWidth(80);

            snackBox.getChildren().addAll(nameLabel, priceLabel, quantitySpinner);
            snacksContainer.getChildren().add(snackBox);

            // Listener untuk update selectedSnacks dan totalLabel
            quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                selectedSnacks.removeIf(item -> item.getSnack().getName().equals(snack.getName()));
                if (newVal > 0) {
                    selectedSnacks.add(new SnackItem(snack, newVal));
                }
                double total = getTotalSnackPrice();
                totalLabel.setText("Total: Rp " + String.format("%,.0f", total));
            });
        }

        scrollPane.setContent(snacksContainer);
        scrollPane.setPrefHeight(300);
        scrollPane.setFitToWidth(true);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button nextBtn = new Button("PROCEED TO PAYMENT");
        nextBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        backBtn.setOnAction(e -> {
            if (mainApp != null) {
                // Kembali ke seat selection lewat BookingController
                bookingController.showSeatSelection();
            }
        });

        // ...existing code...
nextBtn.setOnAction(e -> {
    if (bookingController != null && paymentController != null) {
        bookingController.setSelectedSnacks(selectedSnacks);
        paymentController.showPayment();
    } else if (bookingController == null && mainApp != null) {
        mainApp.showAlert("Error", "BookingController belum diset.");
    } else if (paymentController == null && mainApp != null) {
        mainApp.showAlert("Error", "PaymentController belum diset.");
    }
});

        buttonBox.getChildren().addAll(backBtn, nextBtn);
        root.getChildren().addAll(titleLabel, totalLabel, scrollPane, buttonBox);

        Scene scene = new Scene(root, 500, 500);
        mainApp.getPrimaryStage().setScene(scene);
    }

    public List<Snack> loadSnacks() {
        List<Snack> snacks = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("snacks.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    snacks.add(new Snack(name, price));
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (mainApp != null) {
                mainApp.showAlert("Error", "Failed to load snacks: " + e.getMessage());
            } else {
                System.err.println("Failed to load snacks: " + e.getMessage());
            }
        }
        return snacks;
    }

    // Menghitung total harga snack yang dipilih
    public double getTotalSnackPrice() {
        return selectedSnacks.stream()
                .mapToDouble(item -> item.getSnack().getPrice() * item.getQuantity())
                .sum();
    }

    // Untuk debugging atau display selected snack
    public String getSelectedSnacksString() {
        if (selectedSnacks.isEmpty()) {
            return "No snacks selected";
        }
        StringBuilder sb = new StringBuilder();
        for (SnackItem item : selectedSnacks) {
            sb.append(item.getSnack().getName())
            .append(" x")
            .append(item.getQuantity())
            .append(" (Rp ")
            .append(String.format("%,.0f", item.getSnack().getPrice() * item.getQuantity()))
            .append(")\n");
        }
        return sb.toString();
    }
}