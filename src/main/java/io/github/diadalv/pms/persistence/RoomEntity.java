package io.github.diadalv.pms.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "floor")
    private int floor;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "housekeeping_status")
    private String housekeepingStatus;

    protected RoomEntity() {
    }

    public RoomEntity(String roomNumber, int floor, String roomType, String housekeepingStatus) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        this.housekeepingStatus = housekeepingStatus;
    }

    public String getRoomNumber() { return roomNumber; }
    public int getFloor() { return floor; }
    public String getRoomType() { return roomType; }
    public String getHousekeepingStatus() { return housekeepingStatus; }

    public void setHousekeepingStatus(String housekeepingStatus) {
        this.housekeepingStatus = housekeepingStatus;
    }
}