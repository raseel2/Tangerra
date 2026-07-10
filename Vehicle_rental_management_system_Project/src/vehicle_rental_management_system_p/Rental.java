package vehicle_rental_management_system_p;

import java.sql.Date;

public class Rental implements Identifiable {

    private String rentalId, customerId, vehicleId, status, customerName, vehicleInfo;
    private Date rentalDate, expectedReturnDate, actualReturnDate;
    private double totalCost;

    public Rental() {
    }

    public Rental(String rentalId, String customerId, String vehicleId, Date rentalDate, Date expectedReturnDate, Date actualReturnDate, double totalCost, String status) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Date getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    @Override
    public String getId() {
        return rentalId;
    }

    @Override
    public void setId(String id) {
        this.rentalId = id;
    }

    @Override
    public String toString() {
        return "Rental{rentalId='" + rentalId + "', customerId='" + customerId + "', vehicleId='" + vehicleId + "', rentalDate=" + rentalDate + ", expectedReturnDate=" + expectedReturnDate + ", actualReturnDate=" + actualReturnDate + ", totalCost=" + totalCost + ", status='" + status + "'}";
    }
}
