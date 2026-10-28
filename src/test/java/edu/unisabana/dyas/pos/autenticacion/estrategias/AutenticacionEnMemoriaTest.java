package edu.unisabana.dyas.pos.autenticacion.estrategias;

import static org.assertj.core.api.Assertions.assertThat;

import edu.unisabana.dyas.pos.autenticacion.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Pruebas de la estrategia de ejemplo. Úselas como modelo para las de base de datos y XML.
 */
class AutenticacionEnMemoriaTest {

    private final AutenticacionEnMemoria estrategia = new AutenticacionEnMemoria(new BCryptPasswordEncoder());

    @Test
    void autenticaUnUsuarioConClaveCorrecta() {
        assertThat(estrategia.autenticar("admin", "admin123"))
                .contains(new UsuarioAutenticado("admin", "ADMIN", "memoria"));
    }

    @Test
    void rechazaUnaClaveIncorrecta() {
        assertThat(estrategia.autenticar("admin", "otra-clave")).isEmpty();
    }

    @Test
    void rechazaUnUsuarioInexistente() {
        assertThat(estrategia.autenticar("nadie", "admin123")).isEmpty();
    }

    @Test
    void rechazaUnIntentoDeInyeccion() {
        assertThat(estrategia.autenticar("' OR '1'='1", "' OR '1'='1")).isEmpty();
    }
}
