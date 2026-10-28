package edu.unisabana.dyas.pos.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.unisabana.dyas.pos.autenticacion.ServicioAutenticacion;
import edu.unisabana.dyas.pos.autenticacion.UsuarioAutenticado;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Contrato HTTP del endpoint de login: 200, 401 y 400.
 */
@WebMvcTest(AutenticacionController.class)
class AutenticacionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ServicioAutenticacion servicio;

    @Test
    void respondeOkConElUsuarioAutenticado() throws Exception {
        given(servicio.autenticar("jperez", "2345"))
                .willReturn(Optional.of(new UsuarioAutenticado("jperez", "CAJERO", "bd")));

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"jperez\",\"clave\":\"2345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$.rol").value("CAJERO"))
                .andExpect(jsonPath("$.estrategia").value("bd"));
    }

    @Test
    void respondeUnauthorizedSiLasCredencialesNoSonValidas() throws Exception {
        given(servicio.autenticar(any(), any())).willReturn(Optional.empty());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"jperez\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void respondeBadRequestSiFaltaLaClave() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"jperez\"}"))
                .andExpect(status().isBadRequest());
    }
}
