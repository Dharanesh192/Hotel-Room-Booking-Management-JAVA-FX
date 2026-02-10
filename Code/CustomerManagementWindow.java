import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class CustomerManagementWindow {

    private TableView<Customer> table;

    // masterList = full DB data
    private ObservableList<Customer> masterList = FXCollections.observableArrayList();

    // customers = shown in table
    private ObservableList<Customer> customers = FXCollections.observableArrayList();

    // Input fields
    private TextField nameField = new TextField();
    private TextField addressField = new TextField();
    private TextField phoneField = new TextField();
    private TextField ageField = new TextField();
    private TextField peopleField = new TextField();
    private TextField daysField = new TextField();

    private Runnable onChange;

    public CustomerManagementWindow(Runnable onChange) {
        this.onChange = onChange;
    }

    public void showWindow() {
        Stage window = new Stage();
        window.setTitle("Customer Database Manager");

        // ---------------- TABLE ----------------
        table = new TableView<>();
        table.setPrefHeight(300);

        table.getColumns().addAll(
                makeColumn("ID", "id", 50),
                makeColumn("Name", "name", 120),
                makeColumn("Address", "address", 140),
                makeColumn("Phone", "phone", 100),
                makeColumn("Age", "age", 60),
                makeColumn("People", "people", 80),
                makeColumn("Days", "days", 80),
                makeColumn("Floor", "floor_no", 60),
                makeColumn("Room", "room_no", 60)
        );

        loadData();
        customers.setAll(masterList);

        FilteredList<Customer> filteredList = new FilteredList<>(customers, p -> true);

        // ---------------- SEARCH BAR ----------------
        ComboBox<String> filterChoice = new ComboBox<>();
        filterChoice.getItems().addAll("Name", "Room", "People", "Phone", "Address", "Age", "Days");
        filterChoice.setValue("Name");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        searchField.textProperty().addListener((obs, o, text) -> {
            filteredList.setPredicate(c -> matchesFilter(c, filterChoice.getValue(), text));
        });

        table.setItems(filteredList);

        HBox searchBar = new HBox(6, new Label("Search by:"), filterChoice, searchField);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(10));

        // ---------------- FLOOR FILTER ----------------
        ComboBox<Integer> floorFilter = new ComboBox<>();
        floorFilter.getItems().addAll(1, 2, 3, 4, 5);
        floorFilter.setPromptText("Select Floor");

        Button applyFloor = new Button("Apply Floor");
        Button resetFloor = new Button("Reset");

        applyFloor.setOnAction(e -> {
            Integer f = floorFilter.getValue();
            if (f != null)
                customers.setAll(masterList.filtered(c -> c.getFloorNo() == f));
        });

        resetFloor.setOnAction(e -> {
            customers.setAll(masterList);
            floorFilter.setPromptText("Select Floor");
        });

        HBox floorBox = new HBox(6, floorFilter, applyFloor, resetFloor);
        floorBox.setAlignment(Pos.CENTER_LEFT);
        floorBox.setPadding(new Insets(5));

        // ---------------- SELECT ROW ----------------
        table.setOnMouseClicked(e -> {
            Customer c = table.getSelectionModel().getSelectedItem();
            if (c != null) {
                nameField.setText(c.getName());
                addressField.setText(c.getAddress());
                phoneField.setText(c.getPhone());
                ageField.setText(String.valueOf(c.getAge()));
                peopleField.setText(String.valueOf(c.getPeople()));
                daysField.setText(String.valueOf(c.getDays()));
            }
        });

        // ---------------- BUTTONS ----------------
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");

        updateBtn.setOnAction(e -> updateSelected());
        deleteBtn.setOnAction(e -> deleteSelected());
        refreshBtn.setOnAction(e -> {
            loadData();
            customers.setAll(masterList);
            clearFields();
        });

        HBox actions = new HBox(10, updateBtn, deleteBtn, refreshBtn);
        actions.setAlignment(Pos.CENTER);

        // ---------------- EDIT GRID ----------------
        GridPane editGrid = new GridPane();
        editGrid.setVgap(10);
        editGrid.setHgap(10);
        editGrid.setPadding(new Insets(20));

        addField(editGrid, "Name:", nameField, 0);
        addField(editGrid, "Address:", addressField, 1);
        addField(editGrid, "Phone:", phoneField, 2);
        addField(editGrid, "Age:", ageField, 3);
        addField(editGrid, "People:", peopleField, 4);
        addField(editGrid, "Days:", daysField, 5);

        HBox topRow = new HBox(25, floorBox, searchBar);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));


        VBox layout = new VBox(8, topRow, table, editGrid, actions);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 900, 550);
        scene.getStylesheets().add(new File("Compiled/manager-style.css").toURI().toString());
        window.setScene(scene);
        window.show();
    }

    // ---------------- UTILITIES ----------------

    private TableColumn<Customer, ?> makeColumn(String title, String field, int width) {
        TableColumn<Customer, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(field));
        col.setPrefWidth(width);
        return col;
    }

    private void addField(GridPane grid, String label, TextField field, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }

    private boolean matchesFilter(Customer c, String filterBy, String text) {
        if (text == null || text.isEmpty()) return true;

        String lower = text.toLowerCase();

        return switch (filterBy) {
            case "Name" -> c.getName().toLowerCase().contains(lower);
            case "Address" -> c.getAddress().toLowerCase().contains(lower);
            case "Phone" -> c.getPhone().contains(lower);
            case "Age" -> String.valueOf(c.getAge()).contains(lower);
            case "People" -> String.valueOf(c.getPeople()).contains(lower);
            case "Days" -> String.valueOf(c.getDays()).contains(lower);
            case "Room" -> String.valueOf(c.getRoomNo()).contains(lower);
            default -> true;
        };
    }

    // ---------------- DB LOAD ----------------
    private void loadData() {
        masterList.clear();

        String sql = "SELECT * FROM customers ORDER BY id";
        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                masterList.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getInt("age"),
                        rs.getInt("people"),
                        rs.getInt("days"),
                        rs.getInt("floor_no"),
                        rs.getInt("room_no")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- UPDATE ----------------
    private void updateSelected() {
        Customer c = table.getSelectionModel().getSelectedItem();
        if (c == null) return;

        String sql = "UPDATE customers SET name=?, address=?, phone=?, age=?, people=?, days=? WHERE id=?";
        try (Connection con = connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.setInt(4, Integer.parseInt(ageField.getText()));
            stmt.setInt(5, Integer.parseInt(peopleField.getText()));
            stmt.setInt(6, Integer.parseInt(daysField.getText()));
            stmt.setInt(7, c.getId());

            stmt.executeUpdate();

            loadData();
            customers.setAll(masterList);
            clearFields();
            if (onChange != null) onChange.run();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- DELETE ----------------
    private void deleteSelected() {
        Customer c = table.getSelectionModel().getSelectedItem();
        if (c == null) return;

        try (Connection con = connect()) {

            PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM customers WHERE id=?");
            deleteStmt.setInt(1, c.getId());
            deleteStmt.executeUpdate();

            // Renumber IDs
            PreparedStatement renumberStmt = con.prepareStatement(
                    "WITH ordered AS (" +
                            "SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS new_id FROM customers" +
                            ") UPDATE customers SET id = ordered.new_id FROM ordered WHERE customers.id = ordered.id"
            );
            renumberStmt.executeUpdate();

            PreparedStatement resetSeq = con.prepareStatement(
                    "SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id)+1 FROM customers), 1), false)"
            );
            resetSeq.executeQuery();

            loadData();
            customers.setAll(masterList);
            clearFields();
            if (onChange != null) onChange.run();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- CONNECTION ----------------
    private Connection connect() throws Exception {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/hostel_management",
                "postgres",
                "Project(JAVA)"
        );
    }

    // ---------------- CLEAR FIELDS ----------------
    private void clearFields() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        ageField.clear();
        peopleField.clear();
        daysField.clear();
    }
}
