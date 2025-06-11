package finalLab.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import finalLab.Main;
import finalLab.Model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AuthController {
    private Main mainApp;

    public AuthController(Main mainApp) {
        this.mainApp = mainApp;
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    public User login(String username, String password) {
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[0].equals(username) && parts[2].equals(password)) {
                    return new User(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                }
            }
        } catch (IOException e) {
            mainApp.showAlert("Error", "Terjadi kesalahan saat membaca file users.txt");
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String fullName, String password, double balance) {
        File userFile = new File("users.txt");
        System.out.println("User file path: " + userFile.getAbsolutePath()); // Debug lokasi file

        // Cek apakah username sudah ada
        if (userFile.exists()) {
            try (Scanner scanner = new Scanner(userFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split("\\|");
                    if (parts.length >= 1 && parts[0].equals(username)) {
                        return false;
                    }
                }
            } catch (IOException e) {
                mainApp.showAlert("Error", "Terjadi kesalahan saat membaca file users.txt");
                e.printStackTrace();
                return false;
            }
        }

        // Tambahkan user baru
        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile, true))) {
            writer.println(username + "|" + fullName + "|" + password + "|" + balance);
            return true;
        } catch (IOException e) {
            mainApp.showAlert("Error", "Terjadi kesalahan saat menulis file users.txt");
            return false;
        }
    }

    public void showLoginScreen() {
        VBox root = new VBox(20);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🎬 CINEMA BOOKING SYSTEM");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Login Form
        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setHgap(10);
        loginForm.setVgap(15);
        loginForm.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label loginTitle = new Label("LOGIN");
        loginTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(200);

        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        Button registerBtn = new Button("REGISTER");
        registerBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        loginForm.add(loginTitle, 0, 0, 2, 1);
        loginForm.add(new Label("Username:"), 0, 1);
        loginForm.add(usernameField, 1, 1);
        loginForm.add(new Label("Password:"), 0, 2);
        loginForm.add(passwordField, 1, 2);
        loginForm.add(loginBtn, 0, 3, 2, 1);
        loginForm.add(registerBtn, 0, 4, 2, 1);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                mainApp.showAlert("Error", "Please fill all fields");
                return;
            }

            User user = login(username, password);
            if (user != null) {
                mainApp.onLoginSuccess(user);
            } else {
                mainApp.showAlert("Error", "Invalid username or password");
            }
        });

        registerBtn.setOnAction(e -> showRegisterScreen());

        root.getChildren().addAll(titleLabel, loginForm);

        Scene scene = new Scene(root, 400, 500);
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().show();
    }

    public void showRegisterScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("REGISTER NEW USER");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(15);
        form.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField balanceField = new TextField();
        balanceField.setPromptText("Initial Balance");

        Button registerBtn = new Button("REGISTER");
        registerBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        Button backBtn = new Button("BACK TO LOGIN");
        backBtn.setStyle(
                "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        form.add(new Label("Full Name:"), 0, 0);
        form.add(fullNameField, 1, 0);
        form.add(new Label("Username:"), 0, 1);
        form.add(usernameField, 1, 1);
        form.add(new Label("Password:"), 0, 2);
        form.add(passwordField, 1, 2);
        form.add(new Label("Initial Balance:"), 0, 3);
        form.add(balanceField, 1, 3);
        form.add(registerBtn, 0, 4, 2, 1);
        form.add(backBtn, 0, 5, 2, 1);

        registerBtn.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String balanceStr = balanceField.getText().trim();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || balanceStr.isEmpty()) {
                mainApp.showAlert("Error", "Please fill all fields");
                return;
            }
            try {
                double balance = Double.parseDouble(balanceStr);
                if (balance < 0) {
                    mainApp.showAlert("Error", "Balance cannot be negative");
                    return;
                }
                if (!isValidPassword(password)) {
                    mainApp.showAlert("Error",
                            "Password must be at least 8 characters long and contain uppercase letters, lowercase letters, and numbers");
                    return;
                }
                if (registerUser(username, fullName, password, balance)) {
                    mainApp.showAlert("Success", "Registration successful! You can now login.");
                    showLoginScreen();
                } else {
                    mainApp.showAlert("Error", "Username already exists!");
                }
            } catch (NumberFormatException ex) {
                mainApp.showAlert("Error", "Please enter a valid balance amount");
            }
        });

        backBtn.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(titleLabel, form);

        Scene scene = new Scene(root, 450, 550);
        mainApp.getPrimaryStage().setScene(scene);
    }
}