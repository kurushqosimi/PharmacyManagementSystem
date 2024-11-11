package models;

import java.time.LocalDateTime;

public class OrderHistory {
    private int id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private int createdBy;
    private String createdByName; // Имя пользователя, создавшего заказ

    // Конструктор для всех полей
    public OrderHistory(int id, LocalDateTime orderDate, double totalAmount, int createdBy, String createdByName) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
