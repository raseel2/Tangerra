package vehicle_rental_management_system_p;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnExit;

    private UserDatabase userDB;
    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    @FXML
    public void initialize() {
        userDB = new UserDatabase();
        lblError.setText("");

        txtPassword.setOnAction(e -> handleLogin(e));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        lblError.setText("");

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password");
            return;
        }

        try {
            User user = userDB.authenticate(username, password);

            if (user != null) {
                System.out.println("Login successful: " + user.getFullName() + " (" + user.getUserType() + ")");

                SessionManager session = SessionManager.getInstance();
                session.login(
                        user.getUserId(),
                        user.getUsername(),
                        user.getUserType(),
                        user.getFullName(),
                        user.getCustomerId()
                );

                navigateToDashboard(event);

            } else {
                loginAttempts++;
                System.out.println("Login failed for username: " + username + " (Attempt " + loginAttempts + ")");

                if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                    lblError.setText("Maximum login attempts exceeded. Please try again later.");
                    btnLogin.setDisable(true);
                } else {
                    lblError.setText("Invalid username or password. Attempt " + loginAttempts + " of " + MAX_LOGIN_ATTEMPTS);
                }

                txtPassword.clear();
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            lblError.setText("An error occurred. Please try again.");
        }
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard - Tangerra");
            stage.setResizable(true);
            stage.setMaximized(false);

        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            lblError.setText("Error loading dashboard");
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.out.println("Application exiting...");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
