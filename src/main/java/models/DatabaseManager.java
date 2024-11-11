package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Medication;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/pharmacy_management";
    private static final String USER = "pharmacy_user";
    private static final String PASSWORD = "pharmacy_user";

    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to PostgreSQL server.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from PostgreSQL server.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для добавления лекарства
    public boolean addMedication(Medication medication, User currentUser) {
        // Проверка прав пользователя на добавление
        if (!"admin".equals(currentUser.getRole()) && !"pharmacist".equals(currentUser.getRole())) {
            System.out.println("Ошибка: У пользователя нет прав на добавление лекарства.");
            return false;
        }

        String query = "INSERT INTO medications (name, description, quantity, barcode, is_active, is_prescription_required, created_by, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Заполнение параметров запроса
            statement.setString(1, medication.getName());
            statement.setString(2, medication.getDescription());
            statement.setInt(3, medication.getQuantity());
            statement.setString(4, medication.getBarcode());
            statement.setBoolean(5, medication.isActive());
            statement.setBoolean(6, medication.isPrescriptionRequired());
            statement.setInt(7, currentUser.getId()); // Используем ID текущего пользователя
            statement.setDouble(8, medication.getPrice());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Лекарство успешно добавлено в базу данных.");
                return true;
            } else {
                System.out.println("Ошибка: Лекарство не было добавлено.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Метод для получения списка всех лекарств
    public List<Medication> getAllMedications() {
        List<Medication> medications = new ArrayList<>();
        String query = "SELECT * FROM medications WHERE is_active = true";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Medication medication = new Medication(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("barcode"),
                        resultSet.getBoolean("is_active"),
                        resultSet.getBoolean("is_prescription_required"),
                        resultSet.getInt("created_by"),
                        resultSet.getDouble("price")
                );
                medications.add(medication);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medications;
    }

    // Метод для получения одного лекарства по id
    public Medication getMedicationById(int id) {
        String query = "SELECT * FROM medications WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Medication(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("barcode"),
                        resultSet.getBoolean("is_active"),
                        resultSet.getBoolean("is_prescription_required"),
                        resultSet.getInt("created_by"),
                        resultSet.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для обновления лекарства
    public boolean updateMedication(Medication medication, User currentUser) {
        if (!"admin".equals(currentUser.getRole()) && !"pharmacist".equals(currentUser.getRole())) {
            System.out.println("У пользователя нет прав на редактирование лекарства.");
            return false;
        }

        String query = "UPDATE medications SET name = ?, description = ?, quantity = ?, barcode = ?, is_active = ?, is_prescription_required = ?, price = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, medication.getName());
            statement.setString(2, medication.getDescription());
            statement.setInt(3, medication.getQuantity());
            statement.setString(4, medication.getBarcode());
            statement.setBoolean(5, medication.isActive());
            statement.setBoolean(6, medication.isPrescriptionRequired());
            statement.setDouble(7, medication.getPrice());
            statement.setInt(8, medication.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для мягкого удаления лекарства
    public boolean softDeleteMedication(int id, User currentUser) {
        if (!"admin".equals(currentUser.getRole()) && !"pharmacist".equals(currentUser.getRole())) {
            System.out.println("У пользователя нет прав на удаление лекарства.");
            return false;
        }

        String query = "UPDATE medications SET is_active = false WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = true";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // Здесь можно добавить хеширование пароля

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Добавление нового пользователя
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password, role, is_active) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword()); // Желательно использовать хеширование пароля
            statement.setString(3, user.getRole());
            statement.setBoolean(4, user.isActive());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Деактивация пользователя
    public boolean deactivateUser(int userId) {
        String query = "UPDATE users SET is_active = false WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновление пользователя
    public boolean updateUser(User user) {
        String query = "UPDATE users SET username = ?, password = ?, role = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setBoolean(4, user.isActive());
            statement.setInt(5, user.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получение пользователя по ID
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Medication getMedicationByBarcode(String barcode) {
        String query = "SELECT * FROM medications WHERE barcode = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, barcode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Medication(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("quantity"),
                        barcode,
                        resultSet.getBoolean("is_active"),
                        resultSet.getBoolean("is_prescription_required"),
                        resultSet.getInt("created_by"),
                        resultSet.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Завершение заказа
    public void completeOrder(Order order, User currentUser) {
        String insertOrderQuery = "INSERT INTO orders (total_amount, created_by) VALUES (?, ?) RETURNING id";
        String insertOrderDetailQuery = "INSERT INTO order_details (order_id, medication_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";
        String updateMedicationQuery = "UPDATE medications SET quantity = quantity - ? WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Начало транзакции

            // Сохранение основного заказа и получение сгенерированного ID
            int orderId;
            try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery)) {
                orderStatement.setDouble(1, order.getTotalAmount());
                orderStatement.setInt(2, currentUser.getId());
                ResultSet resultSet = orderStatement.executeQuery();
                resultSet.next();
                orderId = resultSet.getInt(1); // Получаем ID созданного заказа
            }

            // Сохранение каждой детали заказа в order_details и обновление количества на складе
            try (PreparedStatement detailStatement = connection.prepareStatement(insertOrderDetailQuery);
                 PreparedStatement updateMedicationStatement = connection.prepareStatement(updateMedicationQuery)) {

                for (Medication medication : order.getMedications()) {
                    // Добавление детали заказа
                    detailStatement.setInt(1, orderId);
                    detailStatement.setInt(2, medication.getId());
                    detailStatement.setInt(3, medication.getQuantity());
                    detailStatement.setDouble(4, medication.getPrice());
                    detailStatement.addBatch();

                    // Обновление количества лекарства на складе
                    updateMedicationStatement.setInt(1, medication.getQuantity());
                    updateMedicationStatement.setInt(2, medication.getId());
                    updateMedicationStatement.addBatch();
                }

                detailStatement.executeBatch(); // Сохранение всех деталей заказа
                updateMedicationStatement.executeBatch(); // Обновление всех количеств лекарств
            }

            connection.commit(); // Подтверждение транзакции

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Откат транзакции в случае ошибки
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Завершение транзакции
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveOrder(Order order, User currentUser) {
        String insertOrderQuery = "INSERT INTO orders (total_amount, created_by) VALUES (?, ?) RETURNING id";
        String insertOrderDetailQuery = "INSERT INTO order_details (order_id, medication_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false); // Начало транзакции

            // Сохранение основного заказа и получение сгенерированного id
            int orderId;
            try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery)) {
                orderStatement.setDouble(1, order.getTotalAmount());
                orderStatement.setInt(2, currentUser.getId());
                ResultSet resultSet = orderStatement.executeQuery();
                resultSet.next();
                orderId = resultSet.getInt(1);
            }

            // Сохранение каждой детали заказа
            try (PreparedStatement detailStatement = connection.prepareStatement(insertOrderDetailQuery)) {
                for (Medication medication : order.getMedications()) {
                    detailStatement.setInt(1, orderId);
                    detailStatement.setInt(2, medication.getId());
                    detailStatement.setInt(3, medication.getQuantity());
                    detailStatement.setDouble(4, medication.getPrice());
                    detailStatement.addBatch();
                }
                detailStatement.executeBatch();
            }

            connection.commit(); // Подтверждение транзакции
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Откат в случае ошибки
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Завершение транзакции
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<OrderHistory> getOrderHistory() {
        return getOrderHistoryByPeriod(""); // Пустое условие для получения всех заказов
    }

    // Получение заказов за последнюю неделю
    public List<OrderHistory> getOrderHistoryByWeek() {
        return getOrderHistoryByPeriod("WHERE o.order_date >= NOW() - INTERVAL '1 week'");
    }

    public List<OrderHistory> getOrderHistoryByMonth() {
        return getOrderHistoryByPeriod("WHERE o.order_date >= NOW() - INTERVAL '1 month'");
    }

    public List<OrderHistory> getOrderHistoryByYear() {
        return getOrderHistoryByPeriod("WHERE o.order_date >= NOW() - INTERVAL '1 year'");
    }

    // Универсальный метод для выполнения запросов по периодам
    private List<OrderHistory> getOrderHistoryByPeriod(String condition) {
        List<OrderHistory> orders = new ArrayList<>();
        // Основной SQL-запрос
        String sql = "SELECT o.id, o.order_date, o.total_amount, o.created_by, u.username AS created_by_name " +
                "FROM orders o JOIN users u ON o.created_by = u.id ";

        // Добавляем условие, если оно указано
        if (condition != null && !condition.isEmpty()) {
            sql += condition;
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                OrderHistory order = new OrderHistory(
                        resultSet.getInt("id"),
                        resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getDouble("total_amount"),
                        resultSet.getInt("created_by"),
                        resultSet.getString("created_by_name") // Получаем имя пользователя
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT od.medication_id, m.name, od.quantity, od.price_per_unit " +
                "FROM order_details od JOIN medications m ON od.medication_id = m.id " +
                "WHERE od.order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderDetails.add(new OrderDetail(
                        resultSet.getInt("medication_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price_per_unit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    public OrderHistory getOrderSummary(int orderId) {
        String query = "SELECT o.id, o.order_date, o.total_amount, o.created_by, u.username AS created_by_name " +
                "FROM orders o JOIN users u ON o.created_by = u.id " +
                "WHERE o.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new OrderHistory(
                        resultSet.getInt("id"),
                        resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getDouble("total_amount"),
                        resultSet.getInt("created_by"), // ID пользователя
                        resultSet.getString("created_by_name") // Имя пользователя
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
