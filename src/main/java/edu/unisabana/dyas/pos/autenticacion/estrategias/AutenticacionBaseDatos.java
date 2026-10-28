package edu.unisabana.dyas.pos.autenticacion.estrategias;

/**
 * Estrategia concreta: usuarios de la tabla usuarios de MySQL.
 *
 * TODO (Paso 4):
 *   - Implemente EstrategiaAutenticacion y registre la clase como bean con el nombre "bd".
 *   - Consulte con JdbcTemplate y una consulta parametrizada (nunca concatene el usuario en el SQL).
 *   - Compare la clave con PasswordEncoder.matches(...): en la tabla está cifrada con BCrypt.
 */
public class AutenticacionBaseDatos {
}
