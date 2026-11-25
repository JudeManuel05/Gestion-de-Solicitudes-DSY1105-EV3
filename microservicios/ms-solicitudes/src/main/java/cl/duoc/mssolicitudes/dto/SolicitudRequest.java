package cl.duoc.mssolicitudes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON que envía la app para crear o editar una solicitud. */
public record SolicitudRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener al menos 10 caracteres")
        String descripcion,

        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        String servicio,
        String evidenciaUri,
        String correoCliente
) {
}
