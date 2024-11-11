import models.*;
public class TestDatabaseManager {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        // Пример добавления лекарства
        Medication medication = new Medication(0, "Aspirin", "Pain reliever", 100, "123456789", true, false, 1, 80.99);
        dbManager.addMedication(medication, );

        // Получение всех лекарств
        System.out.println("All Medications:");
        for (Medication med : dbManager.getAllMedications()) {
            System.out.println(med.getName());
        }

        // Обновление лекарства
        medication.setQuantity(90);
        dbManager.updateMedication(medication);

        // Мягкое удаление лекарства
        dbManager.softDeleteMedication(medication.getId());

        dbManager.disconnect();
    }
}
