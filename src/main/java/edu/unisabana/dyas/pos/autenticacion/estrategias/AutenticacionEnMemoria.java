package edu.unisabana.dyas.pos.autenticacion.estrategias;

import edu.unisabana.dyas.pos.autenticacion.EstrategiaAutenticacion;
import edu.unisabana.dyas.pos.autenticacion.UsuarioAutenticado;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Estrategia concreta: usuarios fijos en memoria, para demostraciones.
 *
 * Ejemplo guía: las demás estrategias siguen esta misma forma.
 * El nombre del bean ("memoria") es el valor que se usa en AUTH_ESTRATEGIA.
 */
@Component("memoria")
public class AutenticacionEnMemoria implements EstrategiaAutenticacion {

    private record Cuenta(String claveCifrada, String rol) {
    }

    // admin / admin123. La clave se guarda cifrada con BCrypt, igual que en MySQL y en el XML.
    private static final Map<String, Cuenta> CUENTAS = Map.of(
            "admin", new Cuenta("$2a$10$itxBwC3celWT4qrMpWfwweT/AW0oGGg0pd2zfAcLtCKvf99RsYnL.", "ADMIN"));

    private final PasswordEncoder passwordEncoder;

    public AutenticacionEnMemoria(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UsuarioAutenticado> autenticar(String usuario, String clave) {
        return Optional.ofNullable(CUENTAS.get(usuario))
                .filter(cuenta -> passwordEncoder.matches(clave, cuenta.claveCifrada()))
                .map(cuenta -> new UsuarioAutenticado(usuario, cuenta.rol(), "memoria"));
    }
}
