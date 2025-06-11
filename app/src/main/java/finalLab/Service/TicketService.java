package finalLab.Service;

import finalLab.Model.Movie;
import finalLab.Model.Snack;
import finalLab.Model.SnackItem;
import finalLab.Model.Ticket;
import finalLab.Model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketService {

    private final String ticketDataFile = "tickets/tickets.txt";

    // Mendapatkan list seat yang sudah dibooking berdasarkan movie, date, schedule
    public List<String> getBookedSeats(Movie movie, String date, String schedule) {
        List<String> bookedSeats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ticketDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: ticketId|username|movieTitle|date|schedule|seat|snack1,qty;...
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                String movieTitle = parts[2];
                String bookedDate = parts[3];
                String bookedSchedule = parts[4];
                String bookedSeat = parts[5];

                if (movieTitle.equals(movie.getTitle())
                        && bookedDate.equals(date)
                        && bookedSchedule.equals(schedule)) {
                    bookedSeats.add(bookedSeat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    // Mengecek apakah seat tersedia (tidak bisa double booking)
public boolean isSeatAvailable(Movie movie, String date, String schedule, String seat) {
    File file = new File("tickets/tickets.txt");
    if (!file.exists()) return true;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length < 6) continue;

            String movieTitle = parts[2];
            String bookedDate = parts[3];
            String bookedSchedule = parts[4];
            String bookedSeat = parts[5];

            if (movieTitle.equals(movie.getTitle())
                    && bookedDate.equals(date)
                    && bookedSchedule.equals(schedule)
                    && bookedSeat.equals(seat)) {
                return false; // Seat sudah dibooking
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return true;
}

    // Mencoba booking seat, jika seat sudah dibooking di waktu yang sama maka gagal dan return false.
    public boolean tryBookSeat(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        if (isSeatAvailable(movie, date, schedule, seat)) {
            generateTicket(user, movie, date, schedule, seat, snacks);
            return true;
        } else {
            // Seat sudah dibooking untuk movie, date, schedule yang sama
            return false;
        }
    }

    // Membuat tiket baru dan menyimpan ke file, lalu mengembalikan ticket ID
    public String generateTicket(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        String ticketId = UUID.randomUUID().toString();

        StringBuilder snackData = new StringBuilder();
        for (SnackItem item : snacks) {
            snackData.append(item.getSnack().getName())
                    .append(",")
                    .append(item.getQuantity())
                    .append(";");
        }

        String ticketLine = String.join("|",
                ticketId,
                user.getUsername(),
                movie.getTitle(),
                date,
                schedule,
                seat,
                snackData.toString()
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ticketDataFile, true))) {
            writer.write(ticketLine);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ticketId;
    }

    // Menyimpan booking seat dan snack ke file (bisa reuse generateTicket)
    public void bookSeat(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        generateTicket(user, movie, date, schedule, seat, snacks);
    }

    // Mendapatkan list tiket yang dipesan user
    public List<Ticket> getTicketsByUser(User user) {
    List<Ticket> tickets = new ArrayList<>();
    File file = new File("tickets/tickets.txt");
    if (!file.exists()) return tickets;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        Ticket ticket = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("|==========")) {
                ticket = new Ticket();
            } else if (ticket != null) {
                if (line.startsWith("Ticket ID")) ticket.setTicketId(line.split(":", 2)[1].trim());
                if (line.startsWith("Customer")) ticket.setUsername(line.split(":", 2)[1].trim());
                if (line.startsWith("Movie")) ticket.setMovieTitle(line.split(":", 2)[1].trim());
                if (line.startsWith("Date") && ticket.getDate() == null) ticket.setDate(line.split(":", 2)[1].trim());
                if (line.startsWith("Schedule")) ticket.setSchedule(line.split(":", 2)[1].trim());
                if (line.startsWith("Seat")) ticket.setSeat(line.split(":", 2)[1].trim());
                // ...tambahkan parsing snack jika perlu...
                if (line.startsWith("|==================================|")) {
                    if (ticket.getUsername().equals(user.getUsername())) {
                        tickets.add(ticket);
                    }
                    ticket = null;
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return tickets;
}

    // Membatalkan tiket berdasarkan ID (hapus dari file)
    public void cancelTicket(String ticketId) {
        File inputFile = new File(ticketDataFile);
        File tempFile = new File("tickets_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(ticketId + "|")) {
                    // Skip tiket yang mau dibatalkan
                    continue;
                }
                writer.write(currentLine);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Hapus file lama dan ganti dengan file baru
        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }
}