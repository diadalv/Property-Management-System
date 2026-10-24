package io.github.diadalv.pms.room;

public class Room {
    private String number;
    private int floor;
    private String type;
    private HousekeepingStatus housekeepingStatus;

    public Room(String number, int floor, String type) {
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.housekeepingStatus = HousekeepingStatus.CLEAN;
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

}