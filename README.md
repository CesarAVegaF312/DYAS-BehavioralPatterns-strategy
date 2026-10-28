# Taller del Patrón Strategy con Spring Boot y Docker

## Objetivo General

Aplicar el patrón **Strategy** en un servicio REST real: el método de autenticación (memoria, base de datos o archivo XML) se elige por configuración, sin modificar el código que lo usa, y todo el sistema se levanta con un solo comando de Docker Compose.

## Índice

- [Conceptos clave](#conceptos-clave)
- [CONOCE EL TALLER](#conoce-el-taller)
  - [Requisitos](#requisitos)
  - [Estructura del proyecto](#estructura-del-proyecto)
- [PASO A PASO](#paso-a-paso)
  - [Paso 1: Levantar el ambiente](#paso-1-levantar-el-ambiente)
  - [Paso 2: Estudiar la estrategia de ejemplo](#paso-2-estudiar-la-estrategia-de-ejemplo)
  - [Paso 3: Seleccionar la estrategia por configuración](#paso-3-seleccionar-la-estrategia-por-configuración)
  - [Paso 4: Estrategia con base de datos](#paso-4-estrategia-con-base-de-datos)
  - [Paso 5: Estrategia con archivo XML](#paso-5-estrategia-con-archivo-xml)
  - [Paso 6: Pruebas](#paso-6-pruebas)
- [Retos opcionales](#retos-opcionales)
- [Buenas prácticas](#buenas-prácticas)
- [Para entregar](#para-entregar-con-este-taller)
- [Resumen del taller](#hagamos-un-resumen)
- [Recursos recomendados](#recursos-recomendados)
- [Créditos y uso académico](#créditos-y-uso-académico)
- [Licencia](#licencia-de-uso)

---

## Conceptos clave

| Concepto | Qué es |
|---|---|
| **Strategy** | Patrón de comportamiento que define una familia de algoritmos, encapsula cada uno y los hace intercambiables. El cliente depende de la interfaz, no de las implementaciones. |
| **Contexto** | La clase que usa la estrategia. En este taller es el servicio de autenticación que consume el controlador REST. |
| **Inyección de dependencias** | Spring crea los objetos y se los entrega a quien los necesita. Permite elegir la estrategia sin `new` ni `if` en el código cliente. |
| **Perfil o propiedad de configuración** | Valor externo (`application.yml`, variable de entorno, archivo `.env`) que cambia el comportamiento sin recompilar. |
| **Docker Compose** | Herramienta que levanta varios contenedores (la aplicación y MySQL) con un solo archivo `compose.yaml`. |
| **Principio abierto/cerrado** | El sistema está abierto a nuevas estrategias y cerrado a modificaciones del código existente. |

---

## CONOCE EL TALLER

Un punto de venta (POS) necesita autenticar a sus usuarios. Cada cliente que compra el software guarda sus usuarios en un lugar distinto: algunos en una base de datos MySQL, otros en un archivo XML y, para demostraciones, en memoria. El equipo de producto quiere que **el mismo artefacto** sirva para todos los clientes y que el método se elija al desplegar.

El servicio expone un solo endpoint:

```http
POST /api/auth/login
Content-Type: application/json

{ "usuario": "jperez", "clave": "2345" }
```

| Caso | Respuesta |
|---|---|
| Credenciales válidas | `200 OK` con `{ "usuario": "jperez", "rol": "CAJERO", "estrategia": "bd" }` |
| Credenciales inválidas | `401 Unauthorized`, sin indicar si falló el usuario o la clave |
| Cuerpo incompleto | `400 Bad Request` |

### Requisitos

- JDK 21 y Maven 3.9 o superior (o el wrapper `./mvnw` incluido).
- Docker Desktop o Docker Engine con Docker Compose v2.
- Git y un IDE (VS Code, IntelliJ IDEA o Eclipse).
- Haber hecho el [Taller de Docker](https://github.com/CesarAVegaF312/DYAS-Docker), partes 1 y 2.

Verifique el ambiente antes de empezar:

```bash
java -version          # debe mostrar 21
./mvnw -version
docker compose version
```

### Estructura del proyecto

```text
.
├── compose.yaml                  # aplicación + MySQL
├── .env.example                  # variables de entorno; cópielo como .env
├── Dockerfile
├── mvnw, mvnw.cmd                # Maven Wrapper: no necesita instalar Maven
├── db/
│   └── init.sql                  # tabla de usuarios con claves cifradas (BCrypt)
├── src/main/java/edu/unisabana/dyas/pos/
│   ├── PosApplication.java
│   ├── api/
│   │   ├── AutenticacionController.java     # cliente: ya implementado, NO se modifica
│   │   └── SolicitudLogin.java              # cuerpo de la petición
│   ├── configuracion/
│   │   └── ConfiguracionSeguridad.java      # PasswordEncoder (BCrypt) para todas las estrategias
│   └── autenticacion/
│       ├── EstrategiaAutenticacion.java     # interfaz de la estrategia, NO se modifica
│       ├── UsuarioAutenticado.java          # resultado de una autenticación exitosa
│       ├── ServicioAutenticacion.java       # contexto: debe completarlo (Paso 3)
│       └── estrategias/
│           ├── AutenticacionEnMemoria.java  # resuelta como ejemplo guía
│           ├── AutenticacionBaseDatos.java  # debe implementarla (Paso 4)
│           └── AutenticacionXml.java        # debe implementarla (Paso 5)
├── src/main/resources/
│   ├── application.yml
│   └── usuarios.xml
└── src/test/java/...                        # pruebas del controlador y de la estrategia de ejemplo
```

---

## PASO A PASO

### Paso 1: Levantar el ambiente

1. Haga un *fork* del repositorio y clónelo:

   ```bash
   git clone https://github.com/<tu-usuario>/DYAS-BehavioralPatterns-strategy.git
   cd DYAS-BehavioralPatterns-strategy
   ```

2. Cree su archivo de variables a partir de la plantilla y revise su contenido:

   ```bash
   cp .env.example .env
   ```

3. Levante la aplicación y la base de datos:

   ```bash
   docker compose up --build -d
   docker compose ps          # ambos servicios deben aparecer como "healthy"
   ```

4. Pruebe el endpoint con la estrategia en memoria, que viene configurada por defecto:

   ```bash
   curl -i -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"usuario":"admin","clave":"admin123"}'
   ```

5. Conéctese a MySQL y revise la tabla de usuarios. Observe que las claves **no** están en texto plano:

   ```bash
   docker compose exec mysql mysql -u pos -p pos -e "SELECT usuario, rol, clave FROM usuarios;"
   ```

> Si el puerto 8080 o 3306 está ocupado, cambie `APP_PORT` o `DB_PORT` en el archivo `.env`.
>
> MySQL ejecuta `db/init.sql` solo la primera vez, cuando su volumen de datos está vacío. Si cambia ese archivo o la clave de MySQL en el `.env`, borre el volumen con `docker compose down -v` y vuelva a levantar el sistema.

### Paso 2: Estudiar la estrategia de ejemplo

Abra `EstrategiaAutenticacion.java` y `AutenticacionEnMemoria.java`. Responda en su informe:

1. ¿Qué método define la interfaz y qué devuelve cuando la autenticación falla? ¿Por qué no devuelve `null`?
2. ¿Cómo se registra `AutenticacionEnMemoria` como un *bean* de Spring y con qué nombre?
3. ¿Qué clase del proyecto cumple el rol de **contexto** del patrón? ¿Y cuál el de **cliente**?

Dibuje el diagrama de clases del patrón con las tres estrategias (dos de ellas aún sin implementar).

### Paso 3: Seleccionar la estrategia por configuración

Complete `ServicioAutenticacion` para que use la estrategia indicada por la propiedad `pos.autenticacion.estrategia` (variable de entorno `AUTH_ESTRATEGIA` en el `.env`). Condiciones:

- El servicio no puede contener `if`, `switch` ni `new` sobre las estrategias concretas.
- No puede importar ninguna clase del paquete `estrategias`.
- Si la propiedad nombra una estrategia que no existe, la aplicación debe **fallar al arrancar** con un mensaje claro, no al recibir la primera petición.

Pista: Spring puede inyectar todos los *beans* que implementan una interfaz en una colección o en un `Map`.

Compruebe el resultado cambiando `AUTH_ESTRATEGIA` en el `.env` y recreando solo la aplicación (no hace falta reconstruir la imagen):

```bash
docker compose up -d --force-recreate app
```

El campo `estrategia` de la respuesta del login indica qué estrategia validó las credenciales. Para comprobar el fallo al arrancar, ponga un nombre que no exista (por ejemplo `AUTH_ESTRATEGIA=ldap`), recree la aplicación y revise:

```bash
docker compose ps -a app       # debe aparecer como "Exited"
docker compose logs app        # debe mostrar su mensaje de error con el nombre inválido
```

Mientras las estrategias de los Pasos 4 y 5 no estén registradas en Spring, los valores `bd` y `xml` también deben fallar al arrancar.

### Paso 4: Estrategia con base de datos

Implemente `AutenticacionBaseDatos` contra la tabla `usuarios` de MySQL y regístrela como *bean* con el nombre `bd`, que es el valor de `AUTH_ESTRATEGIA` para esta estrategia:

- Use `JdbcTemplate` (o Spring Data JDBC) con **consultas parametrizadas**. Nunca concatene la entrada del usuario en el SQL.
- Las claves están cifradas con BCrypt: busque al usuario por nombre y compare la clave con `PasswordEncoder.matches(...)`. No compare cadenas ni cifre la clave para buscarla.
- Los datos de conexión llegan por variables de entorno desde el `.env`. No escriba credenciales en el código ni en `application.yml`.

Pruebe con `AUTH_ESTRATEGIA=bd` y con un intento de inyección como usuario: `' OR '1'='1`.

### Paso 5: Estrategia con archivo XML

Implemente `AutenticacionXml` leyendo `usuarios.xml` y regístrela como *bean* con el nombre `xml`:

- La ruta del archivo es configurable con la propiedad `pos.autenticacion.xml.ruta` (variable de entorno `XML_RUTA`). Por defecto es `classpath:usuarios.xml`; para un archivo externo se usa `file:/ruta/usuarios.xml`.
- Configure el *parser* para que rechace DTD y entidades externas (ataque XXE).
- Si usa XPath, evite construir la expresión concatenando el usuario (inyección XPath). Una alternativa más segura es leer los usuarios y buscar en la colección.
- Las claves del XML también están cifradas con BCrypt.

Cree otro archivo de usuarios en su máquina y móntelo en el contenedor: en el servicio `app` de `compose.yaml` agregue un volumen (por ejemplo `./config/usuarios.xml:/config/usuarios.xml:ro`) y la variable `XML_RUTA: file:/config/usuarios.xml`. Verifique con `docker compose up -d --force-recreate app` que la aplicación usa el archivo nuevo sin reconstruir la imagen.

### Paso 6: Pruebas

1. Escriba pruebas unitarias para cada estrategia: usuario válido, clave errada, usuario inexistente y entrada maliciosa.
2. Escriba una prueba de `ServicioAutenticacion` que verifique que un nombre de estrategia desconocido impide arrancar el contexto.
3. Ejecute todo:

   ```bash
   ./mvnw test
   ```

---

## Retos opcionales

1. **Cuarta estrategia sin tocar lo existente.** Agregue `AutenticacionServicioExterno`, que valida contra un proveedor de identidad simulado con [WireMock](https://wiremock.org/) como un contenedor más en `compose.yaml`. Demuestre en el informe que no modificó ninguna clase existente.
2. **Estrategia compuesta.** Cree una estrategia que intente varias en orden (por ejemplo, base de datos y, si no encuentra el usuario, XML). ¿Sigue siendo Strategy o aparece otro patrón?
3. **Prueba de integración con contenedores.** Pruebe `AutenticacionBaseDatos` contra un MySQL real con [Testcontainers](https://java.testcontainers.org/).

---

## Buenas prácticas

- La interfaz de la estrategia es pequeña y estable. Si necesita cambiarla para agregar una estrategia, el diseño tiene un problema.
- Los nombres de las estrategias en la configuración son parte del contrato del despliegue. Documéntelos en el README.
- Las respuestas de error no revelan si el usuario existe.
- El archivo `.env` está en `.gitignore`. Solo se versiona `.env.example`.

---

## PARA ENTREGAR CON ESTE TALLER

Entregue el enlace a su *fork* con el código y un informe breve (`INFORME.md` en la raíz) con las respuestas del Paso 2, el diagrama de clases y la evidencia de cada paso (comandos y salidas).

### Criterios de evaluación

| Criterio | Qué se revisa | Peso |
|---|---|---|
| **Ambiente** | `docker compose up --build` levanta la aplicación y MySQL desde un clon limpio, sin pasos manuales adicionales al `.env`. | 15 % |
| **Diseño** | `AutenticacionController` y `EstrategiaAutenticacion` no se modificaron. `ServicioAutenticacion` no conoce las clases concretas ni usa `if`/`switch` sobre ellas. Agregar una estrategia solo requiere una clase nueva y configuración. | 30 % |
| **Implementación** | Consultas parametrizadas, comparación con BCrypt, credenciales solo en variables de entorno, ruta del XML configurable. | 20 % |
| **Funcionalidad** | Cambiar `AUTH_ESTRATEGIA` y reiniciar cambia el método de autenticación; respuestas `200`, `401` y `400` según la tabla del enunciado; un nombre de estrategia inválido impide arrancar. | 20 % |
| **Pruebas e informe** | Pruebas de cada estrategia y del contexto pasan con `./mvnw test`; el informe responde el Paso 2 e incluye el diagrama. | 15 % |

---

## Hagamos un resumen

- El controlador depende de un servicio; el servicio depende de una interfaz; las estrategias concretas solo las conoce Spring.
- La estrategia se elige al desplegar, no al compilar.
- Agregar un método de autenticación no obliga a modificar el código existente.

---

## Recursos recomendados

- [Refactoring Guru: Strategy](https://refactoring.guru/es/design-patterns/strategy)
- [Spring Framework: inyección de colecciones de beans](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-autowire.html)
- [Spring Security: PasswordEncoder](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [OWASP: Prevención de inyección SQL](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
- [Docker Compose: referencia del archivo](https://docs.docker.com/reference/compose-file/)

---

## Créditos y uso académico

**Autor:** César Augusto Vega Fernández
**Curso:** Diseño y Arquitectura de Software, Universidad de La Sabana

<!-- Pendiente de verificar antes de publicar: el caso del punto de venta parece venir del ejemplo del patrón Strategy de Oscar Blancarte, "Introducción a los patrones de diseño: un enfoque práctico". Si se confirma, reemplazar este comentario por la atribución. -->

## Licencia de uso

Este material se distribuye bajo la licencia [Creative Commons Atribución-NoComercial-CompartirIgual 4.0 Internacional (CC BY-NC-SA 4.0)](https://creativecommons.org/licenses/by-nc-sa/4.0/deed.es).
