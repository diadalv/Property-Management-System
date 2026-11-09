package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.config.SecurityConfig;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.room.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "RECEPTION")
class ReservationControllerTest {

    private static final String REQUEST_JSON = """
            {
              "firstName": "Elena",
              "lastName": "Ivanova",
              "email": null,
              "phone": null,
              "roomNumber": "204",
              "arrivalDate": "2026-12-01",
              "departureDate": "2026-12-04",
              "ratePerNight": 85.00
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Hotel hotel;

    @Test
    void createReservationReturns201() throws Exception {
        Room room = new Room("204", 2, "Double");
        Reservation reservation = new Reservation(new Guest("Elena", "Ivanova", null, null), room,
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 4), new BigDecimal("85.00"));
        when(hotel.findRoom("204")).thenReturn(room);
        when(hotel.book(any(), any(), any(), any(), any())).thenReturn(reservation);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value("204"))
                .andExpect(jsonPath("$.arrivalDate").value("2026-12-01"))
                .andExpect(jsonPath("$.departureDate").value("2026-12-04"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void createReservationForBookedRoomReturns409() throws Exception {
        when(hotel.findRoom("204")).thenReturn(new Room("204", 2, "Double"));
        when(hotel.book(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("Room 204 is not available for these dates"));

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Room 204 is not available for these dates"));
    }

    @Test
    void createReservationWithInvalidDatesReturns400() throws Exception {
        when(hotel.findRoom("204")).thenReturn(new Room("204", 2, "Double"));
        when(hotel.book(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Departure date must be after arrival date"));

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Departure date must be after arrival date"));
    }

    @Test
    void getUnknownReservationReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(hotel.findReservation(id)).thenThrow(new ReservationNotFoundException(id));

        mockMvc.perform(get("/reservations/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation with ID: " + id + " does not exist."));
    }

    @Test
    @WithMockUser(roles = "HOUSEKEEPING")
    void housekeepingCannotSeeReservations() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isForbidden());
    }
}