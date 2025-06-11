package finalLab.Controller;

import finalLab.Main;
import finalLab.Model.Movie;
import finalLab.Model.SnackItem;
import finalLab.Model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class TicketController {

    private BookingController bookingController;
    private Main mainApp;
    private User currentUser;
    private Stage primaryStage;
    private final finalLab.Service.TicketService ticketService = new finalLab.Service.TicketService();

    public TicketController(Main mainApp, User currentUser) {
        if (mainApp == null) throw new IllegalArgumentException("mainApp tidak boleh null");
        if (currentUser == null) throw new IllegalArgumentException("currentUser tidak boleh null");
        this.mainApp = mainApp;
        this.currentUser = currentUser;
        this.primaryStage = mainApp.getPrimaryStage();
    }

    public void setBookingController(BookingController bookingController) {
        this.bookingController = bookingController;
    }

    // Generate ticket dengan pengecekan seat
    public String generateTicket(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) throws IOException {
        String ticketId = "TKT" + System.currentTimeMillis();

        // Cek seat available sebelum tulis tiket (PENTING)
        if (!ticketService.isSeatAvailable(movie, date, schedule, seat)) {
            mainApp.showAlert("Error", "Seat sudah dibooking!");
            return null;
        }

        File ticketsDir = new File("tickets");
        if (!ticketsDir.exists()) ticketsDir.mkdirs();

        String filename = "tickets/tickets.txt";

        double snacksTotal = 0;
        StringBuilder ticketBuilder = new StringBuilder();

        ticketBuilder.append("|========== MOVIE TICKET ==========|\n");
        ticketBuilder.append("Ticket ID      : ").append(ticketId).append("\n");
        ticketBuilder.append("Date           : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        ticketBuilder.append("Customer       : ").append(user.getUsername()).append("\n");
        ticketBuilder.append("Movie          : ").append(movie.getTitle()).append("\n");
        ticketBuilder.append("Date           : ").append(date).append("\n");
        ticketBuilder.append("Schedule       : ").append(schedule).append("\n");
        ticketBuilder.append("Seat           : ").append(seat).append("\n");
        ticketBuilder.append("Snacks         :\n");

        if (snacks != null && !snacks.isEmpty()) {
            for (SnackItem item : snacks) {
                double itemTotal = item.getSnack().getPrice() * item.getQuantity();
                snacksTotal += itemTotal;
                ticketBuilder.append("  - ").append(item.getSnack().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" (Rp ").append(String.format("%,.0f", itemTotal)).append(")\n");
            }
        } else {
            ticketBuilder.append("  - No snacks\n");
        }

        double ticketPrice = 50000;
        double total = ticketPrice + snacksTotal;

        ticketBuilder.append("Total Amount   : Rp ").append(String.format("%,.0f", total)).append("\n");
        ticketBuilder.append("Status         : PAID\n");
        ticketBuilder.append("|==================================|\n\n");

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.print(ticketBuilder.toString());
        }

        // Simpan info ticket ke bookingController jika mainApp ada
        if (mainApp != null && bookingController != null) {
            bookingController.setCurrentTicketId(ticketId);
            bookingController.setCurrentTicketText(ticketBuilder.toString());
        }

        return ticketId;
    }

    // Tampilkan ticket di GUI
    public void showTicket() {
        if (mainApp == null || bookingController == null || primaryStage == null) return;

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        Label headerLabel = new Label("🎬 CINEMA TICKET");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox ticketContainer = new VBox(15);
        ticketContainer.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 15;");
        ticketContainer.setMaxWidth(450);

        String ticketId = bookingController.getCurrentTicketId();
        if (ticketId == null || ticketId.isEmpty()) {
            ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        Label ticketIdLabel = new Label("Ticket ID: " + ticketId);
        ticketIdLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-family: 'Courier New';");

        User user = bookingController.getCurrentUser();
        Label customerLabel = new Label("👤 Customer: " + (user != null ? user.getFullName() : "Guest"));
        customerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-font-weight: bold;");

        Movie movie = bookingController.getSelectedMovie();
        Label movieLabel = new Label("🎭 " + (movie != null ? movie.getTitle() : "Unknown Movie"));
        movieLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox dateTimeBox = new HBox(20);
        dateTimeBox.setAlignment(Pos.CENTER);

        String selectedDate = bookingController.getSelectedDate();
        if (selectedDate == null) {
            selectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        String selectedTime = bookingController.getSelectedTime();
        if (selectedTime == null) {
            selectedTime = "Unknown Time";
        }

        Label dateLabel = new Label("📅 " + selectedDate);
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        Label timeLabel = new Label("🕐 " + selectedTime);
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);

        List<String> selectedSeats = bookingController.getSelectedSeats();
        int seatCount = (selectedSeats != null) ? selectedSeats.size() : 0;
        double ticketPrice = seatCount * 50000;
        Label seatsLabel = new Label(
                "💺 Seats: " + (selectedSeats != null && !selectedSeats.isEmpty() ? String.join(", ", selectedSeats) : "No seats selected"));
        seatsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        VBox snacksBox = new VBox(5);
        List<SnackItem> selectedSnacks = bookingController.getSelectedSnacks();
        double snacksTotal = 0;

        if (selectedSnacks != null && !selectedSnacks.isEmpty()) {
            Label snacksHeaderLabel = new Label("🍿 Snacks & Drinks:");
            snacksHeaderLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
            snacksBox.getChildren().add(snacksHeaderLabel);

            for (SnackItem item : selectedSnacks) {
                double itemTotal = item.getSnack().getPrice() * item.getQuantity();
                snacksTotal += itemTotal;
                Label snackLabel = new Label("   " + item.getQuantity() + "x " + item.getSnack().getName()
                        + " - Rp " + String.format("%,.0f", itemTotal));
                snackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                snacksBox.getChildren().add(snackLabel);
            }
        } else {
            Label noSnacksLabel = new Label("🍿 No snacks selected");
            noSnacksLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            snacksBox.getChildren().add(noSnacksLabel);
        }

        VBox priceBox = new VBox(5);
        Label ticketPriceLabel = new Label("Ticket Price: Rp " + String.format("%,.0f", ticketPrice));
        ticketPriceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        Label snacksPriceLabel = new Label("Snacks Total: Rp " + String.format("%,.0f", snacksTotal));
        snacksPriceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        double totalAmount = ticketPrice + snacksTotal;
        Label totalLabel = new Label("💰 Total Amount: Rp " + String.format("%,.0f", totalAmount));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        priceBox.getChildren().addAll(ticketPriceLabel, snacksPriceLabel, totalLabel);

        Label paymentLabel = new Label(
                "💳 Payment: " + (bookingController.getPaymentMethod() != null ? bookingController.getPaymentMethod() : "Unknown"));
        paymentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label timestampLabel = new Label(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        ticketContainer.getChildren().addAll(ticketIdLabel, customerLabel, movieLabel, dateTimeBox,
                seatsLabel, snacksBox, new Label("----------------------------------------"), priceBox, paymentLabel,
                timestampLabel);

        // Tombol aksi
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("💾 SAVE TICKET");
        Button printButton = new Button("🖨️ PRINT");
        Button backButton = new Button("← BACK");
        Button newBookingButton = new Button("🎬 NEW BOOKING");

        saveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        printButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        newBookingButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        saveButton.setOnAction(e -> saveCurrentTicket());
        printButton.setOnAction(e -> System.out.println("PRINTING... (fitur belum diimplementasikan)"));
        backButton.setOnAction(e -> {
            if (bookingController != null) {
                bookingController.showSeatSelection();
            } else if (mainApp != null) {
                mainApp.showMainMenu();
            } 
        });
        newBookingButton.setOnAction(e -> {
            if (mainApp != null) mainApp.showMainMenu();
        });

        buttonBox.getChildren().addAll(saveButton, printButton, backButton, newBookingButton);

        root.getChildren().addAll(headerLabel, ticketContainer, buttonBox);

        Scene scene = new Scene(root, 520, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Tampilkan semua tiket user di console
    public void showUserTickets() {
        if (currentUser == null) {
            mainApp.showAlert("Info", "User belum login.");
            return;
        }

        String filename = "tickets/tickets.txt";
        File file = new File(filename);
        if (!file.exists()) {
            mainApp.showAlert("Info", "Belum ada tiket yang dibeli.");
            return;
        }

        StringBuilder ticketsText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ticketsText.append(line).append("\n");
            }
        } catch (IOException e) {
            mainApp.showAlert("Error", "Gagal membaca file tiket: " + e.getMessage());
            return;
        }

        if (ticketsText.length() == 0) {
            mainApp.showAlert("Info", "Belum ada tiket yang dibeli.");
            return;
        }

        // Tampilkan semua tiket di TextArea (read-only)
        TextArea ticketsArea = new TextArea(ticketsText.toString());
        ticketsArea.setEditable(false);
        ticketsArea.setWrapText(true);
        ticketsArea.setPrefHeight(500);

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🎟 My Tickets");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> mainApp.showMainMenu());

        root.getChildren().addAll(titleLabel, ticketsArea, backBtn);

        Scene scene = new Scene(root, 520, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Simpan ticket saat ini ke file
    public void saveCurrentTicket() {
        if (mainApp == null || bookingController == null || bookingController.getCurrentUser() == null) {
            System.err.println("Cannot save ticket: mainApp, bookingController, or user is null");
            return;
        }

        User user = bookingController.getCurrentUser();
        String ticketText = bookingController.getCurrentTicketText();

        if (ticketText == null || ticketText.isEmpty()) {
            System.err.println("No ticket text to save.");
            return;
        }

        File ticketsDir = new File("tickets/tickets.txt");
        if (!ticketsDir.exists()) ticketsDir.mkdirs();

        String filename = "tickets/tickets.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(ticketText);
            System.out.println("Ticket saved successfully to " + filename);
            // Opsional: beri notifikasi di GUI jika ada
        } catch (IOException e) {
            System.err.println("Failed to save ticket: " + e.getMessage());
        }
    }
}