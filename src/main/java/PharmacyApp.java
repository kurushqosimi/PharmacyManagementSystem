import models.DatabaseManager;
import views.LoginFrame;

public class PharmacyApp {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        LoginFrame loginFrame = new LoginFrame(dbManager);
        loginFrame.setVisible(true);
    }
}
