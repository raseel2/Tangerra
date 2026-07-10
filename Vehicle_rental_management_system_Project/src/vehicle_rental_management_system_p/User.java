package vehicle_rental_management_system_p;

public class User implements Identifiable {

    private String userId, username, password, userType, fullName, customerId;

    public User() {
    }

    public User(String userId, String username, String password, String userType, String fullName, String customerId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.fullName = fullName;
        this.customerId = customerId;
    }

    public User(String username, String password, String userType, String fullName, String customerId) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.fullName = fullName;
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equals(userType);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(userType);
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public void setId(String id) {
        this.userId = id;
    }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', username='" + username + "', userType='" + userType + "', fullName='" + fullName + "', customerId='" + customerId + "'}";
    }
}
