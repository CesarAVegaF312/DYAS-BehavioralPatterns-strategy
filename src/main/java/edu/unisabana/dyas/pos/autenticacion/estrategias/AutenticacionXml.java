package edu.unisabana.dyas.pos.autenticacion.estrategias;

/**
 * Estrategia concreta: usuarios de un archivo XML.
 *
 * TODO (Paso 5):
 *   - Implemente EstrategiaAutenticacion y registre la clase como bean con el nombre "xml".
 *   - Lea el archivo indicado por la propiedad pos.autenticacion.xml.ruta.
 *   - Desactive las entidades externas del parser (XXE) y no arme expresiones XPath concatenando el usuario.
 *   - Compare la clave con PasswordEncoder.matches(...): en el XML está cifrada con BCrypt.
 */
public class AutenticacionXml {
}
