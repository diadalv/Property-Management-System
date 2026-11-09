package io.github.diadalv.pms.room;

import io.github.diadalv.pms.hotel.Hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Hotel hotel;

    @Test
    void getRoomReturnsRoomAsJson() throws Exception {
        when(hotel.findRoom("101")).thenReturn(new Room("101", 1, "Single"));

        mockMvc.perform(get("/rooms/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("101"))
                .andExpect(jsonPath("$.floor").value(1))
                .andExpect(jsonPath("$.type").value("Single"))
                .andExpect(jsonPath("$.housekeepingStatus").value("CLEAN"));
    }

    @Test
    void getUnknownRoomReturns404() throws Exception {
        when(hotel.findRoom("999")).thenThrow(new RoomNotFoundException("999"));

        mockMvc.perform(get("/rooms/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Room 999 does not exist"));
    }

    @Test
    void inspectingDirtyRoomReturns409() throws Exception {
        when(hotel.markRoomInspected("101"))
                .thenThrow(new IllegalStateException("Room 101 must be clean before inspection"));

        mockMvc.perform(post("/rooms/101/inspect"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Room 101 must be clean before inspection"));
    }

    @Test
    void markCleanReturnsCleanRoom() throws Exception {
        when(hotel.markRoomClean("101")).thenReturn(new Room("101", 1, "Single"));

        mockMvc.perform(post("/rooms/101/clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.housekeepingStatus").value("CLEAN"));
    }
}