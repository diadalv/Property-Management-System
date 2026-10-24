package io.github.diadalv.pms.room;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(String roomNumber) {
        super("Room " + roomNumber + " does not exist");
    }
}