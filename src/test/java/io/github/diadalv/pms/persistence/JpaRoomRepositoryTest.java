package io.github.diadalv.pms.persistence;

import io.github.diadalv.pms.room.HousekeepingStatus;
import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class JpaRoomRepositoryTest {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withStartupTimeout(Duration.ofMinutes(5));

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void roomStatusIsSavedInOracleAndLoadedAgain() {
        Room room = new Room("501", 5, "Suite");
        roomRepository.save(room);

        room.markDirty();
        roomRepository.save(room);

        List<Room> rooms501 = roomRepository.findAll().stream()
                .filter(r -> r.getNumber().equals("501"))
                .toList();

        assertEquals(1, rooms501.size());
        assertEquals(HousekeepingStatus.DIRTY, rooms501.get(0).getHousekeepingStatus());
    }
}