import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CustomerDetailsWindow {

    /**
     * Opens the customer form for the selected room.
     */
    public void showWindow(int floor_no, int room_no, Runnable onSaved) {

        Stage window = new Stage();
        window.setTitle("Customer Details — Room " + floor_no + "-" + room_no);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(12);
        grid.setHgap(10);

        Label lblName = new Label("Name:");
        Label lblAddress = new Label("Address:");
        Label lblPhone = new Label("Phone:");
        Label lblAge = new Label("Age:");
        Label lblPeople = new Label("No. of People:");
        Label lblDays = new Label("No. of Days:");

        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField phoneField = new TextField();
        TextField ageField = new TextField();
        TextField peopleField = new TextField();
        TextField daysField = new TextField();

        Button saveBtn = new Button("Save Customer");
        saveBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 8 12;");

        // --------------------------------------------------------------------
        // SAVE BUTTON LOGIC
        // --------------------------------------------------------------------
        saveBtn.setOnAction(e -> {

            // Empty fields → skip quietly
            if (nameField.getText().isEmpty() ||
                    addressField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() ||
                    ageField.getText().isEmpty() ||
                    peopleField.getText().isEmpty() ||
                    daysField.getText().isEmpty()) {
                return;
            }

            try {
                int age = Integer.parseInt(ageField.getText());
                int people = Integer.parseInt(peopleField.getText());
                int days = Integer.parseInt(daysField.getText());

                // ---------------------- CAPACITY CHECK ----------------------
                int capacity = getRoomCapacity(floor_no, room_no);

                if (people > capacity) {
                    Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setHeaderText(null);
                    a.setContentText(
                            "Room capacity exceeded!\n" +
                            "Capacity: " + capacity + "\n" +
                            "Entered: " + people
                    );
                    a.showAndWait();
                    return; // ❌ STOP saving
                }

                // ---------------------- SAVE DATA ----------------------
                boolean ok = saveCustomer(
                        nameField.getText(),
                        addressField.getText(),
                        phoneField.getText(),
                        age,
                        people,
                        days,
                        floor_no,
                        room_no
                );

                if (ok) {
                    window.close();            // close window
                    if (onSaved != null) onSaved.run(); // refresh UI
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // UI LAYOUT ---------------------------------------------------------
        grid.add(lblName, 0, 0);      grid.add(nameField, 1, 0);
        grid.add(lblAddress, 0, 1);   grid.add(addressField, 1, 1);
        grid.add(lblPhone, 0, 2);     grid.add(phoneField, 1, 2);
        grid.add(lblAge, 0, 3);       grid.add(ageField, 1, 3);
        grid.add(lblPeople, 0, 4);    grid.add(peopleField, 1, 4);
        grid.add(lblDays, 0, 5);      grid.add(daysField, 1, 5);

        grid.add(saveBtn, 1, 6);

        Scene scene = new Scene(grid, 420, 360);
        scene.getStylesheets().add(new File("manager-style.css").toURI().toString());
        window.setScene(scene);
        window.show();
    }

    // ========================================================================
    // DATABASE SAVE
    // ========================================================================
    private boolean saveCustomer(String name, String address, String phone,
                                 int age, int people, int days,
                                 int floor_no, int room_no) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String pass = "Project(JAVA)";

        String sql = "INSERT INTO customers (name, address, phone, age, people, days, floor_no, room_no) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.setInt(4, age);
            stmt.setInt(5, people);
            stmt.setInt(6, days);
            stmt.setInt(7, floor_no);
            stmt.setInt(8, room_no);

            stmt.executeUpdate();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ========================================================================
    // GET ROOM CAPACITY FROM DB
    // ========================================================================
    private int getRoomCapacity(int floor, int room) {

        String sql = "SELECT capacity FROM rooms WHERE floor_no=? AND room_no=?";
        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String pass = "Project(JAVA)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, floor);
            stmt.setInt(2, room);

            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("capacity");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0; // default if database issue
    }
}
