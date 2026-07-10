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

public class VehicleManagementController {

    @FXML
    private TableView<Vehicle> tableVehicles;
    @FXML
    private TableColumn<Vehicle, String> colVehicleId;
    @FXML
    private TableColumn<Vehicle, String> colType;
    @FXML
    private TableColumn<Vehicle, String> colBrand;
    @FXML
    private TableColumn<Vehicle, String> colModel;
    @FXML
    private TableColumn<Vehicle, Integer> colYear;
    @FXML
    private TableColumn<Vehicle, Double> colDailyRate;
    @FXML
    private TableColumn<Vehicle, String> colStatus;

    @FXML
    private TextField txtVehicleId;
    @FXML
    private ComboBox<String> cbVehicleType;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtYear;
    @FXML
    private TextField txtDailyRate;
    @FXML
    private ComboBox<String> cbAvailability;

    @FXML
    private ComboBox<String> cbFilterType;
    @FXML
    private ComboBox<String> cbFilterStatus;
    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnBack;

    @FXML
    private Label lblFormTitle;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblTableTitle;
    @FXML
    private Label lblRecordCount;

    private VehicleDatabase vehicleDB;
    private ObservableList<Vehicle> vehicleList;
    private Vehicle vehicle;
    private boolean isEditMode = false;
    private SessionManager session;

    @FXML
    public void initialize() {
        session = SessionManager.getInstance();
        vehicleDB = new VehicleDatabase();
        vehicleList = FXCollections.observableArrayList();

        setupTableColumns();

        setupComboBoxes();

        loadAllVehicles();

        tableVehicles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleTableSelection(newValue)
        );

        configureButtonsByRole();
    }

    private void setupTableColumns() {
        colVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colDailyRate.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));

        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getAvailabilityStatus();
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        tableVehicles.setItems(vehicleList);
    }

    private void setupComboBoxes() {
        ObservableList<String> vehicleTypes = FXCollections.observableArrayList(
                "Car", "SUV", "Truck", "Motorcycle"
        );
        cbVehicleType.setItems(vehicleTypes);
        cbFilterType.setItems(FXCollections.observableArrayList("All", "Car", "SUV", "Truck", "Motorcycle"));
        cbFilterType.setValue("All");

        ObservableList<String> availabilityOptions = FXCollections.observableArrayList(
                "Available", "Rented"
        );
        cbAvailability.setItems(availabilityOptions);
        cbFilterStatus.setItems(FXCollections.observableArrayList("All", "Available", "Rented"));
        cbFilterStatus.setValue("All");
    }

    private void configureButtonsByRole() {
        if (session.isEmployee()) {
            btnDelete.setVisible(false);
        } else if (session.isCustomer()) {
            btnAdd.setVisible(false);
            btnUpdate.setVisible(false);
            btnDelete.setVisible(false);
            btnSave.setVisible(false);
            txtBrand.setEditable(false);
            txtModel.setEditable(false);
            txtYear.setEditable(false);
            txtDailyRate.setEditable(false);
            cbVehicleType.setDisable(true);
            cbAvailability.setDisable(true);
        }
    }

    private void loadAllVehicles() {
        try {
            ArrayList<Vehicle> vehicles = vehicleDB.getVehicles();
            vehicleList.clear();
            vehicleList.addAll(vehicles);
            updateRecordCount();
            lblTableTitle.setText("All Vehicles");
            lblStatus.setText("");
        } catch (Exception e) {
            showError("Error loading vehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleTableSelection(Vehicle vehicle) {
        if (vehicle != null) {
            this.vehicle = vehicle;
            populateForm(vehicle);
            btnUpdate.setDisable(false);
            btnDelete.setDisable(false);
        } else {
            clearSelection();
        }
    }

    private void populateForm(Vehicle vehicle) {
        txtVehicleId.setText(vehicle.getVehicleId());
        cbVehicleType.setValue(vehicle.getVehicleType());
        txtBrand.setText(vehicle.getBrand());
        txtModel.setText(vehicle.getModel());
        txtYear.setText(String.valueOf(vehicle.getYear()));
        txtDailyRate.setText(String.format("%.2f", vehicle.getDailyRate()));
        cbAvailability.setValue(vehicle.getAvailabilityStatus());
    }

    private void clearForm() {
        txtVehicleId.clear();
        cbVehicleType.setValue(null);
        txtBrand.clear();
        txtModel.clear();
        txtYear.clear();
        txtDailyRate.clear();
        cbAvailability.setValue(null);
        lblStatus.setText("");
    }

    private void clearSelection() {
        tableVehicles.getSelectionModel().clearSelection();
        vehicle = null;
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void updateRecordCount() {
        lblRecordCount.setText("Total: " + vehicleList.size() + " vehicles");
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        isEditMode = false;
        clearForm();
        clearSelection();
        lblFormTitle.setText("Add New Vehicle");
        cbAvailability.setValue("Available");
        lblStatus.setText("Fill in vehicle details and click Save");
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (vehicle == null) {
            showError("Please select a vehicle to update");
            return;
        }

        isEditMode = true;
        lblFormTitle.setText("Update Vehicle");
        lblStatus.setText("Modify vehicle details and click Save");
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (vehicle == null) {
            showError("Please select a vehicle to delete");
            return;
        }

        if (vehicleDB.isVehicleRented(vehicle.getVehicleId())) {
            showError("Cannot delete vehicle that is currently rented");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Vehicle");
        confirmAlert.setContentText("Are you sure you want to delete vehicle: "
                + vehicle.getFullName() + "?");

        confirmAlert.showAndWait();
        if (confirmAlert.getResult() != null && confirmAlert.getResult() == ButtonType.OK) {
            try {
                boolean success = vehicleDB.deleteVehicle(vehicle.getVehicleId());
                if (success) {
                    showSuccess("Vehicle deleted successfully");
                    loadAllVehicles();
                    clearForm();
                    clearSelection();
                } else {
                    showError("Failed to delete vehicle");
                }
            } catch (Exception e) {
                showError("Error deleting vehicle: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleType(cbVehicleType.getValue());
            vehicle.setBrand(txtBrand.getText().trim());
            vehicle.setModel(txtModel.getText().trim());
            vehicle.setYear(Integer.parseInt(txtYear.getText().trim()));
            vehicle.setDailyRate(Double.parseDouble(txtDailyRate.getText().trim()));
            vehicle.setIsAvailable(cbAvailability.getValue().equals("Available") ? 'Y' : 'N');

            boolean success;
            if (isEditMode) {
                vehicle.setVehicleId(this.vehicle.getVehicleId());
                success = vehicleDB.updateVehicle(vehicle);
                if (success) {
                    showSuccess("Vehicle updated successfully");
                } else {
                    showError("Failed to update vehicle");
                }
            } else {
                success = vehicleDB.addVehicle(vehicle);
                if (success) {
                    showSuccess("Vehicle added successfully");
                } else {
                    showError("Failed to add vehicle");
                }
            }

            if (success) {
                loadAllVehicles();
                handleCancel(event);
            }

        } catch (Exception e) {
            showError("Error saving vehicle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (cbVehicleType.getValue() == null || cbVehicleType.getValue().isEmpty()) {
            errors.append("- Vehicle type is required\n");
        }

        if (txtBrand.getText().trim().isEmpty()) {
            errors.append("- Brand is required\n");
        }

        if (txtModel.getText().trim().isEmpty()) {
            errors.append("- Model is required\n");
        }

        try {
            int year = Integer.parseInt(txtYear.getText().trim());
            if (year < 1900 || year > 2100) {
                errors.append("- Year must be between 1900 and 2100\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Year must be a valid number\n");
        }

        try {
            double rate = Double.parseDouble(txtDailyRate.getText().trim());
            if (rate <= 0) {
                errors.append("- Daily rate must be greater than 0\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Daily rate must be a valid number\n");
        }

        if (cbAvailability.getValue() == null || cbAvailability.getValue().isEmpty()) {
            errors.append("- Availability status is required\n");
        }

        if (errors.length() > 0) {
            showError("Please fix the following errors:\n" + errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
        clearSelection();
        isEditMode = false;
        lblFormTitle.setText("Vehicle Details");
        lblStatus.setText("");
    }

    @FXML
    private void handleFilter(ActionEvent event) {
        applyFilters();
    }

    private void applyFilters() {
        try {
            ArrayList<Vehicle> filteredVehicles;
            String filterType = cbFilterType.getValue();
            String filterStatus = cbFilterStatus.getValue();

            filteredVehicles = vehicleDB.getVehicles();

            if (filterType != null && !filterType.equals("All")) {
                ArrayList<Vehicle> temp = new ArrayList<>();
                for (Vehicle v : filteredVehicles) {
                    if (v.getVehicleType().equals(filterType)) {
                        temp.add(v);
                    }
                }
                filteredVehicles = temp;
            }

            if (filterStatus != null && !filterStatus.equals("All")) {
                ArrayList<Vehicle> temp = new ArrayList<>();
                for (Vehicle v : filteredVehicles) {
                    if (filterStatus.equals("Available") && v.isAvailableForRent()) {
                        temp.add(v);
                    } else if (filterStatus.equals("Rented") && !v.isAvailableForRent()) {
                        temp.add(v);
                    }
                }
                filteredVehicles = temp;
            }

            vehicleList.clear();
            vehicleList.addAll(filteredVehicles);
            updateRecordCount();
            lblTableTitle.setText("Filtered Vehicles");

        } catch (Exception e) {
            showError("Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            showError("Please enter a search keyword");
            return;
        }

        try {
            ArrayList<Vehicle> searchResults = vehicleDB.searchVehicles(keyword);
            vehicleList.clear();
            vehicleList.addAll(searchResults);
            updateRecordCount();
            lblTableTitle.setText("Search Results for: " + keyword);
            lblStatus.setText("Found " + searchResults.size() + " matching vehicles");

        } catch (Exception e) {
            showError("Error searching vehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
        cbFilterType.setValue("All");
        cbFilterStatus.setValue("All");
        txtSearch.clear();
        loadAllVehicles();
        lblStatus.setText("Filters cleared");
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllVehicles();
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
            showError("Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        lblStatus.setText(message);
        lblStatus.setStyle("-fx-text-fill: green;");
    }

    private void showError(String message) {
        lblStatus.setText(message);
        lblStatus.setStyle("-fx-text-fill: red;");
    }
}
