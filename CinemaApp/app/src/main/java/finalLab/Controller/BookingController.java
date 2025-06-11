package finalLab.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import finalLab.Main;
import finalLab.Model.Movie;
import finalLab.Model.SnackItem;
import finalLab.Model.User;
import finalLab.Service.MovieService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BookingController {

    private Main mainApp;
    private MovieService movieService;
    private SnackController snackController;

    private String currentTicketId;
    private String currentTicketText;
    private String paymentMethod;
    private List<String> selectedSeats;
    private boolean paymentCompleted;
    private String selectedTime;
    private User currentUser;
    private Movie selectedMovie;
    private String selectedDate;
    private String selectedSchedule;
    private String selectedSeat;
    private List<SnackItem> selectedSnacks;
    private Stage primaryStage;

    public BookingController(Main mainApp, User currentUser) {
        this.mainApp = mainApp;
        this.primaryStage = mainApp.getPrimaryStage();
        this.currentUser = currentUser;
        this.movieService = mainApp.getMovieService();
    }

    // Setter untuk SnackController (agar bisa di-inject dari Main)
    public void setSnackController(SnackController snackController) {
        this.snackController = snackController;
    }

    // Getter dan setter
    public void updateUserBalance(double newBalance) {
        currentUser.setBalance(newBalance);
    }
    public String getCurrentTicketId() { return currentTicketId; }
    public void setCurrentTicketId(String currentTicketId) { this.currentTicketId = currentTicketId; }
    public String getCurrentTicketText() { return currentTicketText; }
    public void setCurrentTicketText(String currentTicketText) { this.currentTicketText = currentTicketText; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setSelectedSeats(List<String> selectedSeats) { this.selectedSeats = selectedSeats; }
    public List<String> getSelectedSeats() { return selectedSeats; }
    public boolean isPaymentCompleted() { return paymentCompleted; }
    public void setPaymentCompleted(boolean paymentCompleted) { this.paymentCompleted = paymentCompleted; }
    public String getSelectedTime() { return selectedTime; }
    public void setSelectedTime(String time) { this.selectedTime = time; }
    public User getCurrentUser() { return currentUser; }
    public Movie getSelectedMovie() { return selectedMovie; }
    public String getSelectedDate() { return selectedDate; }
    public String getSelectedSchedule() { return selectedSchedule; }
    public String getSelectedSeat() { return selectedSeat; }
    public List<SnackItem> getSelectedSnacks() { return selectedSnacks; }
    public void setSelectedSnacks(List<SnackItem> snacks) { this.selectedSnacks = snacks; }
    public Stage getPrimaryStage() { return this.primaryStage; }

    // --- GUI Methods ---

    public void showMoviesList() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🎬 AVAILABLE MOVIES");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        ScrollPane scrollPane = new ScrollPane();
        VBox moviesContainer = new VBox(10);

        List<Movie> movies = movieService.loadMovies();
        for (Movie movie : movies) {
            VBox movieBox = new VBox(10);
            movieBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

            Label titleLbl = new Label(movie.getTitle());
            titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label genreLbl = new Label("Genre: " + movie.getGenre());
            Label durationLbl = new Label("Duration: " + movie.getDuration() + " minutes");
            Label scheduleLbl = new Label("Schedules: " + String.join(", ", movie.getSchedules()));

            Button selectBtn = new Button("SELECT MOVIE");
            selectBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

            selectBtn.setOnAction(e -> {
                selectedMovie = movie;
                showDateSelection();
            });

            movieBox.getChildren().addAll(titleLbl, genreLbl, durationLbl, scheduleLbl, selectBtn);
            moviesContainer.getChildren().add(movieBox);
        }

        scrollPane.setContent(moviesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Button backBtn = new Button("← BACK TO MENU");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> mainApp.showMainMenu());

        root.getChildren().addAll(titleLabel, scrollPane, backBtn);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }

    public void showDateSelection() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("📅 SELECT DATE");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label movieLabel = new Label("Movie: " + selectedMovie.getTitle());
        movieLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        VBox dateBox = new VBox(15);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label instructionLabel = new Label("Select a date (next 5 days):");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup dateGroup = new ToggleGroup();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        DateTimeFormatter saveFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            String displayDate = date.format(dateFormatter);
            String saveDate = date.format(saveFormatter);

            if (i == 0) displayDate = "Today - " + displayDate;
            else if (i == 1) displayDate = "Tomorrow - " + displayDate;

            RadioButton radioBtn = new RadioButton(displayDate);
            radioBtn.setToggleGroup(dateGroup);
            radioBtn.setUserData(saveDate);
            radioBtn.setStyle("-fx-font-size: 14px;");
            dateBox.getChildren().add(radioBtn);
        }

        Button nextBtn = new Button("NEXT: SELECT TIME");
        nextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        nextBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) dateGroup.getSelectedToggle();
            if (selected != null) {
                selectedDate = (String) selected.getUserData();
                showScheduleSelection();
            } else {
                mainApp.showAlert("Error", "Please select a date");
            }
        });

        backBtn.setOnAction(e -> showMoviesList());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, nextBtn);

        dateBox.getChildren().addAll(instructionLabel, new Separator(), buttonBox);
        root.getChildren().addAll(titleLabel, movieLabel, dateBox);

        Scene scene = new Scene(root, 450, 500);
        primaryStage.setScene(scene);
    }

    public void showScheduleSelection() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🕐 SELECT TIME");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label movieLabel = new Label("Movie: " + selectedMovie.getTitle());
        movieLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        LocalDate date = LocalDate.parse(selectedDate);
        String displayDate = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        Label dateLabel = new Label("Date: " + displayDate);
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f1c40f;");

        VBox scheduleBox = new VBox(15);
        scheduleBox.setAlignment(Pos.CENTER);
        scheduleBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label instructionLabel = new Label("Select showtime:");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup scheduleGroup = new ToggleGroup();

        for (String schedule : selectedMovie.getSchedules()) {
            RadioButton radioBtn = new RadioButton(schedule);
            radioBtn.setToggleGroup(scheduleGroup);
            radioBtn.setUserData(schedule);
            radioBtn.setStyle("-fx-font-size: 14px;");
            scheduleBox.getChildren().add(radioBtn);
        }

        Button nextBtn = new Button("NEXT: SELECT SEAT");
        nextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

        nextBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) scheduleGroup.getSelectedToggle();
            if (selected != null) {
                selectedSchedule = (String) selected.getUserData();
                showSeatSelection();
            } else {
                mainApp.showAlert("Error", "Please select a schedule");
            }
        });

        backBtn.setOnAction(e -> showDateSelection());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, nextBtn);

        scheduleBox.getChildren().addAll(instructionLabel, new Separator(), buttonBox);
        root.getChildren().addAll(titleLabel, movieLabel, dateLabel, scheduleBox);

        Scene scene = new Scene(root, 450, 450);
        primaryStage.setScene(scene);
    }

    public void showSeatSelection() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("💺 SELECT SEAT");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        LocalDate date = LocalDate.parse(selectedDate);
        String displayDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label infoLabel = new Label(selectedMovie.getTitle() + " - " + displayDate + " - " + selectedSchedule);
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        VBox seatBox = new VBox(10);
        seatBox.setAlignment(Pos.CENTER);
        seatBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");

        Label screenLabel = new Label("🎬 SCREEN 🎬");
        screenLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 10;");

        GridPane seatGrid = new GridPane();
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);

        ToggleGroup seatGroup = new ToggleGroup();
        String[] rows = { "A", "B", "C", "D", "E" };

        for (int i = 0; i < rows.length; i++) {
            for (int j = 1; j <= 8; j++) {
                String seatId = rows[i] + j;
                RadioButton seatBtn = new RadioButton(seatId);
                seatBtn.setToggleGroup(seatGroup);
                seatBtn.setUserData(seatId);
                seatBtn.setStyle("-fx-font-size: 10px;");
                seatGrid.add(seatBtn, j - 1, i);
            }
        }

        Button nextBtn = new Button("NEXT: SELECT SNACKS");
        nextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 180;");

        Button backBtn = new Button("← BACK");
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 180;");

        nextBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) seatGroup.getSelectedToggle();
            if (selected != null) {
                selectedSeat = (String) selected.getUserData();
                if (snackController != null) {
                    snackController.showSnackSelection();
                } else {
                    mainApp.showAlert("Error", "SnackController not set!");
                }
            } else {
                mainApp.showAlert("Error", "Please select a seat");
            }
        });

        backBtn.setOnAction(e -> showScheduleSelection());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, nextBtn);

        seatBox.getChildren().addAll(screenLabel, seatGrid, buttonBox);
        root.getChildren().addAll(titleLabel, infoLabel, seatBox);

        Scene scene = new Scene(root, 500, 450);
        primaryStage.setScene(scene);
    }
}