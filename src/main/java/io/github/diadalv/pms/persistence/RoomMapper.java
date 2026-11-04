package io.github.diadalv.pms.persistence;

import io.github.diadalv.pms.room.HousekeepingStatus;
import io.github.diadalv.pms.room.Room;

public class RoomMapper {

    public static RoomEntity toEntity(Room room) {
        return new RoomEntity(room.getNumber(), room.getFloor(), room.getType(),
                room.getHousekeepingStatus().name());
    }

    public static Room toDomain(RoomEntity entity) {
        return Room.restore(entity.getRoomNumber(), entity.getFloor(), entity.getRoomType(),
                HousekeepingStatus.valueOf(entity.getHousekeepingStatus()));
    }
}