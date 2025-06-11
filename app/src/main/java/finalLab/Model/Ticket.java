package finalLab.Model;

import java.util.List;

public class Ticket {
    private String ticketId;
    private String username;
    private String movieTitle;
    private String date;
    private String schedule;
    private String seat;
    private List<SnackItem> snacks;

    public Ticket() {
    }

    // Getter dan Setter

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public List<SnackItem> getSnacks() {
        return snacks;
    }

    public void setSnacks(List<SnackItem> snacks) {
        this.snacks = snacks;
    }
}
