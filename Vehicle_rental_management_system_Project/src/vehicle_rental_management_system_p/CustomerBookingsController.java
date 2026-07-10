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
import java.util.ArrayList;


public class CustomerBookingsController {

    @FXML private Label lblCustomerName;
    @FXML private Button btnBack;

    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private Button btnRefresh;
    @FXML private Button btnNewBooking;
    @FXML private Label lblActiveCount;
    @FXML private Label lblTotalCount;

    @FXML private TableView<Rental> tableRentals;
    @FXML private TableColumn<Rental, String> colRentalId;
    @FXML private TableColumn<Rental, String> colVehicle;
    @FXML private TableColumn<Rental, Date> colRentalDate;
    @FXML private TableColumn<Rental, Date> colExpectedReturn;
    @FXML private TableColumn<Rental, Date> colActualReturn;
    @FXML private TableColumn<Rental, Double> colTotalCost;
    @FXML private TableColumn<Rental, String> colStatus;
    @FXML private Label lblTotalRentals;
    @FXML private Label lblMessage;

    @FXML private Label lblRentalId;
    @FXML private Label lblVehicleDetails;
    @FXML private Label lblDateDetails;
    @FXML private Label lblCostDetails;
    @FXML private Label lblStatusDetails;
    @FXML private Button btnViewDetails;

    private RentalDatabase rentalDB;
    private SessionManager session;
    private ObservableList<Rental> rentalList;
    private Rental rental;

   
    @FXML
    public void initialize() {
        rentalDB = new RentalDatabase();
        session = SessionManager.getInstance();

        lblCustomerName.setText(session.getFullName());

        setupTableColumns();

        setupFilterComboBox();

        loadCustomerRentals();

        tableRentals.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleTableSelection();
            }
        });
    }

   
    private void setupTableColumns() {
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        colRentalDate.setCellValueFactory(new PropertyValueFactory<>("rentalDate"));
        colExpectedReturn.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colActualReturn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<Rental, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ACTIVE":
                            setStyle("-fx-text-fill: #EA5B2B; -fx-font-weight: bold;");
                            break;
                        case "RETURNED":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "OVERDUE":
                            setStyle("-fx-text-fill: #C74D23; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
    }

  
    private void setupFilterComboBox() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
            "All Bookings", "Active", "Returned"
        );
        cbFilterStatus.setItems(filterOptions);
        cbFilterStatus.setValue("All Bookings");
        cbFilterStatus.setOnAction(event -> applyFilter());
    }

   
    private void loadCustomerRentals() {
        String customerId = session.getCustomerId();
        if (customerId == null || customerId.isEmpty()) {
            show("No customer account linked to this user", "red");
            return;
        }

        ArrayList<Rental> rentals = rentalDB.getRentalsByCustomer(customerId);
        rentalList = FXCollections.observableArrayList(rentals);
        tableRentals.setItems(rentalList);

        updateStatistics(rentals);

        lblTotalRentals.setText("Total: " + rentals.size());
        show("Loaded " + rentals.size() + " booking(s)", "green");
    }

   
    private void updateStatistics(ArrayList<Rental> rentals) {
        long activeCount = 0;
        for (Rental r : rentals) {
            if ("ACTIVE".equals(r.getStatus())) {
                activeCount++;
            }
        }

        lblActiveCount.setText(String.valueOf(activeCount));
        lblTotalCount.setText(String.valueOf(rentals.size()));
    }

  
    private void applyFilter() {
        String filter = cbFilterStatus.getValue();
        String customerId = session.getCustomerId();

        ArrayList<Rental> allRentals = rentalDB.getRentalsByCustomer(customerId);
        ArrayList<Rental> filtered = new ArrayList<>();

        switch (filter) {
            case "Active":
                for (Rental r : allRentals) {
                    if ("ACTIVE".equals(r.getStatus())) {
                        filtered.add(r);
                    }
                }
                break;
            case "Returned":
                for (Rental r : allRentals) {
                    if ("RETURNED".equals(r.getStatus())) {
                        filtered.add(r);
                    }
                }
                break;
            default:
                filtered = allRentals;
        }

        rentalList = FXCollections.observableArrayList(filtered);
        tableRentals.setItems(rentalList);
        lblTotalRentals.setText("Showing: " + filtered.size());
    }

   
    private void handleTableSelection() {
        rental = tableRentals.getSelectionModel().getSelectedItem();
        if (rental != null) {
            updateDetailsPane(rental);
        }
    }

   
    private void updateDetailsPane(Rental rental) {
        lblRentalId.setText("Booking #" + rental.getRentalId());
        lblVehicleDetails.setText("Vehicle: " + rental.getVehicleInfo());

        String dateInfo = String.format("Pickup: %s\nReturn: %s\nActual: %s",
            rental.getRentalDate(),
            rental.getExpectedReturnDate(),
            rental.getActualReturnDate() != null ? rental.getActualReturnDate() : "Not returned yet");
        lblDateDetails.setText(dateInfo);

        lblCostDetails.setText(String.format("Total: %.2f SAR", rental.getTotalCost()));

        String statusText = "Status: " + rental.getStatus();
        lblStatusDetails.setText(statusText);

        switch (rental.getStatus()) {
            case "ACTIVE":
                lblStatusDetails.setStyle("-fx-text-fill: #EA5B2B; -fx-font-weight: bold;");
                break;
            case "RETURNED":
                lblStatusDetails.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case "OVERDUE":
                lblStatusDetails.setStyle("-fx-text-fill: #C74D23; -fx-font-weight: bold;");
                break;
        }
    }

   
    @FXML
    private void handleViewDetails(ActionEvent event) {
        if (rental == null) {
            show("Please select a booking to view details", "orange");
            return;
        }

        String details = String.format(
            "Booking Details:\n\n" +
            "Booking ID: %s\n" +
            "Vehicle: %s\n" +
            "Pickup Date: %s\n" +
            "Expected Return: %s\n" +
            "Actual Return: %s\n" +
            "Total Cost: %.2f SAR\n" +
            "Status: %s",
            rental.getRentalId(),
            rental.getVehicleInfo(),
            rental.getRentalDate(),
            rental.getExpectedReturnDate(),
            rental.getActualReturnDate() != null ? rental.getActualReturnDate() : "Not returned yet",
            rental.getTotalCost(),
            rental.getStatus()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Details");
        alert.setHeaderText("Booking #" + rental.getRentalId());
        alert.setContentText(details);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadCustomerRentals();
        cbFilterStatus.setValue("All Bookings");
    }


    @FXML
    private void handleNewBooking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/customer_booking.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnNewBooking.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Book a Vehicle - Tangerra");
        } catch (IOException e) {
            show("Error loading booking screen: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle_rental_management_system_p/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Tangerra");
            stage.setResizable(true);
        } catch (IOException e) {
            show("Error loading dashboard: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    private void show(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
}
