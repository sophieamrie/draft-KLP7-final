package finalLab.Controller;

import finalLab.Main;
import finalLab.Model.User;
import finalLab.Model.Movie;
import finalLab.Model.SnackItem;
import finalLab.Payment.IPayment;
import finalLab.Service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentController {
    private Main mainApp;
    private BookingController bookingController;
    private UserService userService;
    private IPayment paymentMethod;
    private Stage primaryStage;
    private User currentUser;
    private SnackController snackController;

    public PaymentController(Main mainApp, User currentUser, BookingController bookingController) {
        this.mainApp = mainApp;
        this.currentUser = currentUser;
        this.bookingController = bookingController;
        this.primaryStage = mainApp.getPrimaryStage();
        this.userService = new UserService();
    }

    // Setter untuk SnackController (agar bisa di-inject dari Main)
    public void setSnackController(SnackController snackController) {
        this.snackController = snackController;
    }

    public void setPaymentMethod(IPayment payment) {
        this.paymentMethod = payment;
    }

    public void processPayment(double totalAmount) {
        if (currentUser.getBalance() >= totalAmount) {
            try {
                double newBalance = currentUser.getBalance() - totalAmount;
                currentUser.setBalance(newBalance);

                userService.updateUserBalance(currentUser);
                String ticketId = generateAndSaveTicket(currentUser, totalAmount);
                logTicketPurchaseTransaction(currentUser, totalAmount, ticketId);
                mainApp.showAlert("Payment Successful",
                        "Payment successful! Your ticket has been booked.\n" +
                                "Ticket ID: " + ticketId + "\n" +
                                "Remaining balance: Rp " + String.format("%,.0f", newBalance));

                mainApp.showMainMenu();

            } catch (Exception e) {
                mainApp.showAlert("Error", "Payment failed: " + e.getMessage());
            }
        } else {
            mainApp.showAlert("Insufficient Balance",
                    "Your balance is not enough.\n" +
                            "Required: Rp " + String.format("%,.0f", totalAmount) + "\n" +
                            "Available: Rp " + String.format("%,.0f", currentUser.getBalance()) + "\n" +
                            "Please top up your account.");
        }
    }

    public boolean processPayment(User user, double amount, IPayment payment) {
        try {
            boolean paid = payment.pay(user, amount);
            if (!paid) {
                mainApp.showAlert("Error", "Payment failed! Your balance might be insufficient or an error occurred!");
                return false;
            }

            userService.updateUserBalance(user);

            String ticketId = generateAndSaveTicket(user, amount);

            mainApp.showAlert("Success", "Payment successful!\nTicket ID: " + ticketId + "\nThank you for your purchase!");
            mainApp.showMainMenu();
            return true;
        } catch (Exception e) {
            mainApp.showAlert("Error", "Payment failed: " + e.getMessage());
            return false;
        }
    }

    public void processTopUp(double amount, String paymentMethodName) {
        try {
            double oldBalance = currentUser.getBalance();
            double newBalance = oldBalance + amount;

            currentUser.setBalance(newBalance);
            userService.updateUserBalance(currentUser);

            logTopUpTransaction(amount, paymentMethodName, oldBalance, newBalance);

            mainApp.showAlert("Top Up Successful",
                    "Top up successful!\n" +
                            "Amount: Rp " + String.format("%,.0f", amount) + "\n" +
                            "Payment Method: " + paymentMethodName + "\n" +
                            "New Balance: Rp " + String.format("%,.0f", newBalance));

            mainApp.showMainMenu();
        } catch (Exception e) {
            mainApp.showAlert("Top Up Failed", "Top-up failed: " + e.getMessage());
        }
    }

    private String generateAndSaveTicket(User user, double totalAmount) {
        String ticketId = "TKT" + System.currentTimeMillis();

        try {
            String filename = "tickets.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println("|========== MOVIE TICKET ==========|");
                writer.println("Ticket ID      : " + ticketId);
                writer.println("Date           : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("Customer       : " + user.getFullName());
                writer.println("Movie          : " + (bookingController.getSelectedMovie() != null ? bookingController.getSelectedMovie().getTitle() : "N/A"));
                writer.println("Date           : " + (bookingController.getSelectedDate() != null ? bookingController.getSelectedDate() : "N/A"));
                writer.println("Schedule       : " + (bookingController.getSelectedSchedule() != null ? bookingController.getSelectedSchedule() : "N/A"));
                writer.println("Seat           : " + (bookingController.getSelectedSeat() != null ? bookingController.getSelectedSeat() : "N/A"));
                writer.println("Snacks         :");
                List<SnackItem> snacks = bookingController.getSelectedSnacks();
                if (snacks != null && !snacks.isEmpty()) {
                    for (SnackItem item : snacks) {
                        writer.println("  - " + item.getQuantity() + "x " + item.getSnack().getName() +
                                " (Rp " + String.format("%,.0f", item.getSnack().getPrice() * item.getQuantity()) + ")");
                    }
                } else {
                    writer.println("  - No snacks");
                }

                writer.println("Total Amount   : Rp " + String.format("%,.0f", totalAmount));
                writer.println("Status         : PAID");
                writer.println("|==================================|");
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Failed to save ticket: " + e.getMessage());
        }
        return ticketId;
    }

    private void logTicketPurchaseTransaction(User user, double amount, String ticketId) {
        try {
            String filename = "transaction-" + user.getUsername() + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println("|======= TICKET PURCHASE TRANSACTION =======|");
                writer.println("Date           : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("User           : " + user.getFullName());
                writer.println("Ticket ID      : " + ticketId);
                writer.println("Amount         : Rp " + String.format("%,.0f", amount));
                writer.println("New Balance    : Rp " + String.format("%,.0f", user.getBalance()));
                writer.println("Status         : SUCCESS");
                writer.println("|==========================================|");
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Failed to log transaction: " + e.getMessage());
        }
    }

    private void logTopUpTransaction(double amount, String paymentMethodName, double oldBalance, double newBalance) {
        try {
            String filename = "topup-" + currentUser.getUsername() + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println("|======== TOP-UP TRANSACTION ========|");
                writer.println("Date           : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("User           : " + currentUser.getFullName());
                writer.println("Amount         : Rp " + String.format("%,.0f", amount));
                writer.println("Payment Method : " + paymentMethodName);
                writer.println("Balance Before : Rp " + String.format("%,.0f", oldBalance));
                writer.println("Balance After  : Rp " + String.format("%,.0f", newBalance));
                writer.println("Status         : SUCCESS");
                writer.println("|====================================|");
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Failed to log top-up transaction: " + e.getMessage());
        }
    }

    public void showTopUpScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("💳 TOP UP BALANCE");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label currentBalanceLabel = new Label("Current Balance: Rp " + String.format("%,.0f", currentUser.getBalance()));
        currentBalanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f1c40f;");

        VBox topupBox = new VBox(20);
        topupBox.setAlignment(Pos.CENTER);
        topupBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label instructionLabel = new Label("Select top-up amount or enter custom amount:");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Predefined amounts
        GridPane amountGrid = new GridPane();
        amountGrid.setAlignment(Pos.CENTER);
        amountGrid.setHgap(10);
        amountGrid.setVgap(10);

        double[] predefinedAmounts = { 50000, 100000, 200000, 500000 };
        ToggleGroup amountGroup = new ToggleGroup();

        for (int i = 0; i < predefinedAmounts.length; i++) {
            double amount = predefinedAmounts[i];
            RadioButton radioBtn = new RadioButton("Rp " + String.format("%,.0f", amount));
            radioBtn.setToggleGroup(amountGroup);
            radioBtn.setUserData(amount);
            radioBtn.setStyle("-fx-font-size: 12px;");
            amountGrid.add(radioBtn, i % 2, i / 2);
        }

        // Custom amount option
        RadioButton customRadio = new RadioButton("Custom Amount:");
        customRadio.setToggleGroup(amountGroup);
        customRadio.setUserData(-1.0); // Special value for custom
        customRadio.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        TextField customAmountField = new TextField();
        customAmountField.setPromptText("Enter amount");
        customAmountField.setDisable(true);
        customAmountField.setPrefWidth(150);

        // Enable/disable custom field based on selection
        amountGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                double selectedAmount = (Double) newToggle.getUserData();
                customAmountField.setDisable(selectedAmount != -1.0);
                if (selectedAmount != -1.0) {
                    customAmountField.clear();
                }
            }
        });

        HBox customBox = new HBox(10);
        customBox.setAlignment(Pos.CENTER);
        customBox.getChildren().addAll(customRadio, customAmountField);

        // Payment method selection
        Label paymentLabel = new Label("Select Payment Method:");
        paymentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup paymentGroup = new ToggleGroup();

        RadioButton creditCardRadio = new RadioButton("💳 Credit Card");
        creditCardRadio.setToggleGroup(paymentGroup);
        creditCardRadio.setSelected(true);

        RadioButton debitCardRadio = new RadioButton("💳 Debit Card");
        debitCardRadio.setToggleGroup(paymentGroup);

        RadioButton bankTransferRadio = new RadioButton("🏦 Bank Transfer");
        bankTransferRadio.setToggleGroup(paymentGroup);

        RadioButton eWalletRadio = new RadioButton("📱 E-Wallet");
        eWalletRadio.setToggleGroup(paymentGroup);

        VBox paymentOptions = new VBox(10);
        paymentOptions.getChildren().addAll(creditCardRadio, debitCardRadio, bankTransferRadio, eWalletRadio);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");

        Button topupBtn = new Button("💳 TOP UP NOW");
        topupBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        backBtn.setOnAction(e -> mainApp.showMainMenu());

        topupBtn.setOnAction(e -> {
            RadioButton selectedAmount = (RadioButton) amountGroup.getSelectedToggle();
            RadioButton selectedPayment = (RadioButton) paymentGroup.getSelectedToggle();

            if (selectedAmount == null) {
                mainApp.showAlert("Error", "Please select an amount to top up");
                return;
            }

            if (selectedPayment == null) {
                mainApp.showAlert("Error", "Please select a payment method");
                return;
            }

            double amount;
            double selectedAmountValue = (Double) selectedAmount.getUserData();

            if (selectedAmountValue == -1.0) {
                // Custom amount
                String customAmountText = customAmountField.getText().trim();
                if (customAmountText.isEmpty()) {
                    mainApp.showAlert("Error", "Please enter a custom amount");
                    return;
                }

                try {
                    amount = Double.parseDouble(customAmountText);
                    if (amount <= 0) {
                        mainApp.showAlert("Error", "Amount must be greater than 0");
                        return;
                    }
                    if (amount > 10000000) { // Max 10 million
                        mainApp.showAlert("Error", "Maximum top-up amount is Rp 10,000,000");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    mainApp.showAlert("Error", "Please enter a valid amount");
                    return;
                }
            } else {
                amount = selectedAmountValue;
            }

            String paymentMethod = selectedPayment.getText();
            processTopUp(amount, paymentMethod);
        });

        buttonBox.getChildren().addAll(backBtn, topupBtn);

        topupBox.getChildren().addAll(
                instructionLabel,
                amountGrid,
                customBox,
                new Separator(),
                paymentLabel,
                paymentOptions);

        root.getChildren().addAll(titleLabel, currentBalanceLabel, topupBox, buttonBox);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
    }

    public void showPayment() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("💳 PAYMENT SUMMARY");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox summaryBox = new VBox(15);
        summaryBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");

        // Movie details
        // ...existing code...
// Movie details
Movie selectedMovie = bookingController.getSelectedMovie();
String selectedDate = bookingController.getSelectedDate();
String selectedSchedule = bookingController.getSelectedSchedule();
String selectedSeat = bookingController.getSelectedSeat();
List<SnackItem> selectedSnacks = bookingController.getSelectedSnacks();

Label movieInfo = new Label("🎬 " + (selectedMovie != null ? selectedMovie.getTitle() : "N/A"));
movieInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

// Format selected date for display
String displayDate = "-";
if (selectedDate != null && !selectedDate.isEmpty()) {
    try {
        LocalDate date = LocalDate.parse(selectedDate);
        displayDate = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
    } catch (Exception ex) {
        displayDate = selectedDate;
    }
}
Label dateInfo = new Label("📅 " + displayDate);
Label scheduleInfo = new Label("🕐 " + (selectedSchedule != null ? selectedSchedule : "-"));
Label seatInfo = new Label("💺 Seat: " + (selectedSeat != null ? selectedSeat : "-"));
// ...existing code...

        double ticketPrice = 50000; // Fixed ticket price
        Label ticketPriceLabel = new Label("Ticket Price: Rp " + String.format("%,.0f", ticketPrice));

        // Snacks summary
        double snacksTotal = 0;
        VBox snacksSummary = new VBox(5);
        Label snacksTitle = new Label("🍿 SNACKS:");
        snacksTitle.setStyle("-fx-font-weight: bold;");
        snacksSummary.getChildren().add(snacksTitle);

        if (selectedSnacks != null && !selectedSnacks.isEmpty()) {
            for (SnackItem item : selectedSnacks) {
                double itemTotal = item.getSnack().getPrice() * item.getQuantity();
                snacksTotal += itemTotal;
                Label itemLabel = new Label(
                        item.getQuantity() + "x " + item.getSnack().getName() + " = Rp " + String.format("%,.0f", itemTotal));
                snacksSummary.getChildren().add(itemLabel);
            }
        } else {
            snacksSummary.getChildren().add(new Label("No snacks selected"));
        }

        double totalAmount = ticketPrice + snacksTotal;
        Label totalLabel = new Label("TOTAL: Rp " + String.format("%,.0f", totalAmount));
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label balanceLabel = new Label("Your Balance: Rp " + String.format("%,.0f", currentUser.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");

        Button payBtn = new Button("💳 PAY NOW");
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        if (currentUser.getBalance() < totalAmount) {
            payBtn.setDisable(true);
            payBtn.setText("INSUFFICIENT BALANCE");
            payBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        backBtn.setOnAction(e -> {
            if (snackController != null) {
                snackController.showSnackSelection();
            } else {
                mainApp.showAlert("Error", "SnackController not set!");
            }
        });
        payBtn.setOnAction(e -> processPayment(totalAmount));

        buttonBox.getChildren().addAll(backBtn, payBtn);

        summaryBox.getChildren().addAll(
                movieInfo, scheduleInfo, seatInfo,
                new Separator(),
                ticketPriceLabel, snacksSummary,
                new Separator(),
                totalLabel, balanceLabel);

        root.getChildren().addAll(titleLabel, summaryBox, buttonBox);

        Scene scene = new Scene(root, 450, 550);
        primaryStage.setScene(scene);
    }
}