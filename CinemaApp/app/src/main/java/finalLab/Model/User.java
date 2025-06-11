package finalLab.Model;

public class User {
    private String username;
    private String fullName;
    private String password;
    public double balance;

    public User(String username, String fullName, String password, double balance) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.balance = balance;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
