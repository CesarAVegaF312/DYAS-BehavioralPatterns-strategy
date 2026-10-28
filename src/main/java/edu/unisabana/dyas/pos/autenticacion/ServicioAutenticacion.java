package edu.unisabana.dyas.pos.autenticacion;

import edu.unisabana.dyas.pos.autenticacion.estrategias.AutenticacionEnMemoria;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Contexto del patrón Strategy: delega la autenticación en la estrategia configurada.
 *
 * TODO (Paso 3): hoy el servicio crea siempre la estrategia en memoria y no usa la
 * propiedad pos.autenticacion.estrategia. Cámbielo para que:
 *   - use la estrategia cuyo nombre indica esa propiedad (variable AUTH_ESTRATEGIA del .env);
 *   - no tenga if, switch ni new sobre las estrategias concretas;
 *   - no importe ninguna clase del paquete estrategias;
 *   - falle al arrancar si la propiedad nombra una estrategia que no existe.
 */
@Service
public class ServicioAutenticacion {

    private final EstrategiaAutenticacion estrategia;

    public ServicioAutenticacion(PasswordEncoder passwordEncoder) {
        this.estrategia = new AutenticacionEnMemoria(passwordEncoder);
    }

    public Optional<UsuarioAutenticado> autenticar(String usuario, String clave) {
        return estrategia.autenticar(usuario, clave);
    }
}
