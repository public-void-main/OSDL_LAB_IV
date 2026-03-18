import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HelloFX extends Application {

    // ================= ENUM =================
    enum RoomType { SINGLE, DOUBLE, DELUXE }

    // ================= ROOM CLASS =================
    public static class Room implements Serializable {
        private int roomNumber;
        private RoomType type;
        private double price;
        private boolean available;

        public Room(int roomNumber, RoomType type, double price) {
            this.roomNumber = roomNumber;
            this.type = type;
            this.price = price;
            this.available = true;
        }

        public int getRoomNumber() { return roomNumber; }
        public RoomType getType() { return type; }
        public double getPrice() { return price; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }

    // ================= CUSTOMER CLASS =================
    public static class Customer implements Serializable {
        private String name;
        private String contact;
        private int roomNumber;

        public Customer(String name, String contact, int roomNumber) {
            this.name = name;
            this.contact = contact;
            this.roomNumber = roomNumber;
        }

        public String getName() { return name; }
        public String getContact() { return contact; }
        public int getRoomNumber() { return roomNumber; }
    }

    // ================= SERVICE CLASS =================
    static class HotelService {
        private List<Room> rooms = new ArrayList<>();
        private List<Customer> customers = new ArrayList<>();

        public String addRoom(Room room) {
            boolean exists = rooms.stream()
                    .anyMatch(r -> r.getRoomNumber() == room.getRoomNumber());
            if (exists) return "Room number already exists!";
            rooms.add(room);
            return "Room added successfully!";
        }

        public List<Room> getAllRooms() { return rooms; }

        public List<Room> getAvailableRooms() {
            return rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
        }

        public synchronized String bookRoom(String name, String contact, int roomNo) {
            Optional<Room> roomOpt = rooms.stream()
                    .filter(r -> r.getRoomNumber() == roomNo)
                    .findFirst();
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                if (!room.isAvailable()) return "Room already booked!";
                room.setAvailable(false);
                customers.add(new Customer(name, contact, roomNo));
                return "Booking successful!";
            }
            return "Room not found!";
        }

        public synchronized String checkout(int roomNo) {
            for (Room room : rooms) {
                if (room.getRoomNumber() == roomNo) {
                    if (room.isAvailable()) return "Room is not booked!";
                    room.setAvailable(true);
                    customers.removeIf(c -> c.getRoomNumber() == roomNo);
                    return "Checkout successful!";
                }
            }
            return "Room not found!";
        }

        public List<Customer> getCustomers() { return customers; }

        public String getBookedBy(int roomNo) {
            return customers.stream()
                    .filter(c -> c.getRoomNumber() == roomNo)
                    .map(Customer::getName)
                    .findFirst()
                    .orElse("");
        }
    }

    // ================= DATA STORE =================
    static class DataStore {
        public static void save(List<Room> rooms, List<Customer> customers) {
            new Thread(() -> {
                try (ObjectOutputStream oos =
                             new ObjectOutputStream(new FileOutputStream("hotel.dat"))) {
                    oos.writeObject(rooms);
                    oos.writeObject(customers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // ================= MAIN =================
    private HotelService service = new HotelService();
    private TableView<Room> table = new TableView<>();
    private ObservableList<Room> roomData = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        // Input fields
        TextField roomNoField = new TextField();
        TextField priceField = new TextField();
        ComboBox<RoomType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(RoomType.values());

        TextField nameField = new TextField();
        TextField contactField = new TextField();

        Label message = new Label();

        // Block non-numeric contact input
        contactField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) contactField.setText(newVal.replaceAll("[^\\d]", ""));
        });

        // Buttons
        Button addRoomBtn = new Button("Add Room");
        Button bookBtn = new Button("Book Room");
        Button checkoutBtn = new Button("Checkout");
        Button showAvailableBtn = new Button("Show Available");
        Button saveBtn = new Button("Save Data");

        // Table columns
        TableColumn<Room, Integer> colNo = new TableColumn<>("Room No");
        colNo.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, RoomType> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Room, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Room, Boolean> colAvail = new TableColumn<>("Available");
        colAvail.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableColumn<Room, String> colBookedBy = new TableColumn<>("Booked By");
        colBookedBy.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(service.getBookedBy(cellData.getValue().getRoomNumber()))
        );

        table.getColumns().addAll(colNo, colType, colPrice, colAvail, colBookedBy);
        table.setItems(roomData);
        table.setPrefHeight(200);

        // Actions
        addRoomBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());
                double price = Double.parseDouble(priceField.getText());
                if (no <= 0 || price <= 0) { message.setText("Room number & price must be positive!"); return; }
                if (typeBox.getValue() == null) { message.setText("Select room type!"); return; }

                String result = service.addRoom(new Room(no, typeBox.getValue(), price));
                message.setText(result);
                if (result.contains("success")) {
                    roomNoField.clear(); priceField.clear(); typeBox.setValue(null);
                    updateTable();
                }
            } catch (Exception ex) { message.setText("Invalid input!"); }
        });

        bookBtn.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(roomNoField.getText());
                String contact = contactField.getText();
                String name = nameField.getText();
                if (contact.isEmpty() || !contact.matches("\\d+")) { message.setText("Contact must be numeric!"); return; }
                if (name.isEmpty()) { message.setText("Customer name cannot be empty!"); return; }

                String result = service.bookRoom(name, contact, roomNo);
                message.setText(result);
                if (result.contains("successful")) { nameField.clear(); contactField.clear(); updateTable(); }
            } catch (Exception ex) { message.setText("Error booking!"); }
        });

        checkoutBtn.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(roomNoField.getText());
                String result = service.checkout(roomNo);
                message.setText(result);
                updateTable();
            } catch (Exception ex) { message.setText("Invalid room!"); }
        });

        showAvailableBtn.setOnAction(e -> {
            List<Room> available = service.getAvailableRooms();
            message.setText("Available Rooms: " + available.size());
        });

        saveBtn.setOnAction(e -> {
            DataStore.save(service.getAllRooms(), service.getCustomers());
            message.setText("Data saved!");
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Room No"), 0, 0); grid.add(roomNoField, 1, 0);
        grid.add(new Label("Type"), 0, 1); grid.add(typeBox, 1, 1);
        grid.add(new Label("Price"), 0, 2); grid.add(priceField, 1, 2);
        grid.add(new Label("Name"), 0, 3); grid.add(nameField, 1, 3);
        grid.add(new Label("Contact"), 0, 4); grid.add(contactField, 1, 4);

        VBox buttons = new VBox(10, addRoomBtn, bookBtn, checkoutBtn, showAvailableBtn, saveBtn);
        VBox root = new VBox(15, grid, buttons, message, table);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene); stage.setTitle("Hotel Management System"); stage.show();
    }

    private void updateTable() { roomData.setAll(service.getAllRooms()); }

    public static void main(String[] args) { launch(); }
}