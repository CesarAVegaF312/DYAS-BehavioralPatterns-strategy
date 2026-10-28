package edu.unisabana.dyas.pos.autenticacion;

import java.util.Optional;

/**
 * Estrategia del patrón Strategy: un método de autenticación intercambiable.
 *
 * NO MODIFICAR ESTA INTERFAZ.
 */
public interface EstrategiaAutenticacion {

    /**
     * Verifica las credenciales.
     *
     * @return el usuario autenticado, o vacío si el usuario no existe o la clave no coincide.
     */
    Optional<UsuarioAutenticado> autenticar(String usuario, String clave);
}
