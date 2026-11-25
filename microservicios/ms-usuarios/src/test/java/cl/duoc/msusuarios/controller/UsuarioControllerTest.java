package cl.duoc.msusuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.exception.CredencialesInvalidasException;
import cl.duoc.msusuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba la capa web (rutas, códigos HTTP y JSON) con MockMvc.
 * El servicio se mockea con @MockitoBean, así no se levanta la BD.
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @Test
    void loginCorrectoResponde200ConElUsuario() throws Exception {
        when(service.login(any(LoginRequest.class)))
                .thenReturn(new UsuarioResponse(1L, "Cliente Demo", "cliente@demo.cl", "CLIENTE"));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@demo.cl\",\"clave\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cliente Demo"))
                .andExpect(jsonPath("$.clave").doesNotExist());
    }

    @Test
    void loginIncorrectoResponde401() throws Exception {
        when(service.login(any(LoginRequest.class))).thenThrow(new CredencialesInvalidasException());

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@demo.cl\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
    }

    @Test
    void loginConCorreoInvalidoResponde400() throws Exception {
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"no-es-correo\",\"clave\":\"demo123\"}"))
                .andExpect(status().isBadRequest());
    }
}
