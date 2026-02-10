import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MainView extends Application {

    private static final int TOTAL_ROOMS = 20;

    private int selectedRoom = -1;
    private int currentFloor = 1;

    private Label floorLabel;
    private GridPane roomGrid;

    // FAST in-memory cache
    private Map<String, RoomInfo> roomCache = new HashMap<>();

    @Override
    public void start(Stage stage) {

        // LOAD ROOM DATA ONCE
        loadRoomDataFromDatabase();

        // LEFT MENU -----------------------------------------
        VBox leftMenu = new VBox(15);
        leftMenu.setPadding(new Insets(20));
        leftMenu.setAlignment(Pos.TOP_CENTER);
        leftMenu.setPrefWidth(180);

        VBox floorButtons = new VBox(10);
        floorButtons.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 5; i++) {
            Button btn = new Button("Floor " + i);
            btn.getStyleClass().add("menu-btn");
            int floorNO = i;
            btn.setOnAction(e -> updateFloor(floorNO));
            floorButtons.getChildren().add(btn);
        }

        leftMenu.getChildren().add(floorButtons);

        // RIGHT MENU -----------------------------------------
        VBox rightControls = new VBox(15);
        rightControls.setPadding(new Insets(20));
        rightControls.setAlignment(Pos.TOP_CENTER);

        Button btnBookRoom = new Button("Book Room");
        Button btnCustomerDetails = new Button("Customer Details");

        btnBookRoom.getStyleClass().add("action-btn");
        btnCustomerDetails.getStyleClass().add("action-btn");

        rightControls.getChildren().addAll(btnBookRoom, btnCustomerDetails);

        // BUTTON LOGIC -----------------------------------------
        btnBookRoom.setOnAction(e -> {
            if (selectedRoom == -1) {
                System.out.println("No room selected!");
                return;
            }

            RoomInfo info = roomCache.get(currentFloor + "-" + selectedRoom);

            if (info.booked) {
                System.out.println("Room already booked!");
                return;
            }

            new CustomerDetailsWindow().showWindow(currentFloor, selectedRoom, () -> {
                loadRoomDataFromDatabase();
                updateRoomGrid(currentFloor);
            });

            selectedRoom = -1;
        });

        btnCustomerDetails.setOnAction(e -> new CustomerManagementWindow(this::forceRefresh).showWindow());

        // GRID --------------------------------------------------
        floorLabel = new Label("FLOOR " + currentFloor);
        floorLabel.getStyleClass().add("floor-label");

        roomGrid = new GridPane();
        roomGrid.setHgap(15);
        roomGrid.setVgap(15);
        roomGrid.setPadding(new Insets(30));
        roomGrid.setAlignment(Pos.CENTER);

        updateRoomGrid(currentFloor);

        VBox centerLayout = new VBox(10, floorLabel, roomGrid);
        centerLayout.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setLeft(leftMenu);
        root.setCenter(centerLayout);
        root.setRight(rightControls);

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("manager-style.css").toExternalForm());

        stage.setTitle("Hotel Room Booking System");
        stage.setScene(scene);
        stage.show();
    }

    // LOAD DATA INTO CACHE ----------------------------------------
    private void loadRoomDataFromDatabase() {

        roomCache.clear();

        String sql =
                "SELECT r.floor_no, r.room_no, r.price, r.capacity, " +
                "       CASE WHEN c.room_no IS NULL THEN false ELSE true END AS booked " +
                "FROM rooms r " +
                "LEFT JOIN customers c " +
                "ON r.floor_no = c.floor_no AND r.room_no = c.room_no";

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String pass = "Project(JAVA)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RoomInfo info = new RoomInfo(
                        rs.getInt("floor_no"),
                        rs.getInt("room_no"),
                        rs.getInt("price"),
                        rs.getInt("capacity"),
                        rs.getBoolean("booked")
                );

                roomCache.put(info.floor + "-" + info.room, info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE FLOOR -------------------------------------------------
    private void updateFloor(int floorNumber) {
        currentFloor = floorNumber;
        floorLabel.setText("FLOOR " + floorNumber);
        updateRoomGrid(floorNumber);
    }

    // REDRAW GRID FROM CACHE (NO DB ACCESS) ------------------------
    private void updateRoomGrid(int floorNumber) {

        roomGrid.getChildren().clear();

        for (int i = 1; i <= TOTAL_ROOMS; i++) {

            String key = floorNumber + "-" + i;
            RoomInfo info = roomCache.get(key);

            Rectangle rect = new Rectangle(70, 70);
            rect.getStyleClass().add("room");

            if (info.booked) {
                rect.getStyleClass().add("room-booked");
            } else {
                rect.getStyleClass().add("room-available");
            }

            int displayRoomNo = floorNumber * 100 + i;
            Label roomLabel = new Label(String.valueOf(displayRoomNo));
            roomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

            StackPane stack = new StackPane(rect, roomLabel);

            // Tooltip
            Tooltip tooltip = new Tooltip(
                    "Price: ₹" + info.price +
                    "\nRoom for " + info.capacity + " persons" +
                    "\nRoom is " + (info.booked ? "BOOKED" : "Available")
            );
            Tooltip.install(stack, tooltip);

            final int roomNO = i;

            stack.setOnMouseClicked(e -> {

                if (info.booked) {
                    System.out.println("Room already booked!");
                    return;
                }

                roomGrid.getChildren().forEach(node -> {
                    if (node instanceof StackPane sp) {
                        Rectangle r = (Rectangle) sp.getChildren().get(0);
                        r.getStyleClass().remove("room-selected");
                        if (!r.getStyleClass().contains("room-booked"))
                            r.getStyleClass().add("room-available");
                    }
                });

                rect.getStyleClass().remove("room-available");
                rect.getStyleClass().add("room-selected");

                selectedRoom = roomNO;
            });

            roomGrid.add(stack, (i - 1) % 5, (i - 1) / 5);
        }
    }
    public void forceRefresh() {
        loadRoomDataFromDatabase();
        updateRoomGrid(currentFloor);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
