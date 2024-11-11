package org.example;

import models.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();
        // Test your connection and any sample query if needed.
        dbManager.disconnect();
    }
}
