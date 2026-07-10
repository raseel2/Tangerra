# Tangerra – Vehicle Rental Management System

Tangerra is a desktop application for managing a vehicle rental business, built with **Java**, **JavaFX**, and **Oracle Database**. It provides role-based access for Admins, Employees, and Customers to handle the complete vehicle rental workflow — from browsing vehicles to processing returns.

## Features

- **Role-based login** for Admin, Employee, and Customer accounts
- **Customer role**: browse available vehicles, create bookings, view rental history and booking details
- **Employee role**: manage vehicles, customers, and rentals — including processing returns and calculating costs
- **Admin role**: full system access, including user management, in addition to all employee capabilities

## Tech Stack

- **Language:** Java
- **UI:** JavaFX (FXML + CSS)
- **Database:** Oracle Database (JDBC)
- **Build tool:** Ant / NetBeans project

## Screens

- Login
- Dashboard
- Customer Management
- Vehicle Management
- Rental Management
- User Management
- Customer Booking / Booking History

## Screenshots

| Login | Dashboard |
|---|---|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) |

| Customer Management | Vehicle Management |
|---|---|
| ![Customer Management](screenshots/customer_management.png) | ![Vehicle Management](screenshots/vehicle_management.png) |

| Rental Management | User Management |
|---|---|
| ![Rental Management](screenshots/rental_management.png) | ![User Management](screenshots/user_management.png) |

| Customer Booking | Booking History |
|---|---|
| ![Customer Booking](screenshots/customer_booking.png) | ![Booking History](screenshots/customer_bookings.png) |

### Class Diagram
![Class Diagram](screenshots/class_diagram.png)

## Database Schema

The system uses four main tables:

- `CUSTOMER` – customer details and license info
- `VEHICLE` – vehicle inventory (type, brand, model, daily rate, availability)
- `USERS` – login accounts, linked to a role (`ADMIN`, `EMPLOYEE`, `CUSTOMER`)
- `RENTAL` – rental records linking customers and vehicles, with dates, cost, and status

See [`database/schema.sql`](database/schema.sql) for the full schema.

## Getting Started

### Prerequisites
- Java JDK (8 or later)
- JavaFX SDK
- Oracle Database (XE or similar)
- NetBeans (recommended, project is pre-configured for it) or Ant

### Setup
1. Clone the repository
   ```bash
   git clone https://github.com/USERNAME/Tangerra.git
   ```
2. Open the project in NetBeans (`File > Open Project`)
3. Update the database connection details in `DatabaseConnection.java` with your own Oracle credentials
4. Run `SetupDatabase.java` once to create the tables and sample data
5. Run `MainApp.java` to launch the application

## Project Team

This project was developed for **CS313 – Advanced Programming Language**:

- Raseel Mohammed Al-Shahrani
- Aryam Hadi Al-Faifi
- Rahaf Yahaya Al-Malki
- Hala Abdullatif Al-Ghamdi

## License

This project was developed for educational purposes.
