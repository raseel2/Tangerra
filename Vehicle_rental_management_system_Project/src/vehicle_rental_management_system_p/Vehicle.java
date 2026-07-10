package vehicle_rental_management_system_p;

public class Vehicle implements Identifiable {

    private String vehicleId, vehicleType, brand, model;
    private int year;
    private double dailyRate;
    private char isAvailable;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, String vehicleType, String brand, String model, int year, double dailyRate, char isAvailable) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.dailyRate = dailyRate;
        this.isAvailable = isAvailable;
    }

    public Vehicle(String vehicleType, String brand, String model, int year, double dailyRate, char isAvailable) {
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.dailyRate = dailyRate;
        this.isAvailable = isAvailable;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public char getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(char isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isAvailableForRent() {
        return isAvailable == 'Y';
    }

    public String getAvailabilityStatus() {
        return isAvailable == 'Y' ? "Available" : "Rented";
    }

    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }

    @Override
    public String getId() {
        return vehicleId;
    }

    @Override
    public void setId(String id) {
        this.vehicleId = id;
    }

    @Override
    public String toString() {
        return "Vehicle{vehicleId='" + vehicleId + "', vehicleType='" + vehicleType + "', brand='" + brand + "', model='" + model + "', year=" + year + ", dailyRate=" + dailyRate + ", isAvailable=" + isAvailable + "}";
    }
}
