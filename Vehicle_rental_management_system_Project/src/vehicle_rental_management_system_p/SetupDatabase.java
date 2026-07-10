package vehicle_rental_management_system_p;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SetupDatabase {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Database Setup - Creating Tables and Sample Data");
        System.out.println("==============================================\n");

        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        try {
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            System.out.println("Step 1: Dropping existing tables (if any)...");
            try {
                stmt.execute("DROP TABLE RENTAL CASCADE CONSTRAINTS");
                stmt.execute("DROP TABLE USERS CASCADE CONSTRAINTS");
                stmt.execute("DROP TABLE VEHICLE CASCADE CONSTRAINTS");
                stmt.execute("DROP TABLE CUSTOMER CASCADE CONSTRAINTS");
                System.out.println("✓ Existing tables dropped\n");
            } catch (SQLException e) {
                System.out.println("  (Tables may not exist yet - this is OK)\n");
            }

            System.out.println("Step 2: Dropping existing sequences (if any)...");
            try {
                stmt.execute("DROP SEQUENCE customer_seq");
                stmt.execute("DROP SEQUENCE vehicle_seq");
                stmt.execute("DROP SEQUENCE rental_seq");
                stmt.execute("DROP SEQUENCE user_seq");
                System.out.println("✓ Existing sequences dropped\n");
            } catch (SQLException e) {
                System.out.println("  (Sequences may not exist yet - this is OK)\n");
            }

            System.out.println("Step 3: Creating sequences...");
            stmt.execute("CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 1");
            stmt.execute("CREATE SEQUENCE vehicle_seq START WITH 1 INCREMENT BY 1");
            stmt.execute("CREATE SEQUENCE rental_seq START WITH 1 INCREMENT BY 1");
            stmt.execute("CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1");
            System.out.println("✓ Sequences created\n");

            System.out.println("Step 4: Creating CUSTOMER table...");
            String createCustomer = "CREATE TABLE CUSTOMER ("
                    + "customer_id VARCHAR2(10) PRIMARY KEY, "
                    + "name VARCHAR2(50) NOT NULL, "
                    + "phone VARCHAR2(15) NOT NULL, "
                    + "email VARCHAR2(50) NOT NULL, "
                    + "license_number VARCHAR2(20) NOT NULL UNIQUE, "
                    + "registration_date DATE DEFAULT SYSDATE)";
            stmt.execute(createCustomer);
            System.out.println("✓ CUSTOMER table created\n");

            System.out.println("Step 5: Creating VEHICLE table...");
            String createVehicle = "CREATE TABLE VEHICLE ("
                    + "vehicle_id VARCHAR2(10) PRIMARY KEY, "
                    + "vehicle_type VARCHAR2(20) NOT NULL CHECK (vehicle_type IN ('Car', 'Motorcycle', 'Truck', 'SUV')), "
                    + "brand VARCHAR2(30) NOT NULL, "
                    + "model VARCHAR2(30) NOT NULL, "
                    + "year NUMBER(4) NOT NULL CHECK (year >= 1900 AND year <= 2100), "
                    + "daily_rate NUMBER(8,2) NOT NULL CHECK (daily_rate > 0), "
                    + "is_available CHAR(1) DEFAULT 'Y' CHECK (is_available IN ('Y', 'N')))";
            stmt.execute(createVehicle);
            System.out.println("✓ VEHICLE table created\n");

            System.out.println("Step 6: Creating USERS table...");
            String createUsers = "CREATE TABLE USERS ("
                    + "user_id VARCHAR2(10) PRIMARY KEY, "
                    + "username VARCHAR2(30) NOT NULL UNIQUE, "
                    + "password VARCHAR2(50) NOT NULL, "
                    + "user_type VARCHAR2(10) NOT NULL CHECK (user_type IN ('ADMIN', 'EMPLOYEE', 'CUSTOMER')), "
                    + "full_name VARCHAR2(50) NOT NULL, "
                    + "customer_id VARCHAR2(10), "
                    + "CONSTRAINT fk_user_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE SET NULL)";
            stmt.execute(createUsers);
            System.out.println("✓ USERS table created\n");

            System.out.println("Step 7: Creating RENTAL table...");
            String createRental = "CREATE TABLE RENTAL ("
                    + "rental_id VARCHAR2(10) PRIMARY KEY, "
                    + "customer_id VARCHAR2(10) NOT NULL, "
                    + "vehicle_id VARCHAR2(10) NOT NULL, "
                    + "rental_date DATE NOT NULL, "
                    + "expected_return_date DATE NOT NULL, "
                    + "actual_return_date DATE, "
                    + "total_cost NUMBER(10,2), "
                    + "status VARCHAR2(10) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RETURNED')), "
                    + "CONSTRAINT fk_rental_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id), "
                    + "CONSTRAINT fk_rental_vehicle FOREIGN KEY (vehicle_id) REFERENCES VEHICLE(vehicle_id), "
                    + "CONSTRAINT chk_rental_dates CHECK (expected_return_date > rental_date))";
            stmt.execute(createRental);
            System.out.println("✓ RENTAL table created\n");

            System.out.println("Step 8: Inserting sample customers...");
            stmt.execute("INSERT INTO CUSTOMER VALUES ('C001', 'Alice Johnson', '+966501234567', 'alice.johnson@email.com', 'DL123456', SYSDATE)");
            stmt.execute("INSERT INTO CUSTOMER VALUES ('C002', 'Bob Smith', '+966502345678', 'bob.smith@email.com', 'DL234567', SYSDATE)");
            stmt.execute("INSERT INTO CUSTOMER VALUES ('C003', 'Carol White', '+966503456789', 'carol.white@email.com', 'DL345678', SYSDATE)");
            stmt.execute("INSERT INTO CUSTOMER VALUES ('C004', 'David Brown', '+966504567890', 'david.brown@email.com', 'DL456789', SYSDATE)");
            stmt.execute("INSERT INTO CUSTOMER VALUES ('C005', 'Emma Davis', '+966505678901', 'emma.davis@email.com', 'DL567890', SYSDATE)");
            System.out.println("✓ 5 customers inserted\n");

            System.out.println("Step 9: Inserting sample vehicles...");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V001', 'Car', 'Toyota', 'Camry', 2023, 50.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V002', 'Car', 'Honda', 'Accord', 2023, 55.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V003', 'Car', 'Ford', 'Fusion', 2022, 45.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V004', 'Car', 'Nissan', 'Altima', 2024, 52.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V005', 'Car', 'Chevrolet', 'Malibu', 2023, 48.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V006', 'SUV', 'Honda', 'CR-V', 2023, 75.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V007', 'SUV', 'Toyota', 'RAV4', 2024, 80.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V008', 'SUV', 'Ford', 'Explorer', 2023, 85.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V009', 'SUV', 'Jeep', 'Grand Cherokee', 2024, 90.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V010', 'SUV', 'Mazda', 'CX-5', 2023, 72.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V011', 'Truck', 'Ford', 'F-150', 2023, 95.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V012', 'Truck', 'Chevrolet', 'Silverado', 2024, 98.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V013', 'Truck', 'Ram', '1500', 2023, 92.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V014', 'Truck', 'Toyota', 'Tundra', 2024, 100.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V015', 'Motorcycle', 'Yamaha', 'MT-07', 2024, 30.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V016', 'Motorcycle', 'Honda', 'CB500F', 2023, 28.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V017', 'Motorcycle', 'Kawasaki', 'Ninja 400', 2024, 32.00, 'Y')");
            stmt.execute("INSERT INTO VEHICLE VALUES ('V018', 'Motorcycle', 'Suzuki', 'SV650', 2023, 29.00, 'Y')");
            System.out.println("✓ 18 vehicles inserted\n");

            System.out.println("Step 10: Inserting users...");
            stmt.execute("INSERT INTO USERS VALUES ('U001', 'admin', 'admin123', 'ADMIN', 'System Administrator', NULL)");
            stmt.execute("INSERT INTO USERS VALUES ('U002', 'employee', 'emp123', 'EMPLOYEE', 'John Employee', NULL)");
            stmt.execute("INSERT INTO USERS VALUES ('U003', 'sarah', 'sarah123', 'EMPLOYEE', 'Sarah Wilson', NULL)");
            stmt.execute("INSERT INTO USERS VALUES ('U004', 'alice', 'alice123', 'CUSTOMER', 'Alice Johnson', 'C001')");
            stmt.execute("INSERT INTO USERS VALUES ('U005', 'bob', 'bob123', 'CUSTOMER', 'Bob Smith', 'C002')");
            stmt.execute("INSERT INTO USERS VALUES ('U006', 'carol', 'carol123', 'CUSTOMER', 'Carol White', 'C003')");
            System.out.println("✓ 6 users inserted\n");

            System.out.println("Step 11: Inserting sample rentals...");
            stmt.execute("INSERT INTO RENTAL VALUES ('R001', 'C001', 'V001', "
                    + "TO_DATE('2025-10-25', 'YYYY-MM-DD'), TO_DATE('2025-10-30', 'YYYY-MM-DD'), "
                    + "NULL, NULL, 'ACTIVE')");
            stmt.execute("UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = 'V001'");

            stmt.execute("INSERT INTO RENTAL VALUES ('R002', 'C002', 'V006', "
                    + "TO_DATE('2025-10-26', 'YYYY-MM-DD'), TO_DATE('2025-10-31', 'YYYY-MM-DD'), "
                    + "NULL, NULL, 'ACTIVE')");
            stmt.execute("UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = 'V006'");

            stmt.execute("INSERT INTO RENTAL VALUES ('R003', 'C003', 'V015', "
                    + "TO_DATE('2025-10-20', 'YYYY-MM-DD'), TO_DATE('2025-10-23', 'YYYY-MM-DD'), "
                    + "TO_DATE('2025-10-23', 'YYYY-MM-DD'), 90.00, 'RETURNED')");

            stmt.execute("INSERT INTO RENTAL VALUES ('R004', 'C004', 'V003', "
                    + "TO_DATE('2025-10-15', 'YYYY-MM-DD'), TO_DATE('2025-10-20', 'YYYY-MM-DD'), "
                    + "TO_DATE('2025-10-20', 'YYYY-MM-DD'), 225.00, 'RETURNED')");

            stmt.execute("INSERT INTO RENTAL VALUES ('R005', 'C005', 'V011', "
                    + "TO_DATE('2025-10-22', 'YYYY-MM-DD'), TO_DATE('2025-10-27', 'YYYY-MM-DD'), "
                    + "NULL, NULL, 'ACTIVE')");
            stmt.execute("UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = 'V011'");
            System.out.println("✓ 5 rentals inserted\n");

            conn.commit();
            System.out.println("✓ All changes committed\n");

            stmt.close();

            System.out.println("==============================================");
            System.out.println("DATABASE SETUP COMPLETED SUCCESSFULLY!");
            System.out.println("==============================================");
            System.out.println("Summary:");
            System.out.println("  - 4 tables created");
            System.out.println("  - 5 customers added");
            System.out.println("  - 18 vehicles added");
            System.out.println("  - 6 users added");
            System.out.println("  - 5 rentals added");
            System.out.println("\nDefault Login Credentials:");
            System.out.println("  Admin:    username=admin    password=admin123");
            System.out.println("  Employee: username=employee password=emp123");
            System.out.println("  Customer: username=alice    password=alice123");
            System.out.println("==============================================");

        } catch (SQLException e) {
            System.err.println("\n✗ Database setup FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
    }
}
