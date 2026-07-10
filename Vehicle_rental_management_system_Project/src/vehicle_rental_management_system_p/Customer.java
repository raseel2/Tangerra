package vehicle_rental_management_system_p;

import java.sql.Date;

public class Customer implements Identifiable {

    private String customerId, name, phone, email, licenseNumber;
    private Date registrationDate;

    public Customer() {
    }

    public Customer(String customerId, String name, String phone, String email, String licenseNumber, Date registrationDate) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.licenseNumber = licenseNumber;
        this.registrationDate = registrationDate;
    }

    public Customer(String name, String phone, String email, String licenseNumber, Date registrationDate) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.licenseNumber = licenseNumber;
        this.registrationDate = registrationDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String getId() {
        return customerId;
    }

    @Override
    public void setId(String id) {
        this.customerId = id;
    }

    @Override
    public String toString() {
        return "Customer{customerId='" + customerId + "', name='" + name + "', phone='" + phone + "', email='" + email + "', licenseNumber='" + licenseNumber + "', registrationDate=" + registrationDate + "}";
    }
}
