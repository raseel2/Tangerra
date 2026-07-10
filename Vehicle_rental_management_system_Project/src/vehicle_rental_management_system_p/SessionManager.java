package vehicle_rental_management_system_p;


public class SessionManager {

    private static SessionManager instance;

    private String userId;
    private String username;
    private String userType;
    private String fullName;
    private String customerId; 

 
    public SessionManager() {
        this.userId = null;
        this.username = null;
        this.userType = null;
        this.fullName = null;
        this.customerId = null;
    }

  
    public void login(String userId, String username, String userType, String fullName, String customerId) {
        this.userId = userId;
        this.username = username;
        this.userType = userType;
        this.fullName = fullName;
        this.customerId = customerId;
        System.out.println("Session started for user: " + username + " (" + userType + ")");
    }

  
    public void logout() {
        System.out.println("Session ended for user: " + username);
        this.userId = null;
        this.username = null;
        this.userType = null;
        this.fullName = null;
        this.customerId = null;
    }

  
    public boolean isLoggedIn() {
        return userId != null;
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

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCustomerId() {
        return customerId;
    }

  
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
}
