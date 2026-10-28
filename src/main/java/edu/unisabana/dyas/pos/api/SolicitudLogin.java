package edu.unisabana.dyas.pos.api;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de la petición POST /api/auth/login.
 */
public record SolicitudLogin(@NotBlank String usuario, @NotBlank String clave) {
}
