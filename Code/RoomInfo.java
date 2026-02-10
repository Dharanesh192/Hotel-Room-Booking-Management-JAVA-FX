public class RoomInfo {
    public int floor;
    public int room;
    public int price;
    public int capacity;
    public boolean booked;

    public RoomInfo(int floor, int room, int price, int capacity, boolean booked) {
        this.floor = floor;
        this.room = room;
        this.price = price;
        this.capacity = capacity;
        this.booked = booked;
    }
}
