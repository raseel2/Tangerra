package vehicle_rental_management_system_p;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDatabase {
    private DatabaseConnection db;

    public CustomerDatabase() {
        this.db = DatabaseConnection.getInstance();
    }

    public Customer getCustomerById(String customerId) {
        String query = "SELECT * FROM CUSTOMER WHERE customer_id = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getDate("registration_date")
                );
                rs.close();
                ps.close();
                return customer;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer getCustomerByEmail(String email) {
        String query = "SELECT * FROM CUSTOMER WHERE email = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getDate("registration_date")
                );
                rs.close();
                ps.close();
                return customer;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer getCustomerByLicense(String licenseNumber) {
        String query = "SELECT * FROM CUSTOMER WHERE license_number = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, licenseNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getDate("registration_date")
                );
                rs.close();
                ps.close();
                return customer;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Customer> getCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM CUSTOMER ORDER BY customer_id";
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getDate("registration_date")
                ));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public ArrayList<Customer> searchCustomers(String keyword) {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM CUSTOMER WHERE UPPER(name) LIKE UPPER(?) OR UPPER(email) LIKE UPPER(?) OR UPPER(phone) LIKE UPPER(?)";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getDate("registration_date")
                ));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO CUSTOMER (customer_id, name, phone, email, license_number, registration_date) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()) {
                customer.setCustomerId(generateNextCustomerId());
            }
            if (customer.getRegistrationDate() == null) {
                customer.setRegistrationDate(new java.sql.Date(System.currentTimeMillis()));
            }
            ps = conn.prepareStatement(query);
            ps.setString(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getLicenseNumber());
            ps.setDate(6, customer.getRegistrationDate());
            int rows = ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            return rows > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE CUSTOMER SET name = ?, phone = ?, email = ?, license_number = ? WHERE customer_id = ?";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getLicenseNumber());
            ps.setString(5, customer.getCustomerId());
            int rows = ps.executeUpdate();
            conn.commit();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            try { db.getConnection().rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(String customerId) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            String deleteUsersQuery = "DELETE FROM USERS WHERE customer_id = ?";
            PreparedStatement psDeleteUsers = conn.prepareStatement(deleteUsersQuery);
            psDeleteUsers.setString(1, customerId);
            psDeleteUsers.executeUpdate();
            psDeleteUsers.close();

            String deleteRentalsQuery = "DELETE FROM RENTAL WHERE customer_id = ?";
            PreparedStatement psDeleteRentals = conn.prepareStatement(deleteRentalsQuery);
            psDeleteRentals.setString(1, customerId);
            psDeleteRentals.executeUpdate();
            psDeleteRentals.close();

            String deleteCustomerQuery = "DELETE FROM CUSTOMER WHERE customer_id = ?";
            PreparedStatement psDeleteCustomer = conn.prepareStatement(deleteCustomerQuery);
            psDeleteCustomer.setString(1, customerId);
            int rows = psDeleteCustomer.executeUpdate();
            psDeleteCustomer.close();

            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM CUSTOMER WHERE email = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
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
            e.printStackTrace();
        }
        return false;
    }

    public boolean licenseExists(String licenseNumber) {
        String query = "SELECT COUNT(*) FROM CUSTOMER WHERE license_number = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, licenseNumber);
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
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalCustomerCount() {
        String query = "SELECT COUNT(*) FROM CUSTOMER";
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                stmt.close();
                return count;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String generateNextCustomerId() {
        String query = "SELECT customer_seq.NEXTVAL FROM dual";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = db.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int nextVal = rs.getInt(1);
                rs.close();
                return String.format("C%03d", nextVal);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return "C001";
    }

    public boolean hasActiveRentals(String customerId) {
        String query = "SELECT COUNT(*) FROM RENTAL WHERE customer_id = ? AND status = 'ACTIVE'";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, customerId);
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
            e.printStackTrace();
        }
        return false;
    }
}
