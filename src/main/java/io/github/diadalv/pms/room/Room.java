package io.github.diadalv.pms.room;

public class Room {
    private final String number;
    private final int floor;
    private final String type;
    private HousekeepingStatus housekeepingStatus;

    public Room(String number, int floor, String type) {
        this(number, floor, type, HousekeepingStatus.CLEAN);
    }

    private Room(String number, int floor, String type, HousekeepingStatus housekeepingStatus) {
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.housekeepingStatus = housekeepingStatus;
    }

    public static Room restore(String number, int floor, String type, HousekeepingStatus housekeepingStatus) {
        return new Room(number, floor, type, housekeepingStatus);
    }

    public String getNumber() {
        return number;
    }

    public int getFloor() {
        return floor;
    }

    public String getType() {
        return type;
    }

    public HousekeepingStatus getHousekeepingStatus() {
        return housekeepingStatus;
    }

    public void markDirty() {
        housekeepingStatus = HousekeepingStatus.DIRTY;
    }

    public void markClean() {
        housekeepingStatus = HousekeepingStatus.CLEAN;
    }

    public void markInspected() {
        if (housekeepingStatus != HousekeepingStatus.CLEAN) {
            throw new IllegalStateException("Room " + number + " must be clean before inspection");
        }
        housekeepingStatus = HousekeepingStatus.INSPECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room other)) {
            return false;
        }
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}