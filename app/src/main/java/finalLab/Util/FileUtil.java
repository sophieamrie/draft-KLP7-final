package finalLab.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    // Membaca semua baris dalam file dan mengembalikan sebagai List<String>
    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // Menulis sebuah list baris ke file, bisa pilih append atau overwrite
    public static void writeLines(String filePath, List<String> lines, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // Menghapus file
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    // Mengecek apakah file ada
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // ...existing code...

    // Menambahkan satu baris ke akhir file (append)
    public static void appendLine(String filePath, String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    // Mengganti satu baris tertentu (berdasarkan index) di file
    public static void replaceLine(String filePath, int index, String newLine) throws IOException {
        List<String> lines = readLines(filePath);
        if (index >= 0 && index < lines.size()) {
            lines.set(index, newLine);
            writeLines(filePath, lines, false);
        }
    }

    // Menghapus satu baris tertentu (berdasarkan index) di file
    public static void deleteLine(String filePath, int index) throws IOException {
        List<String> lines = readLines(filePath);
        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
            writeLines(filePath, lines, false);
        }
    }

// ...existing code...
}
