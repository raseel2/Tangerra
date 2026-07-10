package vehicle_rental_management_system_p;

import java.sql.*;
import java.util.ArrayList;

public class UserDatabase {

    private DatabaseConnection db;

    public UserDatabase() {
        this.db = DatabaseConnection.getInstance();
    }

    public User authenticate(String username, String password) {
        String query = "SELECT * FROM USERS WHERE username = ? AND password = ?";
        User user = null;

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("full_name"),
                        rs.getString("customer_id")
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    public User getUserById(String userId) {
        String query = "SELECT * FROM USERS WHERE user_id = ?";
        User user = null;

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("full_name"),
                        rs.getString("customer_id")
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM USERS WHERE username = ?";
        User user = null;

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("full_name"),
                        rs.getString("customer_id")
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    public ArrayList<User> getUsers() {
        String query = "SELECT * FROM USERS ORDER BY user_id";
        ArrayList<User> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("full_name"),
                        rs.getString("customer_id")
                );
                list.add(user);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<User> getUsersByType(String userType) {
        String query = "SELECT * FROM USERS WHERE user_type = ? ORDER BY user_id";
        ArrayList<User> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userType);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("user_type"),
                        rs.getString("full_name"),
                        rs.getString("customer_id")
                );
                list.add(user);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean addUser(User user) {
        String query = "INSERT INTO USERS (user_id, username, password, user_type, full_name, customer_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                user.setUserId(generateNextUserId());
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getUserType());
            ps.setString(5, user.getFullName());
            ps.setString(6, user.getCustomerId());

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateUser(User user) {
        String query = "UPDATE USERS SET username = ?, password = ?, user_type = ?, "
                + "full_name = ?, customer_id = ? WHERE user_id = ?";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserType());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getCustomerId());
            ps.setString(6, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String query = "DELETE FROM USERS WHERE user_id = ?";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM USERS WHERE username = ?";

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count > 0;
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextUserId() {
        String query = "SELECT user_seq.NEXTVAL FROM dual";

        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                int nextVal = rs.getInt(1);
                rs.close();
                stmt.close();
                return String.format("U%03d", nextVal);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error generating user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return "U001";
    }
}
