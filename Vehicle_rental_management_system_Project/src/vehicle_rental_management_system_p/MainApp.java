package vehicle_rental_management_system_p;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            SequenceFixer fixer = new SequenceFixer();
            fixer.fixSequences();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 700, 800);

            primaryStage.setTitle("Login - Tangerra");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

            System.out.println("Application started successfully");

        } catch (Exception e) {
            System.err.println("Error loading login screen: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application shutting down...");
        DatabaseConnection.getInstance().closeConnection();
        super.stop();
    }

    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Vehicle Rental Management System");
        System.out.println("Starting application..");
        System.out.println("==============================================\n");

      
        launch(args);
    }
}
