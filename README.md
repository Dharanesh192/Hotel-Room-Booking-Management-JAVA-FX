# 🏨 Hotel / Hostel Room Booking Management System (JavaFX)

A desktop-based **Hotel / Hostel Room Booking Management System** built using **JavaFX** and **PostgreSQL**.  
This application allows managing room availability, booking rooms, storing customer details and administering bookings through the JavaFX UI.


## 📁 Project Structure

    Hotel-Room-Booking-Management-JAVA-FX/
    │
    ├── Code/ 
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

## 🔗 Files To Download
- JAVA FX file Download link -> https://download2.gluonhq.com/openjfx/25.0.2/openjfx-25.0.2_windows-x64_bin-sdk.zip
- PostgreSQL Jar file Download link -> https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

## 💾 Compile and Run
    javac --module-path javafx-sdk-25\lib --add-modules javafx.controls,javafx.fxml \-cp PostgreSQL\postgresql-42.7.3.jar -d Compiled Code\*.java && \java --module-path javafx-sdk-25\lib --add-modules javafx.controls,javafx.fxml \-cp "Compiled;PostgreSQL\postgresql-42.7.3.jar" MainView

## 📜 Requriment For The Project
- Download the Javafx-sdk-25 to run FX code
- For postgreSQL jar download it the provided link
- Download the required Jar file for your database to connect Java code and the database
- Downioad the PostgreSQL to create a database and store the user input
- ⚠️ Follow the project structure as given 
- ⚠️ You can use any database for this project. **"But you need to alter the code"**
- 🎯 Create a database and set a password in your database
- 🎯 Add your database password to the code where I mentioned



