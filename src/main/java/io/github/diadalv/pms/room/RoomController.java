package io.github.diadalv.pms.room;

import io.github.diadalv.pms.hotel.Hotel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {

    private final Hotel hotel;

    public RoomController(Hotel hotel) {
        this.hotel = hotel;
    }

    @GetMapping("/rooms")
    public List<RoomResponse> getRooms() {
        return hotel.getRooms().stream()
                .map(room -> RoomResponse.from(room))
                .toList();
    }

    @GetMapping("/rooms/{number}")
    public RoomResponse getRoom(@PathVariable String number) {
        return RoomResponse.from(hotel.findRoom(number));
    }

    @PostMapping("/rooms/{number}/clean")
    public RoomResponse markClean(@PathVariable String number) {
        Room room = hotel.findRoom(number);
        room.markClean();
        return RoomResponse.from(room);
    }

    @PostMapping("/rooms/{number}/dirty")
    public RoomResponse markDirty(@PathVariable String number){
        Room room = hotel.findRoom(number);
        room.markDirty();
        return RoomResponse.from(room);
    }

    @PostMapping("/rooms/{number}/inspect")
    public RoomResponse markInspect(@PathVariable String number) {
        Room room = hotel.findRoom(number);
        room.markInspected();
        return RoomResponse.from(room);
    }
}