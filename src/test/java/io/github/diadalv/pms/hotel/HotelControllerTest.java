package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@Import(SecurityConfig.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Hotel hotel;

    @Test
    @WithMockUser(roles = "RECEPTION")
    void receptionCannotRunNightAudit() throws Exception {
        mockMvc.perform(post("/night-audit").param("date", "2026-11-09"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanRunNightAudit() throws Exception {
        mockMvc.perform(post("/night-audit").param("date", "2026-11-09"))
                .andExpect(status().isOk());
    }
}