package finalLab.Service;

import finalLab.Model.Snack;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SnackService {

    // Load semua snack dari file
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
            System.err.println("Failed to load snacks: " + e.getMessage());
        }
        return snacks;
    }

    // Cari snack berdasarkan nama (case-insensitive)
    public Snack findSnackByName(String name) {
        List<Snack> snacks = loadSnacks();
        for (Snack snack : snacks) {
            if (snack.getName().equalsIgnoreCase(name)) {
                return snack;
            }
        }
        return null;
    }
}
