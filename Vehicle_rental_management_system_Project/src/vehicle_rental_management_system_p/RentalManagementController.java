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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class RentalManagementController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNewRental;
    @FXML
    private Button btnReturnVehicle;
    @FXML
    private Button btnViewDetails;
    @FXML
    private ComboBox<String> cbFilterStatus;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnRefresh;

    @FXML
    private TableView<Rental> tableRentals;
    @FXML
    private TableColumn<Rental, String> colRentalId;
    @FXML
    private TableColumn<Rental, String> colCustomer;
    @FXML
    private TableColumn<Rental, String> colVehicle;
    @FXML
    private TableColumn<Rental, Date> colRentalDate;
    @FXML
    private TableColumn<Rental, Date> colExpectedReturn;
    @FXML
    private TableColumn<Rental, Date> colActualReturn;
    @FXML
    private TableColumn<Rental, Double> colTotalCost;
    @FXML
    private TableColumn<Rental, String> colStatus;
    @FXML
    private Label lblTotalRentals;
    @FXML
    private Label lblMessage;

    @FXML
    private Label lblFormTitle;
    @FXML
    private GridPane gpNewRental;
    @FXML
    private GridPane gpReturnVehicle;
    @FXML
    private TextField txtRentalId;
    @FXML
    private ComboBox<String> cbCustomer;
    @FXML
    private ComboBox<String> cbVehicle;
    @FXML
    private DatePicker dpRentalDate;
    @FXML
    private DatePicker dpExpectedReturn;
    @FXML
    private TextField txtDailyRate;
    @FXML
    private TextField txtEstimatedDays;
    @FXML
    private TextField txtEstimatedCost;

    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private TextField txtActualDays;
    @FXML
    private TextField txtLateDays;
    @FXML
    private TextField txtTotalCost;

    @FXML
    private Button btnSave;
    @FXML
    private Button btnCalculate;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblError;

    private RentalDatabase rentalDB;
    private CustomerDatabase customerDB;
    private VehicleDatabase vehicleDB;
    private SessionManager session;

    private ObservableList<Rental> rentalList;
    private ArrayList<Customer> customersList;
    private ArrayList<Vehicle> availableVehiclesList;
    private Rental rental;
    private boolean isReturnMode = false;

    @FXML
    public void initialize() {
        rentalDB = new RentalDatabase();
        customerDB = new CustomerDatabase();
        vehicleDB = new VehicleDatabase();
        session = SessionManager.getInstance();

        setupTableColumns();

        loadAllRentals();
        loadCustomers();
        loadAvailableVehicles();

        setupFilterComboBox();

        dpRentalDate.setValue(LocalDate.now());
        dpReturnDate.setValue(LocalDate.now());

        setupListeners();

        configureUIByRole();

        setFormMode("new");
    }

    private void setupTableColumns() {
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        colRentalDate.setCellValueFactory(new PropertyValueFactory<>("rentalDate"));
        colExpectedReturn.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colActualReturn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableRentals.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleTableSelection();
            }
        });
    }

    private void loadAllRentals() {
        ArrayList<Rental> rentals = rentalDB.getRentals();
        rentalList = FXCollections.observableArrayList(rentals);
        tableRentals.setItems(rentalList);
        lblTotalRentals.setText("Total: " + rentals.size());
    }

    private void loadCustomers() {
        customersList = customerDB.getCustomers();
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        for (Customer customer : customersList) {
            customerNames.add(customer.getCustomerId() + " - " + customer.getName());
        }
        cbCustomer.setItems(customerNames);
    }

    private void loadAvailableVehicles() {
        availableVehiclesList = vehicleDB.getAvailableVehicles();
        updateVehicleComboBox();
    }

    private void updateVehicleComboBox() {
        ObservableList<String> vehicleInfo = FXCollections.observableArrayList();
        for (Vehicle vehicle : availableVehiclesList) {
            vehicleInfo.add(vehicle.getVehicleId() + " - " + vehicle.getBrand() + " "
                    + vehicle.getModel() + " (" + vehicle.getDailyRate() + " SAR/day)");
        }
        cbVehicle.setItems(vehicleInfo);
    }

    private void setupFilterComboBox() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "All Rentals", "Active", "Returned"
        );
        cbFilterStatus.setItems(filterOptions);
        cbFilterStatus.setValue("All Rentals");

        cbFilterStatus.setOnAction(event -> applyFilter());
    }

    private void setupListeners() {
        cbVehicle.setOnAction(event -> {
            String selected = cbVehicle.getValue();
            if (selected != null && !selected.isEmpty()) {
                String vehicleId = selected.split(" - ")[0];
                Vehicle vehicle = findVehicleById(vehicleId);
                if (vehicle != null) {
                    txtDailyRate.setText(String.format("%.2f SAR", vehicle.getDailyRate()));
                    calculateEstimatedCost();
                }
            }
        });

        dpRentalDate.setOnAction(event -> calculateEstimatedCost());
        dpExpectedReturn.setOnAction(event -> calculateEstimatedCost());
        dpReturnDate.setOnAction(event -> calculateActualCost());
    }

    private void configureUIByRole() {
        if (session.isCustomer()) {
            btnNewRental.setDisable(true);
            btnReturnVehicle.setDisable(true);
            btnSave.setDisable(true);
        }
    }

    private void setFormMode(String mode) {
        if (mode.equals("new")) {
            lblFormTitle.setText("New Rental");
            isReturnMode = false;

            gpNewRental.setVisible(true);
            gpNewRental.setManaged(true);
            gpReturnVehicle.setVisible(false);
            gpReturnVehicle.setManaged(false);

            cbCustomer.setDisable(false);
            cbVehicle.setDisable(false);
            dpRentalDate.setDisable(false);
            dpExpectedReturn.setDisable(false);

            dpReturnDate.setDisable(true);
            txtActualDays.setDisable(true);
            txtLateDays.setDisable(true);
            txtTotalCost.setDisable(true);

            btnSave.setText("Save Rental");
            btnCalculate.setDisable(true);

        } else if (mode.equals("return")) {
            lblFormTitle.setText("Return Vehicle");
            isReturnMode = true;

            gpNewRental.setVisible(false);
            gpNewRental.setManaged(false);
            gpReturnVehicle.setVisible(true);
            gpReturnVehicle.setManaged(true);

            cbCustomer.setDisable(true);
            cbVehicle.setDisable(true);
            dpRentalDate.setDisable(true);
            dpExpectedReturn.setDisable(true);

            dpReturnDate.setDisable(false);
            txtActualDays.setDisable(false);
            txtLateDays.setDisable(false);
            txtTotalCost.setDisable(false);

            btnSave.setText("Process Return");
            btnCalculate.setDisable(false);
        }
    }

    private void handleTableSelection() {
        rental = tableRentals.getSelectionModel().getSelectedItem();
        if (rental != null) {
            populateFormWithRental(rental);
        }
    }

    private void populateFormWithRental(Rental rental) {
        txtRentalId.setText(rental.getRentalId());

        for (int i = 0; i < cbCustomer.getItems().size(); i++) {
            if (cbCustomer.getItems().get(i).startsWith(rental.getCustomerId())) {
                cbCustomer.getSelectionModel().select(i);
                break;
            }
        }

        Vehicle vehicle = vehicleDB.getVehicleById(rental.getVehicleId());
        if (vehicle != null) {
            String vehicleDisplay = vehicle.getVehicleId() + " - " + vehicle.getBrand() + " "
                    + vehicle.getModel() + " (" + vehicle.getDailyRate() + " SAR/day)";
            cbVehicle.setValue(vehicleDisplay);
            txtDailyRate.setText(String.format("%.2f SAR", vehicle.getDailyRate()));
        }

        if (rental.getRentalDate() != null) {
            dpRentalDate.setValue(rental.getRentalDate().toLocalDate());
        }
        if (rental.getExpectedReturnDate() != null) {
            dpExpectedReturn.setValue(rental.getExpectedReturnDate().toLocalDate());
        }
        if (rental.getActualReturnDate() != null) {
            dpReturnDate.setValue(rental.getActualReturnDate().toLocalDate());
        }

        calculateEstimatedCost();
        if (rental.getActualReturnDate() != null) {
            calculateActualCost();
        }
    }

    private void calculateEstimatedCost() {
        LocalDate rentalDate = dpRentalDate.getValue();
        LocalDate expectedReturn = dpExpectedReturn.getValue();
        String dailyRateText = txtDailyRate.getText().replace("SAR", "").trim();

        if (rentalDate != null && expectedReturn != null && !dailyRateText.isEmpty()) {
            try {
                long days = (java.sql.Date.valueOf(expectedReturn).getTime() - java.sql.Date.valueOf(rentalDate).getTime()) / (1000 * 60 * 60 * 24) + 1;
                double dailyRate = Double.parseDouble(dailyRateText);
                double estimatedCost = days * dailyRate;

                txtEstimatedDays.setText(String.valueOf(days));
                txtEstimatedCost.setText(String.format("%.2f SAR", estimatedCost));
            } catch (NumberFormatException e) {
                txtEstimatedCost.setText("0.00 SAR");
            }
        }
    }

    private void calculateActualCost() {
        if (rental == null) {
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue();
        if (returnDate == null) {
            return;
        }

        try {
            Date sqlReturnDate = Date.valueOf(returnDate);
            double cost = rentalDB.calculateRentalCost(rental.getRentalId(), sqlReturnDate);

            LocalDate rentalDate = rental.getRentalDate().toLocalDate();
            LocalDate expectedReturn = rental.getExpectedReturnDate().toLocalDate();

            long actualDays = (java.sql.Date.valueOf(returnDate).getTime() - java.sql.Date.valueOf(rentalDate).getTime()) / (1000 * 60 * 60 * 24) + 1;
            long expectedDays = (java.sql.Date.valueOf(expectedReturn).getTime() - java.sql.Date.valueOf(rentalDate).getTime()) / (1000 * 60 * 60 * 24) + 1;
            long lateDays = Math.max(0, actualDays - expectedDays);

            txtActualDays.setText(String.valueOf(actualDays));
            txtLateDays.setText(String.valueOf(lateDays));
            txtTotalCost.setText(String.format("%.2f SAR", cost));

            if (lateDays > 0) {
                txtLateDays.setStyle("-fx-background-color: #f8d7da; -fx-font-weight: bold;");
                lblError.setText("⚠️ Late Fee Applied: " + lateDays + " days × 1.5× daily rate");
                lblError.setStyle("-fx-text-fill: #C74D23;");
            } else {
                txtLateDays.setStyle("-fx-background-color: #d4edda;");
                lblError.setText("");
            }

        } catch (Exception e) {
            showError("Error calculating cost: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewRental(ActionEvent event) {
        clearForm();
        setFormMode("new");
        loadAvailableVehicles();
        show("Fill in the form to create a new rental", "blue");
    }

    @FXML
    private void handleReturnVehicle(ActionEvent event) {
        Rental selected = tableRentals.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a rental to return");
            return;
        }

        if (!"ACTIVE".equals(selected.getStatus())) {
            showError("Only active rentals can be returned");
            return;
        }

        rental = selected;
        populateFormWithRental(selected);
        setFormMode("return");
        dpReturnDate.setValue(LocalDate.now());
        calculateActualCost();
        show("Review the cost and click 'Process Return'", "blue");
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        Rental selected = tableRentals.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a rental to view");
            return;
        }

        String details = String.format(
                "Rental Details:\n\n"
                + "Rental ID: %s\n"
                + "Customer: %s\n"
                + "Vehicle: %s\n"
                + "Rental Date: %s\n"
                + "Expected Return: %s\n"
                + "Actual Return: %s\n"
                + "Total Cost: %.2f SAR\n"
                + "Status: %s",
                selected.getRentalId(),
                selected.getCustomerName(),
                selected.getVehicleInfo(),
                selected.getRentalDate(),
                selected.getExpectedReturnDate(),
                selected.getActualReturnDate() != null ? selected.getActualReturnDate() : "Not returned yet",
                selected.getTotalCost(),
                selected.getStatus()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rental Details");
        alert.setHeaderText("Rental Information");
        alert.setContentText(details);
        alert.showAndWait();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isReturnMode) {
            processReturn();
        } else {
            saveNewRental();
        }
    }

    private void saveNewRental() {
        if (!validateRentalInput()) {
            return;
        }

        try {
            Rental rental = new Rental();
            rental.setRentalId(null);

            String customerSelection = cbCustomer.getValue();
            String customerId = customerSelection.split(" - ")[0];
            rental.setCustomerId(customerId);

            String vehicleSelection = cbVehicle.getValue();
            String vehicleId = vehicleSelection.split(" - ")[0];
            rental.setVehicleId(vehicleId);

            rental.setRentalDate(Date.valueOf(dpRentalDate.getValue()));
            rental.setExpectedReturnDate(Date.valueOf(dpExpectedReturn.getValue()));
            rental.setStatus("ACTIVE");

            boolean success = rentalDB.addRental(rental);

            if (success) {
                show("Rental created successfully!", "green");
                loadAllRentals();
                loadAvailableVehicles();
                clearForm();
            } else {
                showError("Failed to create rental");
            }

        } catch (Exception e) {
            showError("Error creating rental: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processReturn() {
        if (rental == null) {
            showError("No rental selected");
            return;
        }

        if (dpReturnDate.getValue() == null) {
            showError("Please select a return date");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Return");
        confirm.setHeaderText("Process Vehicle Return");
        confirm.setContentText("Total Cost: " + txtTotalCost.getText() + "\n\nConfirm return?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Date returnDate = Date.valueOf(dpReturnDate.getValue());
                boolean success = rentalDB.returnVehicle(rental.getRentalId(), returnDate);

                if (success) {
                    show("Vehicle returned successfully!", "green");
                    loadAllRentals();
                    loadAvailableVehicles();
                    clearForm();
                    setFormMode("new");
                } else {
                    showError("Failed to process return");
                }

            } catch (Exception e) {
                showError("Error processing return: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCalculate(ActionEvent event) {
        if (isReturnMode) {
            calculateActualCost();
        } else {
            calculateEstimatedCost();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllRentals();
            return;
        }

        ArrayList<Rental> filtered = new ArrayList<>();
        for (Rental rental : rentalList) {
            if (rental.getRentalId().toLowerCase().contains(keyword.toLowerCase())
                    || rental.getCustomerName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(rental);
            }
        }

        rentalList = FXCollections.observableArrayList(filtered);
        tableRentals.setItems(rentalList);
        lblTotalRentals.setText("Found: " + filtered.size());
    }

    private void applyFilter() {
        String filter = cbFilterStatus.getValue();
        ArrayList<Rental> rentals;

        switch (filter) {
            case "Active":
                rentals = rentalDB.getActiveRentals();
                break;
            case "Returned":
                ArrayList<Rental> all = rentalDB.getRentals();
                rentals = new ArrayList<>();
                for (Rental r : all) {
                    if ("RETURNED".equals(r.getStatus())) {
                        rentals.add(r);
                    }
                }
                break;
            default:
                rentals = rentalDB.getRentals();
        }

        rentalList = FXCollections.observableArrayList(rentals);
        tableRentals.setItems(rentalList);
        lblTotalRentals.setText("Total: " + rentals.size());
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllRentals();
        loadCustomers();
        loadAvailableVehicles();
        cbFilterStatus.setValue("All Rentals");
        txtSearch.clear();
        show("Data refreshed", "green");
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
        setFormMode("new");
        rental = null;
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

    private boolean validateRentalInput() {
        if (cbCustomer.getValue() == null || cbCustomer.getValue().isEmpty()) {
            showError("Please select a customer");
            return false;
        }

        if (cbVehicle.getValue() == null || cbVehicle.getValue().isEmpty()) {
            showError("Please select a vehicle");
            return false;
        }

        if (dpRentalDate.getValue() == null) {
            showError("Please select a rental date");
            return false;
        }

        if (dpExpectedReturn.getValue() == null) {
            showError("Please select an expected return date");
            return false;
        }

        if (dpExpectedReturn.getValue().isBefore(dpRentalDate.getValue())) {
            showError("Expected return date must be after rental date");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtRentalId.clear();
        cbCustomer.getSelectionModel().clearSelection();
        cbVehicle.getSelectionModel().clearSelection();
        dpRentalDate.setValue(LocalDate.now());
        dpExpectedReturn.setValue(null);
        dpReturnDate.setValue(LocalDate.now());
        txtDailyRate.clear();
        txtEstimatedDays.clear();
        txtEstimatedCost.clear();
        txtActualDays.clear();
        txtLateDays.clear();
        txtTotalCost.clear();
        lblError.setText("");
        lblMessage.setText("");
        rental = null;
    }

    private Vehicle findVehicleById(String vehicleId) {
        for (Vehicle vehicle : availableVehiclesList) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                return vehicle;
            }
        }
        return vehicleDB.getVehicleById(vehicleId);
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
