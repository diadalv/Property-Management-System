package io.github.diadalv.pms.room;

import io.github.diadalv.pms.hotel.Hotel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {

    private final Hotel hotel;

    public RoomController(Hotel hotel) {
        this.hotel = hotel;
    }

    @GetMapping("/rooms")
    public List<Room> getRooms() {
        return hotel.getRooms();
    }
}