package models;

public class Medication {
    private int id;
    private String name;
    private String description;
    private int quantity;
    private String barcode;
    private boolean isActive;
    private boolean isPrescriptionRequired;
    private int createdBy;
    private double price; // Поле для цены

    // Конструктор
    public Medication(int id, String name, String description, int quantity, String barcode, boolean isActive, boolean isPrescriptionRequired, int createdBy, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.barcode = barcode;
        this.isActive = isActive;
        this.isPrescriptionRequired = isPrescriptionRequired;
        this.createdBy = createdBy;
        this.price = price;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isPrescriptionRequired() {
        return isPrescriptionRequired;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public double getPrice() {
        return price;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        isPrescriptionRequired = prescriptionRequired;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
