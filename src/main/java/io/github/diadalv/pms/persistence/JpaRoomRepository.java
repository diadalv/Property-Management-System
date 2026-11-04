package io.github.diadalv.pms.persistence;

import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomRepository;

import java.util.List;

public class JpaRoomRepository implements RoomRepository {

    private final RoomJpaRepository jpaRepository;

    public JpaRoomRepository(RoomJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Room room) {
        jpaRepository.save(RoomMapper.toEntity(room));
    }

    @Override
    public List<Room> findAll() {
        return jpaRepository.findAll().stream()
                .map(RoomMapper::toDomain)
                .toList();
    }
}