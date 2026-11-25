package cl.duoc.mssolicitudes.dto;

import jakarta.validation.constraints.NotBlank;

/** JSON para cambiar solo el estado: {"estado": "En proceso"} */
public record EstadoRequest(
        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}
