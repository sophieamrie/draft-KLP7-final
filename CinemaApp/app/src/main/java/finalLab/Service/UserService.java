package finalLab.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import finalLab.Model.User;

public class UserService {

    public void updateUserBalance(User currentUser) throws IOException {
        System.out.println("User file path: " + new File("users.txt").getAbsolutePath());
        File file = new File("users.txt");
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[0].equals(currentUser.getUsername())) {
                    // Update baris user dengan balance terbaru
                    lines.add(parts[0] + "|" + parts[1] + "|" + parts[2] + "|" + currentUser.getBalance());
                } else {
                    lines.add(line);
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }
}