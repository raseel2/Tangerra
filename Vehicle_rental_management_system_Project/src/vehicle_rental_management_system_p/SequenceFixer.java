package vehicle_rental_management_system_p;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SequenceFixer {

    private DatabaseConnection db;

    public SequenceFixer() {
        this.db = DatabaseConnection.getInstance();
    }

    public void fixSequences() {
        try {
            fixVehicleSequence();
            fixCustomerSequence();
            fixRentalSequence();
            fixUserSequence();
            System.out.println("Sequences fixed successfully");
        } catch (Exception e) {
            System.err.println("Error fixing sequences: " + e.getMessage());
        }
    }

    private void fixVehicleSequence() {
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();

            int maxVehicleId = getMaxNumericId(stmt, "VEHICLE", "vehicle_id");
            int nextVal = maxVehicleId + 1;

            stmt.execute("DROP SEQUENCE vehicle_seq");
            stmt.execute("CREATE SEQUENCE vehicle_seq START WITH " + nextVal);
            stmt.close();

            System.out.println("Vehicle sequence fixed. Next ID will be: V" + String.format("%03d", nextVal));

        } catch (SQLException e) {
            System.err.println("Error fixing vehicle sequence: " + e.getMessage());
        }
    }

    private void fixCustomerSequence() {
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();

            int maxCustomerId = getMaxNumericId(stmt, "CUSTOMER", "customer_id");
            int nextVal = maxCustomerId + 1;

            stmt.execute("DROP SEQUENCE customer_seq");
            stmt.execute("CREATE SEQUENCE customer_seq START WITH " + nextVal);
            stmt.close();

            System.out.println("Customer sequence fixed. Next ID will be: C" + String.format("%03d", nextVal));

        } catch (SQLException e) {
            System.err.println("Error fixing customer sequence: " + e.getMessage());
        }
    }

    private void fixRentalSequence() {
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();

            int maxRentalId = getMaxNumericId(stmt, "RENTAL", "rental_id");
            int nextVal = maxRentalId + 1;

            stmt.execute("DROP SEQUENCE rental_seq");
            stmt.execute("CREATE SEQUENCE rental_seq START WITH " + nextVal);
            stmt.close();

            System.out.println("Rental sequence fixed. Next ID will be: R" + String.format("%03d", nextVal));

        } catch (SQLException e) {
            System.err.println("Error fixing rental sequence: " + e.getMessage());
        }
    }

    private void fixUserSequence() {
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();

            int maxUserId = getMaxNumericId(stmt, "USERS", "user_id");
            int nextVal = maxUserId + 1;

            stmt.execute("DROP SEQUENCE user_seq");
            stmt.execute("CREATE SEQUENCE user_seq START WITH " + nextVal);
            stmt.close();

            System.out.println("User sequence fixed. Next ID will be: U" + String.format("%03d", nextVal));

        } catch (SQLException e) {
            System.err.println("Error fixing user sequence: " + e.getMessage());
        }
    }

    private int getMaxNumericId(Statement stmt, String tableName, String columnName) throws SQLException {
        String query = "SELECT MAX(SUBSTR(" + columnName + ", 2)) as max_id FROM " + tableName;
        ResultSet rs = stmt.executeQuery(query);
        int maxId = 0;

        if (rs.next()) {
            String maxIdStr = rs.getString("max_id");
            if (maxIdStr != null && !maxIdStr.isEmpty()) {
                try {
                    maxId = Integer.parseInt(maxIdStr);
                } catch (NumberFormatException e) {
                    maxId = 0;
                }
            }
        }

        rs.close();
        return maxId;
    }

}
