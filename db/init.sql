-- Usuarios del POS para la estrategia de base de datos.
-- Las claves están cifradas con BCrypt; nunca se guardan en texto plano.
--   jperez  / 2345  (CAJERO)
--   fsuarez / 3456  (SUPERVISOR)
CREATE TABLE usuarios (
    usuario VARCHAR(50)  NOT NULL PRIMARY KEY,
    clave   VARCHAR(100) NOT NULL,
    rol     VARCHAR(20)  NOT NULL
);

INSERT INTO usuarios (usuario, clave, rol) VALUES
    ('jperez',  '$2a$10$tIyRplKwMDzTOC0k8G/wAOg.AHojzPsyPzygXd4w1i8zmwEZW.o26', 'CAJERO'),
    ('fsuarez', '$2a$10$gThp6kQIAsnh9eoDqlZzXOhr4UAcYa5zGYxduFZW1PCNJWiL/j4iK', 'SUPERVISOR');
