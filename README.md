# Gestión de Turnos ECIBienestar

Sistema de gestión de turnos desarrollado en Java usando Spring WebFlux y MongoDB, orientado a la atención de usuarios en servicios de bienestar universitario (psicología, medicina general, odontología, etc.). Proporciona una API REST para la creación, consulta y administración de turnos, con integración de autenticación mediante JWT y despliegue preparado para entornos en la nube.

## Características principales

- **Gestión de turnos**: Crear, consultar, eliminar y listar turnos por usuario, rol o especialidad.
- **Priorización**: Soporte para turnos de prioridad especial.
- **Roles de usuario**: Control de acceso y personalización según el tipo de usuario (por ejemplo, administrador, médico, psicólogo).
- **Integración**: Comunicación con servicios externos de autenticación y usuarios.
- **API documentada**: Uso de Swagger/OpenAPI para facilitar la integración y pruebas.
- **Seguridad**: Configuración de CORS y reglas de seguridad adaptadas para desarrollo y producción.

## Estructura del Proyecto

```
src/
 └── main/
      ├── java/com/shiftmanagement/app_core/
      │      ├── model/        # Modelos de dominio (User, Shift, ShiftStatus, etc.)
      │      ├── services/     # Lógica de negocio (ShiftService, UserService)
      │      ├── controllers/  # Controladores REST (ShiftController)
      │      └── Config/       # Configuraciones de Spring Security, WebClient y CORS
      └── resources/
             ├── application.properties
             └── META-INF/
```

## Instalación y ejecución

1. **Requisitos previos**
   - Java 17+
   - Maven 3.8+
   - Acceso a MongoDB (URI configurable en `application.properties`)

2. **Configuración**
   - Edita `src/main/resources/application.properties` para establecer las credenciales de MongoDB y la URL del servicio de autenticación:
     ```
     spring.data.mongodb.uri=mongodb+srv://<usuario>:<contraseña>@<host>/
     api.auth.url=https://<url-del-servicio-de-usuarios>
     api.auth.username=<usuario>
     api.auth.password=<contraseña>
     ```

3. **Compilar y ejecutar**
   ```bash
   mvn clean package
   java -jar target/gestion-turnos-ecibienestar-0.0.1-SNAPSHOT.jar
   ```

4. **Prueba de la API**
   - Accede a la documentación interactiva Swagger en:  
     `http://localhost:8080/swagger-ui.html`

## Ejemplo de endpoints

- `GET /api/shifts` — Lista todos los turnos
- `POST /api/shifts` — Crea un nuevo turno
- `DELETE /api/shifts/{id}` — Elimina un turno por ID
- `GET /api/shifts/role/{role}` — Turnos por rol de usuario
- `GET /api/shifts/user/{id}` — Turnos asignados a un usuario

## Seguridad y CORS

- El proyecto permite todas las rutas por defecto (ajustable en `SecurityConfig`).
- CORS está configurado para aceptar solicitudes de dominios específicos y localhost para desarrollo.

## Despliegue

- Listo para despliegue en servicios de nube como Azure Web Apps.
- Variables sensibles deben manejarse como secretos de entorno en producción.

## Créditos

Desarrollado por [DSBAENAR](https://github.com/DSBAENAR).

## Licencia

Este proyecto no tiene una licencia explícita. Si deseas contribuir o usarlo para producción, contacta al autor.

---

¿Preguntas o sugerencias? Abre un issue en el repositorio.
