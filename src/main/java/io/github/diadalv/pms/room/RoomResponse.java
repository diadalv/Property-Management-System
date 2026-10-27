package io.github.diadalv.pms.room;

public record RoomResponse(String number, int floor, String type, HousekeepingStatus housekeepingStatus) {
    public static RoomResponse from(Room room) {
        return new RoomResponse(room.getNumber(), room.getFloor(), room.getType(), room.getHousekeepingStatus());
    }

}
