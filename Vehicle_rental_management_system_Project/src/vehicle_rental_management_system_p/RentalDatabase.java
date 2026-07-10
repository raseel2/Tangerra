package vehicle_rental_management_system_p;

import java.sql.*;
import java.util.ArrayList;

public class RentalDatabase {
    private DatabaseConnection db;

    public RentalDatabase() {
        this.db = DatabaseConnection.getInstance();
    }

    public Rental getRentalById(String rentalId) {
        String query = "SELECT r.*, c.name as customer_name, v.brand || ' ' || v.model as vehicle_info " +
                      "FROM RENTAL r JOIN CUSTOMER c ON r.customer_id = c.customer_id " +
                      "JOIN VEHICLE v ON r.vehicle_id = v.vehicle_id WHERE r.rental_id = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rentalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Rental rental = createRentalFromResultSet(rs);
                rs.close();
                ps.close();
                return rental;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Rental> getRentals() {
        ArrayList<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.*, c.name as customer_name, v.brand || ' ' || v.model as vehicle_info " +
                      "FROM RENTAL r JOIN CUSTOMER c ON r.customer_id = c.customer_id " +
                      "JOIN VEHICLE v ON r.vehicle_id = v.vehicle_id ORDER BY r.rental_date DESC";
        Connection conn = null;
        try {
            conn = db.getConnection();
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                rentals.add(createRentalFromResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }

    public ArrayList<Rental> getActiveRentals() {
        ArrayList<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.*, c.name as customer_name, v.brand || ' ' || v.model as vehicle_info " +
                      "FROM RENTAL r JOIN CUSTOMER c ON r.customer_id = c.customer_id " +
                      "JOIN VEHICLE v ON r.vehicle_id = v.vehicle_id WHERE r.status = 'ACTIVE' ORDER BY r.rental_date DESC";
        Connection conn = null;
        try {
            conn = db.getConnection();
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                rentals.add(createRentalFromResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }

    public ArrayList<Rental> getRentalsByCustomer(String customerId) {
        ArrayList<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.*, c.name as customer_name, v.brand || ' ' || v.model as vehicle_info " +
                      "FROM RENTAL r JOIN CUSTOMER c ON r.customer_id = c.customer_id " +
                      "JOIN VEHICLE v ON r.vehicle_id = v.vehicle_id WHERE r.customer_id = ? ORDER BY r.rental_date DESC";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rentals.add(createRentalFromResultSet(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }

    public boolean addRental(Rental rental) {
        String query = "INSERT INTO RENTAL (rental_id, customer_id, vehicle_id, rental_date, expected_return_date, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            if (rental.getRentalId() == null || rental.getRentalId().isEmpty()) {
                rental.setRentalId(generateNextRentalId());
            }
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rental.getRentalId());
            ps.setString(2, rental.getCustomerId());
            ps.setString(3, rental.getVehicleId());
            ps.setDate(4, rental.getRentalDate());
            ps.setDate(5, rental.getExpectedReturnDate());
            int rows = ps.executeUpdate();
            
            PreparedStatement updateVehicle = conn.prepareStatement("UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = ?");
            updateVehicle.setString(1, rental.getVehicleId());
            updateVehicle.executeUpdate();
            updateVehicle.close();
            
            conn.commit();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            try { db.getConnection().rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRental(Rental rental) {
        String query = "UPDATE RENTAL SET expected_return_date = ?, actual_return_date = ?, total_cost = ?, status = ? WHERE rental_id = ?";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1, rental.getExpectedReturnDate());
            ps.setDate(2, rental.getActualReturnDate());
            ps.setDouble(3, rental.getTotalCost());
            ps.setString(4, rental.getStatus());
            ps.setString(5, rental.getRentalId());
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

    public boolean returnVehicle(String rentalId, Date returnDate) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            double cost = calculateRentalCost(rentalId, returnDate);

            PreparedStatement getRental = conn.prepareStatement("SELECT vehicle_id FROM RENTAL WHERE rental_id = ?");
            getRental.setString(1, rentalId);
            ResultSet rs = getRental.executeQuery();
            String vehicleId = null;
            if (rs.next()) {
                vehicleId = rs.getString("vehicle_id");
            }
            rs.close();
            getRental.close();

            PreparedStatement updateRental = conn.prepareStatement(
                "UPDATE RENTAL SET actual_return_date = ?, total_cost = ?, status = 'RETURNED' WHERE rental_id = ?");
            updateRental.setDate(1, returnDate);
            updateRental.setDouble(2, cost);
            updateRental.setString(3, rentalId);
            updateRental.executeUpdate();
            updateRental.close();

            if (vehicleId != null) {
                PreparedStatement updateVehicle = conn.prepareStatement("UPDATE VEHICLE SET is_available = 'Y' WHERE vehicle_id = ?");
                updateVehicle.setString(1, vehicleId);
                updateVehicle.executeUpdate();
                updateVehicle.close();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public double calculateRentalCost(String rentalId, Date returnDate) {
        String query = "SELECT r.rental_date, r.expected_return_date, v.daily_rate FROM RENTAL r " +
                      "JOIN VEHICLE v ON r.vehicle_id = v.vehicle_id WHERE r.rental_id = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rentalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Date rentalDate = rs.getDate("rental_date");
                Date expectedReturn = rs.getDate("expected_return_date");
                double dailyRate = rs.getDouble("daily_rate");
                
                long rentalDays = (returnDate.getTime() - rentalDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
                long expectedDays = (expectedReturn.getTime() - rentalDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
                
                double cost = expectedDays * dailyRate;
                
                if (rentalDays > expectedDays) {
                    long lateDays = rentalDays - expectedDays;
                    cost += lateDays * dailyRate * 1.5;
                }
                
                rs.close();
                ps.close();
                return cost;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveRentalCount() {
        String query = "SELECT COUNT(*) FROM RENTAL WHERE status = 'ACTIVE'";
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

    public String generateNextRentalId() {
        String query = "SELECT rental_seq.NEXTVAL FROM dual";
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int nextVal = rs.getInt(1);
                rs.close();
                stmt.close();
                return String.format("R%03d", nextVal);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "R001";
    }

    private Rental createRentalFromResultSet(ResultSet rs) throws SQLException {
        Rental rental = new Rental(
            rs.getString("rental_id"),
            rs.getString("customer_id"),
            rs.getString("vehicle_id"),
            rs.getDate("rental_date"),
            rs.getDate("expected_return_date"),
            rs.getDate("actual_return_date"),
            rs.getDouble("total_cost"),
            rs.getString("status")
        );
        rental.setCustomerName(rs.getString("customer_name"));
        rental.setVehicleInfo(rs.getString("vehicle_info"));
        return rental;
    }
}
