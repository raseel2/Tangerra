package vehicle_rental_management_system_p;

import java.sql.*;
import java.util.ArrayList;

public class VehicleDatabase {

    private DatabaseConnection db;

    public VehicleDatabase() {
        this.db = DatabaseConnection.getInstance();
    }

    public Vehicle getVehicleById(String vehicleId) {
        String query = "SELECT * FROM VEHICLE WHERE vehicle_id = ?";
        Vehicle vehicle = null;

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicleId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate"),
                        rs.getString("is_available").charAt(0)
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error getting vehicle by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return vehicle;
    }

    public ArrayList<Vehicle> getVehicles() {
        String query = "SELECT * FROM VEHICLE ORDER BY vehicle_id";
        ArrayList<Vehicle> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate"),
                        rs.getString("is_available").charAt(0)
                );
                list.add(vehicle);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Vehicle> getVehiclesByType(String vehicleType) {
        String query = "SELECT * FROM VEHICLE WHERE vehicle_type = ? ORDER BY brand, model";
        ArrayList<Vehicle> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicleType);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate"),
                        rs.getString("is_available").charAt(0)
                );
                list.add(vehicle);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error getting vehicles by type: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Vehicle> getAvailableVehicles() {
        String query = "SELECT * FROM VEHICLE WHERE is_available = 'Y' ORDER BY vehicle_type, brand";
        ArrayList<Vehicle> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate"),
                        rs.getString("is_available").charAt(0)
                );
                list.add(vehicle);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error getting available vehicles: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Vehicle> searchVehicles(String keyword) {
        String query = "SELECT * FROM VEHICLE WHERE "
                + "UPPER(brand) LIKE UPPER(?) OR "
                + "UPPER(model) LIKE UPPER(?) OR "
                + "UPPER(vehicle_type) LIKE UPPER(?) "
                + "ORDER BY vehicle_type, brand";
        ArrayList<Vehicle> list = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate"),
                        rs.getString("is_available").charAt(0)
                );
                list.add(vehicle);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error searching vehicles: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean addVehicle(Vehicle vehicle) {
        String query = "INSERT INTO VEHICLE (vehicle_id, vehicle_type, brand, model, year, daily_rate, is_available) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();

            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            if (vehicle.getVehicleId() == null || vehicle.getVehicleId().isEmpty()) {
                vehicle.setVehicleId(generateNextVehicleId());
            }

            ps = conn.prepareStatement(query);
            ps.setString(1, vehicle.getVehicleId());
            ps.setString(2, vehicle.getVehicleType());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setInt(5, vehicle.getYear());
            ps.setDouble(6, vehicle.getDailyRate());
            ps.setString(7, String.valueOf(vehicle.getIsAvailable()));

            int rowsAffected = ps.executeUpdate();
            conn.commit();

            conn.setAutoCommit(originalAutoCommit);

            System.out.println("Vehicle added successfully: " + vehicle.getVehicleId());
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String query = "UPDATE VEHICLE SET vehicle_type = ?, brand = ?, model = ?, "
                + "year = ?, daily_rate = ?, is_available = ? WHERE vehicle_id = ?";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicle.getVehicleType());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYear());
            ps.setDouble(5, vehicle.getDailyRate());
            ps.setString(6, String.valueOf(vehicle.getIsAvailable()));
            ps.setString(7, vehicle.getVehicleId());

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean deleteVehicle(String vehicleId) {
        String query = "DELETE FROM VEHICLE WHERE vehicle_id = ?";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicleId);

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateAvailability(String vehicleId, char isAvailable) {
        String query = "UPDATE VEHICLE SET is_available = ? WHERE vehicle_id = ?";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, String.valueOf(isAvailable));
            ps.setString(2, vehicleId);

            int rowsAffected = ps.executeUpdate();
            conn.commit();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating vehicle availability: " + e.getMessage());
            e.printStackTrace();
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean isVehicleRented(String vehicleId) {
        String query = "SELECT is_available FROM VEHICLE WHERE vehicle_id = ?";

        try {
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vehicleId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                char availability = rs.getString("is_available").charAt(0);
                rs.close();
                ps.close();
                return availability == 'N';
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error checking vehicle rental status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public int getTotalVehicleCount() {
        String query = "SELECT COUNT(*) FROM VEHICLE";

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
            System.err.println("Error getting total vehicle count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getAvailableVehicleCount() {
        String query = "SELECT COUNT(*) FROM VEHICLE WHERE is_available = 'Y'";

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
            System.err.println("Error getting available vehicle count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public String generateNextVehicleId() {
        String query = "SELECT vehicle_seq.NEXTVAL FROM dual";
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = db.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                int nextVal = rs.getInt(1);
                rs.close();
                return String.format("V%03d", nextVal);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("Error generating vehicle ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return "V001";
    }
}
