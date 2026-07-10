package vehicle_rental_management_system_p;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.ArrayList;


public class CustomerManagementController {
    @FXML private TableView<Customer> tableCustomers;
    @FXML private TableColumn<Customer, String> colCustomerId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colLicense;
    @FXML private TableColumn<Customer, java.sql.Date> colRegDate;
    @FXML private TextField txtCustomerId, txtName, txtPhone, txtEmail, txtLicense, txtSearch;
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnSave, btnCancel;
    @FXML private Label lblFormTitle, lblStatus, lblTableTitle, lblRecordCount;
    
    private CustomerDatabase customerDB;
    private ObservableList<Customer> customerList;
    private Customer customer;
    private boolean isEditMode = false;
    private SessionManager session;

    @FXML
    public void initialize() {
        session = SessionManager.getInstance();
        customerDB = new CustomerDatabase();
        customerList = FXCollections.observableArrayList();
        setupTableColumns();
        loadAllCustomers();
        tableCustomers.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newVal) -> handleTableSelection(newVal)
        );
        if (session.isEmployee()) btnDelete.setVisible(false);
    }

    private void setupTableColumns() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colRegDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        tableCustomers.setItems(customerList);
    }

    private void loadAllCustomers() {
        try {
            ArrayList<Customer> customers = customerDB.getCustomers();
            customerList.clear();
            customerList.addAll(customers);
            updateRecordCount();
            lblTableTitle.setText("All Customers");
            lblStatus.setText("");
        } catch (Exception e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    private void handleTableSelection(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            populateForm(customer);
            btnUpdate.setDisable(false);
            btnDelete.setDisable(false);
        } else {
            clearSelection();
        }
    }

    private void populateForm(Customer customer) {
        txtCustomerId.setText(customer.getCustomerId());
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        txtLicense.setText(customer.getLicenseNumber());
    }

    private void clearForm() {
        txtCustomerId.clear();
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtLicense.clear();
        lblStatus.setText("");
    }

    private void clearSelection() {
        tableCustomers.getSelectionModel().clearSelection();
        customer = null;
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void updateRecordCount() {
        lblRecordCount.setText("Total: " + customerList.size() + " customers");
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        isEditMode = false;
        clearForm();
        clearSelection();
        lblFormTitle.setText("Add New Customer");
        lblStatus.setText("Fill in customer details and click Save");
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (customer == null) {
            showError("Please select a customer to update");
            return;
        }
        isEditMode = true;
        lblFormTitle.setText("Update Customer");
        lblStatus.setText("Modify customer details and click Save");
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (customer == null) {
            showError("Please select a customer to delete");
            return;
        }

        if (customerDB.hasActiveRentals(customer.getCustomerId())) {
            showError("Cannot delete customer with active rentals. Please complete or cancel all active rentals first.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Delete customer: " + customer.getName() + "?\n\nNote: All associated rental records will also be deleted.");
        alert.showAndWait();
        if (alert.getResult() != null && alert.getResult() == ButtonType.OK) {
            try {
                if (customerDB.deleteCustomer(customer.getCustomerId())) {
                    showSuccess("Customer and associated records deleted successfully");
                    loadAllCustomers();
                    clearForm();
                    clearSelection();
                } else {
                    showError("Failed to delete customer");
                }
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) return;
        try {
            Customer newCustomer = new Customer();
            newCustomer.setName(txtName.getText().trim());
            newCustomer.setPhone(txtPhone.getText().trim());
            newCustomer.setEmail(txtEmail.getText().trim());
            newCustomer.setLicenseNumber(txtLicense.getText().trim());

            boolean success;
            if (isEditMode) {
                newCustomer.setCustomerId(this.customer.getCustomerId());
                newCustomer.setRegistrationDate(this.customer.getRegistrationDate());
                success = customerDB.updateCustomer(newCustomer);
                if (success) showSuccess("Customer updated successfully");
                else showError("Failed to update customer");
            } else {
                success = customerDB.addCustomer(newCustomer);
                if (success) showSuccess("Customer added successfully");
                else showError("Failed to add customer");
            }
            if (success) {
                loadAllCustomers();
                handleCancel(event);
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (txtName.getText().trim().isEmpty()) errors.append("- Name is required\n");
        if (txtPhone.getText().trim().isEmpty()) errors.append("- Phone is required\n");
        if (txtEmail.getText().trim().isEmpty()) errors.append("- Email is required\n");
        else if (!txtEmail.getText().contains("@")) errors.append("- Invalid email format\n");
        if (txtLicense.getText().trim().isEmpty()) errors.append("- License number is required\n");
        
        if (errors.length() > 0) {
            showError("Please fix:\n" + errors.toString());
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
        clearSelection();
        isEditMode = false;
        lblFormTitle.setText("Customer Details");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            showError("Enter search keyword");
            return;
        }
        try {
            ArrayList<Customer> results = customerDB.searchCustomers(keyword);
            customerList.clear();
            customerList.addAll(results);
            updateRecordCount();
            lblTableTitle.setText("Search Results: " + keyword);
            lblStatus.setText("Found " + results.size() + " customers");
        } catch (Exception e) {
            showError("Search error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAllCustomers();
        lblStatus.setText("Data refreshed");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Vehicle Rental Management System");
            stage.setResizable(true);
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void showSuccess(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: green;");
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: red;");
    }
}
