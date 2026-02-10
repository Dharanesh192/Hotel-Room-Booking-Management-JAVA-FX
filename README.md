# 🏨 Hotel / Hostel Room Booking Management System (JavaFX)

A desktop-based **Hotel / Hostel Room Booking Management System** built using **JavaFX**, **PostgreSQL**, and **JDBC**.  
This application allows managing room availability, booking rooms, storing customer details, and administering bookings through a modern JavaFX UI.

---

## ✨ Features

### 🏢 Room Management
- Floors 1–5, each with 20 rooms
- Visual room grid
- Color indication:
  - 🟥 Booked rooms
  - ⬜ Available rooms
  - 🟩 Selected room
- Room number displayed (e.g., 101, 205)
- Tooltip shows:
  - Room price
  - Capacity
  - Booking status

### 👤 Customer Management
- Add customer details when booking a room
- View all customers in a table
- Update customer details
- Delete customer bookings
- Auto-refresh room availability when changes occur

### 🔍 Search & Filter
- Search customers by:
  - Name
  - Room number
  - Phone
  - Age
  - Number of people
  - Days of stay
- Filter customers by floor using a dropdown

### ⚡ Performance
- Room data is cached in memory for fast UI response
- Database is accessed only when required
- Automatic refresh after insert/update/delete

### 🎨 UI Theme
- Premium **Red & Gold** theme
- Custom CSS styling
- Responsive layout using JavaFX containers

---

## 🛠 Technologies Used

- **Java 24**
- **JavaFX 25**
- **PostgreSQL**
- **JDBC (PostgreSQL Driver)**
- **CSS (JavaFX Styling)**

---

## 📁 Project Structure

    Hotel-Room-Booking-Management-JAVA-FX/
    │
    ├── Code/ # Java code files
    │ ├── MainView.java
    │ ├── CustomerDetailsWindow.java
    │ ├── CustomerManagementWindow.java
    │ ├── Customer.java
    │ └── RoomInfo.java
    │
    ├── Compiled/
    │    └──manager-style.css
    │    └── # Compiled .class files
    │
    ├── PostgreSQL/
    │ └── postgresql-42.7.3.jar
    │
    ├── javafx-sdk-25/
        └── lib/
    
