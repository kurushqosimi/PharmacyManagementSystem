package models;

public class OrderDetail {
    private int medicationId;
    private String medicationName;
    private int quantity;
    private double pricePerUnit;

    // Конструктор
    public OrderDetail(int medicationId, String medicationName, int quantity, double pricePerUnit) {
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }
}
