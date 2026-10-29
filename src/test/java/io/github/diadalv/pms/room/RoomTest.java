package io.github.diadalv.pms.room;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomTest {

    @Test
    void cannotInspectDirtyRoom() {
        Room room = new Room("101", 1, "Single");
        room.markDirty();
        assertThrows(IllegalStateException.class, () -> room.markInspected());
    }
}
