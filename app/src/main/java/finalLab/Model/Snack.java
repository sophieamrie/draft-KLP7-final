package finalLab.Model;

public class Snack {
    private String name;
    private double price;

    public Snack(String name) {
        this.name = name;
    }
    
    public Snack(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @Override
    public String toString() {
        return "Snack{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
