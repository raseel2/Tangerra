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
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;


public class CustomerBookingController {

    @FXML
    private Button btnBack;
    @FXML
    private Label lblCustomerName;

    @FXML
    private ComboBox<String> cbVehicleType;
    @FXML
    private TextField txtMaxPrice;
    @FXML
    private Button btnFilter;
    @FXML
    private Button btnClearFilter;

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
    private Label lblVehicleCount; 
    @FXML
    private Label lblSelectedVehicle;
    @FXML
    private Label lblDailyRate;
    @FXML
    private DatePicker dpPickupDate;
    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private Label lblRentalDays;
    @FXML
    private Label lblRateDisplay;
    @FXML
    private Label lblTotalCost;
    @FXML
    private Button btnCalculate;
    @FXML
    private Button btnBookNow;
    @FXML
    private Button btnClearSelection;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblError;

    private SessionManager session;
    private VehicleDatabase vehicleDB;
    private RentalDatabase rentalDB;
    private CustomerDatabase customerDB;

    private Vehicle vehicle;
    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        session = SessionManager.getInstance();
        vehicleDB = new VehicleDatabase();
        rentalDB = new RentalDatabase();
        customerDB = new CustomerDatabase();

        lblCustomerName.setText("Welcome " + session.getFullName());

        setupTable();

        ObservableList<String> types = FXCollections.observableArrayList(
                "All Types", "Car", "SUV", "Truck", "Motorcycle"
        );
        cbVehicleType.setItems(types);
        cbVehicleType.setValue("All Types");

        dpPickupDate.setValue(LocalDate.now());
        dpReturnDate.setValue(LocalDate.now().plusDays(1));

        dpPickupDate.setOnAction(e -> calculatePrice());
        dpReturnDate.setOnAction(e -> calculatePrice());

        loadAvailableVehicles();

        btnBookNow.setDisable(true);
    }

    private void setupTable() {
        colVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colDailyRate.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));

        tableVehicles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleVehicleSelection();
            }
        });
    }

    private void loadAvailableVehicles() {
        ArrayList<Vehicle> vehicles = vehicleDB.getAvailableVehicles();
        vehicleList = FXCollections.observableArrayList(vehicles);
        tableVehicles.setItems(vehicleList);
        lblVehicleCount.setText(vehicles.size() + " vehicles available");
    }

    private void handleVehicleSelection() {
        vehicle = tableVehicles.getSelectionModel().getSelectedItem();
        if (vehicle != null) {
            lblSelectedVehicle.setText(vehicle.getBrand() + " " + vehicle.getModel()
                    + " (" + vehicle.getYear() + ")");
            lblDailyRate.setText("Daily Rate: " + vehicle.getDailyRate() + " SAR");
            lblRateDisplay.setText(vehicle.getDailyRate() + " SAR");
            calculatePrice();
            btnBookNow.setDisable(false);
        }
    }

    @FXML
    private void handleFilter(ActionEvent event) {
        String type = cbVehicleType.getValue();
        String maxPriceText = txtMaxPrice.getText().trim();

        ArrayList<Vehicle> allVehicles = vehicleDB.getAvailableVehicles();
        ArrayList<Vehicle> filtered = new ArrayList<>();

        for (Vehicle v : allVehicles) {
            boolean matchesType = type.equals("All Types") || v.getVehicleType().equals(type);
            boolean matchesPrice = true;

            if (!maxPriceText.isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceText);
                    matchesPrice = v.getDailyRate() <= maxPrice;
                } catch (NumberFormatException e) {
                    matchesPrice = true;
                }
            }

            if (matchesType && matchesPrice) {
                filtered.add(v);
            }
        }

        vehicleList = FXCollections.observableArrayList(filtered);
        tableVehicles.setItems(vehicleList);
        lblVehicleCount.setText(filtered.size() + " vehicles found");
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
        cbVehicleType.setValue("All Types");
        txtMaxPrice.clear();
        loadAvailableVehicles();
    }

    @FXML
    private void handleCalculate(ActionEvent event) {
        calculatePrice();
    }

    private void calculatePrice() {
        if (vehicle == null) {
            lblRentalDays.setText("0 days");
            lblTotalCost.setText("0 SAR");
            return;
        }

        LocalDate pickup = dpPickupDate.getValue();
        LocalDate returnDate = dpReturnDate.getValue();

        if (pickup == null || returnDate == null) {
            lblRentalDays.setText("0 days");
            lblTotalCost.setText("0 SAR");
            return;
        }

        if (returnDate.isBefore(pickup)) {
            showError("Return date must be after pickup date");
            lblRentalDays.setText("0 days");
            lblTotalCost.setText("0 SAR");
            return;
        }

        long days = ChronoUnit.DAYS.between(pickup, returnDate);
        if (days == 0) {
            days = 1;
        }
        double totalCost = days * vehicle.getDailyRate();

        lblRentalDays.setText(days + " day" + (days > 1 ? "s" : ""));
        lblTotalCost.setText(String.format("%.2f SAR", totalCost));

        clearMessages();
    }

    @FXML
    private void handleBookNow(ActionEvent event) {
        if (!validateBooking()) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Booking");
        confirm.setHeaderText("Confirm Your Reservation");
        confirm.setContentText(
                "Vehicle: " + vehicle.getBrand() + " " + vehicle.getModel() + "\n"
                + "Pickup: " + dpPickupDate.getValue() + "\n"
                + "Return: " + dpReturnDate.getValue() + "\n"
                + "Total Cost: " + lblTotalCost.getText() + "\n\n"
                + "Proceed with booking?"
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            createBooking();
        }
    }

    private boolean validateBooking() {
        if (vehicle == null) {
            showError("Please select a vehicle");
            return false;
        }

        if (dpPickupDate.getValue() == null) {
            showError("Please select pickup date");
            return false;
        }

        if (dpReturnDate.getValue() == null) {
            showError("Please select return date");
            return false;
        }

        if (dpPickupDate.getValue().isBefore(LocalDate.now())) {
            showError("Pickup date cannot be in the past");
            return false;
        }

        if (dpReturnDate.getValue().isBefore(dpPickupDate.getValue())) {
            showError("Return date must be after pickup date");
            return false;
        }

        return true;
    }

    private void createBooking() {
        try {
            if (vehicle == null) {
                showError("Vehicle selection was lost. Please select a vehicle again.");
                return;
            }

            Rental rental = new Rental();
            rental.setCustomerId(session.getCustomerId());
            rental.setVehicleId(vehicle.getVehicleId());
            rental.setRentalDate(Date.valueOf(dpPickupDate.getValue()));
            rental.setExpectedReturnDate(Date.valueOf(dpReturnDate.getValue()));
            rental.setStatus("ACTIVE");

            boolean success = rentalDB.addRental(rental);

            if (success) {
                String vehicleInfo = vehicle.getBrand() + " " + vehicle.getModel();
                showSuccess("Booking confirmed successfully! Rental ID: " + rental.getRentalId());
                btnBookNow.setDisable(true);

                loadAvailableVehicles();
                handleClearSelection(null);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Booking Confirmed");
                info.setHeaderText("Your vehicle is reserved!");
                info.setContentText(
                        "Booking Details:\n"
                        + "Vehicle: " + vehicleInfo + "\n"
                        + "Pickup: " + dpPickupDate.getValue() + "\n"
                        + "Return: " + dpReturnDate.getValue() + "\n"
                        + "Total: " + lblTotalCost.getText() + "\n\n"
                        + "Please pick up your vehicle on the scheduled date.\n"
                        + "Thank you for choosing Tangerra!"
                );
                info.showAndWait();
            } else {
                showError("Failed to create booking. Please try again.");
            }

        } catch (Exception e) {
            showError("Error creating booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearSelection(ActionEvent event) {
        vehicle = null;
        tableVehicles.getSelectionModel().clearSelection();
        lblSelectedVehicle.setText("Please select a vehicle from the table");
        lblDailyRate.setText("Daily Rate: -");
        lblRateDisplay.setText("0 SAR");
        lblRentalDays.setText("0 days");
        lblTotalCost.setText("0 SAR");
        btnBookNow.setDisable(true);
        clearMessages();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Tangerra Vehicle Rental");
            stage.setResizable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblMessage.setText("");
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblError.setText("");
    }

    private void clearMessages() {
        lblError.setText("");
        lblMessage.setText("");
    }
}
