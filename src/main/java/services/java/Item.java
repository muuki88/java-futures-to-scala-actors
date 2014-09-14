package services.java;

public class Item {

    private final String id;
    private final int client;
    private final double price;

    public Item(String id, int client, double price) {
        this.id = id;
        this.client = client;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public int getClient() {
        return client;
    }

    public double getPrice() {
        return price;
    }

}
