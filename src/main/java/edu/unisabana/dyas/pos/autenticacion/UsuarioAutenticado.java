package edu.unisabana.dyas.pos.autenticacion;

/**
 * Resultado de una autenticación exitosa.
 *
 * @param estrategia nombre de la estrategia que validó las credenciales (memoria, bd o xml)
 */
public record UsuarioAutenticado(String usuario, String rol, String estrategia) {
}
