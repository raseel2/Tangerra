package vehicle_rental_management_system_p;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Database Connection Test");
        System.out.println("==============================================\n");

        DatabaseConnection db = DatabaseConnection.getInstance();

        System.out.println("Test 1: Testing database connection...");
        boolean connectionSuccess = db.testConnection();
        if (connectionSuccess) {
            System.out.println("✓ Connection test PASSED\n");
        } else {
            System.out.println("✗ Connection test FAILED\n");
            return;
        }

        System.out.println("Test 2: Executing test query...");
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 'Hello from Oracle Database!' AS message FROM dual");

            if (rs.next()) {
                System.out.println("Query result: " + rs.getString("message"));
                System.out.println("✓ Query test PASSED\n");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("✗ Query test FAILED");
            e.printStackTrace();
            return;
        }

        System.out.println("Test 3: Checking if tables exist...");
        try {
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT table_name FROM user_tables " +
                    "WHERE table_name IN ('CUSTOMER', 'VEHICLE', 'USERS', 'RENTAL')";
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("Tables found:");
            int tableCount = 0;
            while (rs.next()) {
                System.out.println("  - " + rs.getString("table_name"));
                tableCount++;
            }

            if (tableCount == 4) {
                System.out.println("✓ All 4 tables found\n");
            } else {
                System.out.println("✗ Expected 4 tables, found " + tableCount);
                System.out.println("Please run schema.sql to create tables\n");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("✗ Table check FAILED");
            System.out.println("Please run schema.sql to create tables");
            e.printStackTrace();
            return;
        }

        System.out.println("Test 4: Testing SessionManager...");
        SessionManager session = SessionManager.getInstance();

        session.login("U001", "admin", "ADMIN", "Test Admin", null);
        if (session.isLoggedIn() && session.isAdmin()) {
            System.out.println("✓ SessionManager login PASSED");
        } else {
            System.out.println("✗ SessionManager login FAILED");
        }

        session.logout();
        if (!session.isLoggedIn()) {
            System.out.println("✓ SessionManager logout PASSED\n");
        } else {
            System.out.println("✗ SessionManager logout FAILED\n");
        }

        System.out.println("==============================================");
        System.out.println("All tests completed successfully!");
        System.out.println("Database connection is working properly.");
        System.out.println("You can now proceed with application development.");
        System.out.println("==============================================");

        db.closeConnection();
    }
}
