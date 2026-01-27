package edu.brandeis.cosi103a.ip2;

public class Card {
    private int cost;
    private int value;
    private boolean isCurrency;
    private String name;

    public Card(int cost, int value, boolean isCurrency, String name) {
        this.cost = cost;
        this.value = value;
        this.isCurrency = isCurrency;
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public int getValue() {
        return value;
    }

    public boolean isCurrency() {
        return isCurrency;
    }

    public String getName() {
        return name;
    }
}
