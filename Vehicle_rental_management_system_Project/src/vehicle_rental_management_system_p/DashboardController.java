package vehicle_rental_management_system_p;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.scene.text.Font;


public class DashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblUserType;

    @FXML
    private Label lblSystemInfo;

    @FXML
    private Label lblTotalVehicles;

    @FXML
    private Label lblAvailableVehicles;

    @FXML
    private Label lblActiveRentals;

    @FXML
    private Label lblTotalCustomers;

    @FXML
    private Button btnVehicles;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnRentals;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnExit;

    private SessionManager session;

  
    @FXML
    public void initialize() {
        session = SessionManager.getInstance();

        if (!session.isLoggedIn()) {
            System.err.println("No user logged in!");
            return;
        }

        lblWelcome.setText("Welcome " + session.getFullName());
        lblWelcome.setFont(new Font("Calibri", 16));
        lblUserType.setText("Role: " + session.getUserType());

        configureMenuByRole();

        loadStats();
    }

  
    private void configureMenuByRole() {
        if (session.isAdmin()) {
            btnVehicles.setVisible(true);
            btnVehicles.setText("Manage Vehicles");
            btnCustomers.setVisible(true);
            btnRentals.setVisible(true);
            btnRentals.setText("Manage Rentals");
            btnUsers.setVisible(true);
            lblSystemInfo.setText("Admin Dashboard - Full Access");

        } else if (session.isEmployee()) {
            btnVehicles.setVisible(true);
            btnVehicles.setText("Manage Vehicles");
            btnCustomers.setVisible(true);
            btnRentals.setVisible(true);
            btnRentals.setText("Manage Rentals");
            btnUsers.setVisible(false);
            lblSystemInfo.setText("Employee Dashboard");

        } else if (session.isCustomer()) {
            btnVehicles.setVisible(true);
            btnVehicles.setText("Book a Vehicle"); 
            btnCustomers.setVisible(false);
            btnRentals.setVisible(true);
            btnRentals.setText("My Bookings"); 
            btnUsers.setVisible(false);
            lblSystemInfo.setText("Customer Dashboard - Book Your Ride!");
        }
    }

    
    private void loadStats() {
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM VEHICLE");
            if (rs.next()) {
                lblTotalVehicles.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

            rs = stmt.executeQuery("SELECT COUNT(*) FROM VEHICLE WHERE is_available = 'Y'");
            if (rs.next()) {
                lblAvailableVehicles.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

            rs = stmt.executeQuery("SELECT COUNT(*) FROM RENTAL WHERE status = 'ACTIVE'");
            if (rs.next()) {
                lblActiveRentals.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

            rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMER");
            if (rs.next()) {
                lblTotalCustomers.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

            stmt.close();

        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    @FXML
    private void handleVehicles(ActionEvent event) {
        try {
            String viewPath;
            String title;

            if (session.isCustomer()) {
                viewPath = "view/customer_booking.fxml";
                title = "Book a Vehicle - Tangerra";
            } else {
                viewPath = "view/vehicle_management.fxml";
                title = "Vehicle Management - Tangerra";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(true);

        } catch (Exception e) {
            System.err.println("Error loading screen: " + e.getMessage());
            e.printStackTrace();
            showInfoAlert("Error", "Failed to load screen");
        }
    }

  
    @FXML
    private void handleCustomers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/customer_management.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Management - Vehicle Rental Management System");
            stage.setResizable(true);
        } catch (Exception e) {
            showInfoAlert("Error", "Failed to load customer management");
        }
    }

    
    @FXML
    private void handleRentals(ActionEvent event) {
        try {
            String viewPath;
            String title;

            if (session.isCustomer()) {
                viewPath = "view/customer_bookings.fxml";
                title = "My Bookings - Tangerra";
            } else {
                viewPath = "view/rental_management.fxml";
                title = "Rental Management - Tangerra";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(true);
        } catch (Exception e) {
            showInfoAlert("Error", "Failed to load screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    @FXML
    private void handleUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/user_management.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Management - Vehicle Rental Management System");
            stage.setResizable(true);
        } catch (Exception e) {
            showInfoAlert("Error", "Failed to load user management: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Logout");
            alert.setHeaderText("Are you sure you want to logout?");
            alert.setContentText("You will be returned to the login screen.");

            alert.showAndWait();
            if (alert.getResult() != null && alert.getResult() == ButtonType.OK) {
                session.logout();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 600, 500);
                stage.setScene(scene);
                stage.setTitle("Login - Vehicle Rental Management System");
                stage.setResizable(true);
            }

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    @FXML
    private void handleExit(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Exit");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("The application will close.");

            alert.showAndWait();
            if (alert.getResult() != null && alert.getResult() == ButtonType.OK) {
                session.logout();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }

        } catch (Exception e) {
            System.err.println("Error during exit: " + e.getMessage());
            e.printStackTrace();
        }
    }

  
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
