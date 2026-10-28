package edu.unisabana.dyas.pos.api;

import edu.unisabana.dyas.pos.autenticacion.ServicioAutenticacion;
import edu.unisabana.dyas.pos.autenticacion.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cliente del patrón Strategy: solo conoce el servicio de autenticación.
 *
 * NO MODIFICAR ESTA CLASE.
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    private final ServicioAutenticacion servicio;

    public AutenticacionController(ServicioAutenticacion servicio) {
        this.servicio = servicio;
    }

    /**
     * 200 con el usuario y su rol si las credenciales son válidas; 401 si no lo son.
     * Un cuerpo incompleto responde 400 (lo hace Spring por la validación de SolicitudLogin).
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioAutenticado> login(@Valid @RequestBody SolicitudLogin solicitud) {
        return servicio.autenticar(solicitud.usuario(), solicitud.clave())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
