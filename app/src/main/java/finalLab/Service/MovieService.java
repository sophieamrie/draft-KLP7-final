package finalLab.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import finalLab.Main;
import finalLab.Model.Movie;

public class MovieService {
    private Main mainApp;

    public MovieService(Main mainApp) {
        this.mainApp = mainApp;
    }

    public MovieService() {

    }
    
    public List<Movie> loadMovies() {
        List<Movie> movies = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("movies.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String[] schedules = parts[3].split(",");
                    movies.add(new Movie(parts[0], parts[1], Integer.parseInt(parts[2]), Arrays.asList(schedules)));
                }
            }
        } catch (IOException e) {
    if (mainApp != null) {
        mainApp.showAlert("Error", "Failed to load movies");
    } else {
        System.err.println("Failed to load movies: " + e.getMessage());
    }
}
        return movies;
    }
}