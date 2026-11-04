package io.github.diadalv.pms.room;

import java.util.List;

public interface RoomRepository {

    void save(Room room);

    List<Room> findAll();
}