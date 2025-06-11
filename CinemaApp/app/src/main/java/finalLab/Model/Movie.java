package finalLab.Model;

import java.util.List;

public class Movie {
    private String title;
    private String genre;
    private int duration;
    private List<String> schedules;

    
    public Movie(String title, String genre, int duration, List<String> schedules) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.schedules = schedules;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", schedules=" + schedules +
                '}';
    }
}
