package vehicle_rental_management_system_p;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class UserManagementController {

    @FXML private Button btnBack;

    @FXML private Button btnAdd;
    @FXML private Button btnRefresh;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colUserType;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblMessage;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> cbUserType;
    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Button btnCancel;
    @FXML private Label lblError;

    private UserDatabase userDB;
    private SessionManager session;

    private ObservableList<User> userList;
    private boolean isEditMode = false;
    private User selectedUser;

   
    @FXML
    public void initialize() {
        userDB = new UserDatabase();
        session = SessionManager.getInstance();

        if (!session.isAdmin()) {
            showError("Access Denied: Admin privileges required");
            btnAdd.setDisable(true);
            btnSave.setDisable(true);
            return;
        }

        setupTableColumns();

        loadAllUsers();

        setupUserTypeComboBox();

        tableUsers.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleTableSelection();
            }
        });
    }

   
    private void setupTableColumns() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUserType.setCellValueFactory(new PropertyValueFactory<>("userType"));
    }

   
    private void loadAllUsers() {
        ArrayList<User> users = userDB.getUsers();
        userList = FXCollections.observableArrayList(users);
        tableUsers.setItems(userList);
        lblTotalUsers.setText("Total: " + users.size());
    }

   
    private void setupUserTypeComboBox() {
        ObservableList<String> userTypes = FXCollections.observableArrayList(
            "ADMIN", "EMPLOYEE", "CUSTOMER"
        );
        cbUserType.setItems(userTypes);
    }

  
    private void handleTableSelection() {
        selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            populateFormWithUser(selectedUser);
        }
    }

   
    private void populateFormWithUser(User user) {
        txtUsername.setText(user.getUsername());
        txtFullName.setText(user.getFullName());
        cbUserType.setValue(user.getUserType());

        txtPassword.clear();

        isEditMode = true;
        lblFormTitle.setText("Edit User");
        btnSave.setText("Update");
    }

   
    @FXML
    private void handleAdd(ActionEvent event) {
        clearForm();
        isEditMode = false;
        lblFormTitle.setText("Add New User");
        btnSave.setText("Save");
        show("Fill in the form to add a new user", "blue");
    }


    
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            User user = new User();

            if (isEditMode) {
                user.setUserId(selectedUser.getUserId());
            } else {
                user.setUserId(null); 
            }

            user.setUsername(txtUsername.getText().trim());
            user.setFullName(txtFullName.getText().trim());
            user.setUserType(cbUserType.getValue());

            String password = txtPassword.getText();
            if (!password.isEmpty()) {
                user.setPassword(password);
            } else if (isEditMode) {
                user.setPassword(selectedUser.getPassword());
            }

            user.setCustomerId(null);

            boolean success;
            if (isEditMode) {
                success = userDB.updateUser(user);
            } else {
                success = userDB.addUser(user);
            }

            if (success) {
                show(isEditMode ? "User updated successfully" : "User added successfully", "green");
                loadAllUsers();
                clearForm();
            } else {
                showError(isEditMode ? "Failed to update user" : "Failed to add user");
            }

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllUsers();
        show("Data refreshed", "green");
    }

    
    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
    }

   
    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
        isEditMode = false;
    }

   
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Vehicle Rental Management System");
            stage.setResizable(true);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }


    private boolean validateInput() {
        if (txtUsername.getText().trim().isEmpty()) {
            showError("Username is required");
            return false;
        }

        if (txtFullName.getText().trim().isEmpty()) {
            showError("Full name is required");
            return false;
        }

        if (cbUserType.getValue() == null) {
            showError("Please select a user type");
            return false;
        }

        if (!isEditMode || !txtPassword.getText().isEmpty()) {
            if (txtPassword.getText().length() < 6) {
                showError("Password must be at least 6 characters");
                return false;
            }
        }

        if (!isEditMode) {
            if (userDB.usernameExists(txtUsername.getText().trim())) {
                showError("Username already exists");
                return false;
            }
        }

        return true;
    }


    private void clearForm() {
        txtUsername.clear();
        txtPassword.clear();
        txtFullName.clear();
        cbUserType.getSelectionModel().clearSelection();
        lblError.setText("");
        lblMessage.setText("");
        selectedUser = null;
        isEditMode = false;
        lblFormTitle.setText("User Details");
        btnSave.setText("Save");
    }

 
    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblMessage.setText("");
    }

    
    private void show(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        lblError.setText("");
    }
}
