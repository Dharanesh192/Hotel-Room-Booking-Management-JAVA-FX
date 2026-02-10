public class Customer {
    private int id;
    private String name;
    private String address;
    private String phone;
    private int age;
    private int people;
    private int days;
    private int floor_no;
    private int room_no;

    public int getFloorNo() {
        return floor_no;
    }

    public int getRoomNo() {
        return room_no;
    }


    public Customer(int id, String name, String address, String phone,
                    int age, int people, int days, int floor_no, int room_no) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.age = age;
        this.people = people;
        this.days = days;
        this.floor_no = floor_no;
        this.room_no = room_no;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }
    public int getPeople() { return people; }
    public int getDays() { return days; }
    public int getFloor_no() { return floor_no; }
    public int getRoom_no() { return room_no; }
}
