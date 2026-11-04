package io.github.diadalv.pms.room;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Room> rooms = new LinkedHashMap<>();

    @Override
    public void save(Room room) {
        rooms.put(room.getNumber(), copy(room));
    }

    @Override
    public List<Room> findAll() {
        return rooms.values().stream()
                .map(room -> copy(room))
                .toList();
    }

    private Room copy(Room room) {
        return Room.restore(room.getNumber(), room.getFloor(), room.getType(),
                room.getHousekeepingStatus());
    }
}