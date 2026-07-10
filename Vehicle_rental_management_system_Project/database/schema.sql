DROP TABLE RENTAL CASCADE CONSTRAINTS;
DROP TABLE USERS CASCADE CONSTRAINTS;
DROP TABLE VEHICLE CASCADE CONSTRAINTS;
DROP TABLE CUSTOMER CASCADE CONSTRAINTS;

DROP SEQUENCE customer_seq;
DROP SEQUENCE vehicle_seq;
DROP SEQUENCE rental_seq;
DROP SEQUENCE user_seq;

CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE vehicle_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE rental_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE CUSTOMER (
    customer_id VARCHAR2(10) PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    phone VARCHAR2(15) NOT NULL,
    email VARCHAR2(50) NOT NULL,
    license_number VARCHAR2(20) NOT NULL UNIQUE,
    registration_date DATE DEFAULT SYSDATE
);

CREATE TABLE VEHICLE (
    vehicle_id VARCHAR2(10) PRIMARY KEY,
    vehicle_type VARCHAR2(20) NOT NULL CHECK (vehicle_type IN ('Car', 'Motorcycle', 'Truck', 'SUV')),
    brand VARCHAR2(30) NOT NULL,
    model VARCHAR2(30) NOT NULL,
    year NUMBER(4) NOT NULL CHECK (year >= 1900 AND year <= 2100),
    daily_rate NUMBER(8,2) NOT NULL CHECK (daily_rate > 0),
    is_available CHAR(1) DEFAULT 'Y' CHECK (is_available IN ('Y', 'N'))
);

CREATE TABLE USERS (
    user_id VARCHAR2(10) PRIMARY KEY,
    username VARCHAR2(30) NOT NULL UNIQUE,
    password VARCHAR2(50) NOT NULL,
    user_type VARCHAR2(10) NOT NULL CHECK (user_type IN ('ADMIN', 'EMPLOYEE', 'CUSTOMER')),
    full_name VARCHAR2(50) NOT NULL,
    customer_id VARCHAR2(10),
    CONSTRAINT fk_user_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE SET NULL
);

CREATE TABLE RENTAL (
    rental_id VARCHAR2(10) PRIMARY KEY,
    customer_id VARCHAR2(10) NOT NULL,
    vehicle_id VARCHAR2(10) NOT NULL,
    rental_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_date DATE,
    total_cost NUMBER(10,2),
    status VARCHAR2(10) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RETURNED')),
    CONSTRAINT fk_rental_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    CONSTRAINT fk_rental_vehicle FOREIGN KEY (vehicle_id) REFERENCES VEHICLE(vehicle_id),
    CONSTRAINT chk_rental_dates CHECK (expected_return_date > rental_date)
);

CREATE INDEX idx_rental_customer ON RENTAL(customer_id);
CREATE INDEX idx_rental_vehicle ON RENTAL(vehicle_id);
CREATE INDEX idx_rental_status ON RENTAL(status);
CREATE INDEX idx_vehicle_available ON VEHICLE(is_available);
CREATE INDEX idx_user_username ON USERS(username);

INSERT INTO CUSTOMER VALUES ('C001', 'Alice Johnson', '+966501234567', 'alice.johnson@email.com', 'DL123456', SYSDATE);
INSERT INTO CUSTOMER VALUES ('C002', 'Bob Smith', '+966502345678', 'bob.smith@email.com', 'DL234567', SYSDATE);
INSERT INTO CUSTOMER VALUES ('C003', 'Carol White', '+966503456789', 'carol.white@email.com', 'DL345678', SYSDATE);
INSERT INTO CUSTOMER VALUES ('C004', 'David Brown', '+966504567890', 'david.brown@email.com', 'DL456789', SYSDATE);
INSERT INTO CUSTOMER VALUES ('C005', 'Emma Davis', '+966505678901', 'emma.davis@email.com', 'DL567890', SYSDATE);

INSERT INTO VEHICLE VALUES ('V001', 'Car', 'Toyota', 'Camry', 2023, 188.00, 'Y');
INSERT INTO VEHICLE VALUES ('V002', 'Car', 'Honda', 'Accord', 2023, 206.00, 'Y');
INSERT INTO VEHICLE VALUES ('V003', 'Car', 'Ford', 'Fusion', 2022, 169.00, 'Y');
INSERT INTO VEHICLE VALUES ('V004', 'Car', 'Nissan', 'Altima', 2024, 195.00, 'Y');
INSERT INTO VEHICLE VALUES ('V005', 'Car', 'Chevrolet', 'Malibu', 2023, 180.00, 'Y');

INSERT INTO VEHICLE VALUES ('V006', 'SUV', 'Honda', 'CR-V', 2023, 281.00, 'Y');
INSERT INTO VEHICLE VALUES ('V007', 'SUV', 'Toyota', 'RAV4', 2024, 300.00, 'Y');
INSERT INTO VEHICLE VALUES ('V008', 'SUV', 'Ford', 'Explorer', 2023, 319.00, 'Y');
INSERT INTO VEHICLE VALUES ('V009', 'SUV', 'Jeep', 'Grand Cherokee', 2024, 338.00, 'Y');
INSERT INTO VEHICLE VALUES ('V010', 'SUV', 'Mazda', 'CX-5', 2023, 270.00, 'Y');

INSERT INTO VEHICLE VALUES ('V011', 'Truck', 'Ford', 'F-150', 2023, 356.00, 'Y');
INSERT INTO VEHICLE VALUES ('V012', 'Truck', 'Chevrolet', 'Silverado', 2024, 368.00, 'Y');
INSERT INTO VEHICLE VALUES ('V013', 'Truck', 'Ram', '1500', 2023, 345.00, 'Y');
INSERT INTO VEHICLE VALUES ('V014', 'Truck', 'Toyota', 'Tundra', 2024, 375.00, 'Y');

INSERT INTO VEHICLE VALUES ('V015', 'Motorcycle', 'Yamaha', 'MT-07', 2024, 113.00, 'Y');
INSERT INTO VEHICLE VALUES ('V016', 'Motorcycle', 'Honda', 'CB500F', 2023, 105.00, 'Y');
INSERT INTO VEHICLE VALUES ('V017', 'Motorcycle', 'Kawasaki', 'Ninja 400', 2024, 120.00, 'Y');
INSERT INTO VEHICLE VALUES ('V018', 'Motorcycle', 'Suzuki', 'SV650', 2023, 109.00, 'Y');

INSERT INTO USERS VALUES ('U001', 'admin', 'admin123', 'ADMIN', 'System Administrator', NULL);
INSERT INTO USERS VALUES ('U002', 'employee', 'emp123', 'EMPLOYEE', 'John Employee', NULL);
INSERT INTO USERS VALUES ('U003', 'sarah', 'sarah123', 'EMPLOYEE', 'Sarah Wilson', NULL);
INSERT INTO USERS VALUES ('U004', 'alice', 'alice123', 'CUSTOMER', 'Alice Johnson', 'C001');
INSERT INTO USERS VALUES ('U005', 'bob', 'bob123', 'CUSTOMER', 'Bob Smith', 'C002');
INSERT INTO USERS VALUES ('U006', 'carol', 'carol123', 'CUSTOMER', 'Carol White', 'C003');

INSERT INTO RENTAL VALUES ('R001', 'C001', 'V001', TO_DATE('2025-10-25', 'YYYY-MM-DD'), TO_DATE('2025-10-30', 'YYYY-MM-DD'), NULL, NULL, 'ACTIVE');
UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = 'V001';

INSERT INTO RENTAL VALUES ('R002', 'C002', 'V006', TO_DATE('2025-10-26', 'YYYY-MM-DD'), TO_DATE('2025-10-31', 'YYYY-MM-DD'), NULL, NULL, 'ACTIVE');
UPDATE VEHICLE SET is_available = 'N' WHERE vehicle_id = 'V006';

INSERT INTO RENTAL VALUES ('R003', 'C003', 'V015', TO_DATE('2025-10-20', 'YYYY-MM-DD'), TO_DATE('2025-10-23', 'YYYY-MM-DD'), TO_DATE('2025-10-23', 'YYYY-MM-DD'), 339.00, 'RETURNED');

INSERT INTO RENTAL VALUES ('R004', 'C004', 'V003', TO_DATE('2025-10-15', 'YYYY-MM-DD'), TO_DATE('2025-10-20', 'YYYY-MM-DD'), TO_DATE('2025-10-20', 'YYYY-MM-DD'), 845.00, 'RETURNED');

COMMIT;
