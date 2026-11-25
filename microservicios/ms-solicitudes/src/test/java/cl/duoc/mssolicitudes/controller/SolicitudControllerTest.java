package cl.duoc.mssolicitudes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.mssolicitudes.dto.SolicitudRequest;
import cl.duoc.mssolicitudes.exception.RecursoNoEncontradoException;
import cl.duoc.mssolicitudes.model.Solicitud;
import cl.duoc.mssolicitudes.service.SolicitudService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Prueba los endpoints (rutas, códigos HTTP y JSON) con el servicio mockeado. */
@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolicitudService service;

    private Solicitud ejemplo() {
        Solicitud s = new Solicitud();
        s.setId(1L);
        s.setCodigo("SOL-1A2B3C4D");
        s.setTitulo("Sitio web caído");
        s.setEstado("Recibida");
        return s;
    }

    @Test
    void listarDevuelve200ConLaLista() throws Exception {
        when(service.listar(null)).thenReturn(List.of(ejemplo()));

        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("SOL-1A2B3C4D"));
    }

    @Test
    void crearDevuelve201() throws Exception {
        when(service.crear(any(SolicitudRequest.class))).thenReturn(ejemplo());

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Sitio web caído","descripcion":"El sitio no carga desde ayer",
                                 "categoria":"Incidente técnico","servicio":"www.cliente.cl"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("Recibida"));
    }

    @Test
    void crearConTituloCortoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Hola\",\"descripcion\":\"El sitio no carga desde ayer\",\"categoria\":\"Hosting\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarInexistenteDevuelve404() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe la solicitud con id 99")).when(service).eliminar(99L);

        mockMvc.perform(delete("/api/solicitudes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la solicitud con id 99"));
    }
}
