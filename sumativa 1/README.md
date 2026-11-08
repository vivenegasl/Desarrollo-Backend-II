# 🐾 Clínica PetSalud - Backend REST Seguro

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2021-orange.svg)](https://www.oracle.com/java/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3.5-blue.svg)](https://spring.io/projects/spring-security)
[![JJWT](https://img.shields.io/badge/JJWT-0.12.6-red.svg)](https://github.com/jwtk/jjwt)
[![Tests](https://img.shields.io/badge/Tests-18%2F18%20Passed-success.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

> **Evaluación Sumativa 1 - Desarrollo Backend II (PBY2202)**  
> **Tema:** Integrando seguridad en aplicaciones Backend  
> **Institución:** Duoc UC ONLINE  

---

## 📋 Descripción del Proyecto

**CLÍNICA PETSALUD** es una prestigiosa red veterinaria con más de 40 años de trayectoria en Chile y 4 sucursales activas en la Región Metropolitana y la Región de Valparaíso. Este proyecto implementa una arquitectura backend moderna, robusta y escalable construida sobre **Spring Boot 3.3.6**, asegurada íntegramente mediante **Spring Security 6**, tokens criptográficos **JWT (JJWT 0.12.6)** y un esquema de control de acceso basado en roles (**RBAC**).

El sistema ofrece servicios de:
- **Atención veterinaria:** Agendamiento de citas y gestión digital de expedientes médicos.
- **Venta e inventario:** Catálogo público de productos, alimentos balanceados, alertas de stock bajo y registro de ventas.
- **Fidelización y notificaciones:** Sistema de puntos de recompensa y avisos clínicos personalizados.
- **Protección y privacidad de datos:** Cumplimiento con la **Ley 19.628** (Protección de la Vida Privada / Datos Personales en Chile) y defensas activas contra amenazas del **OWASP Top 10** (SQL Injection, XSS, CSRF, BOLA/IDOR).

---

## 🏛️ Arquitectura de Seguridad

```
   [ Cliente / Postman / Swagger / Frontend ]
                       │ (HTTP Request)
                       ▼
 ┌────────────────────────────────────────────────────────┐
 │            Spring Security Filter Chain               │
 ├────────────────────────────────────────────────────────┤
 │ 1. CORS Filter                                         │
 │ 2. CSRF Disabled (Justificado: Stateless REST con JWT) │
 │ 3. Security Headers (nosniff, sameOrigin, CSP)         │
 │ 4. JwtAuthenticationFilter (OncePerRequestFilter)      │
 │    ├─ Extrae cabecera 'Authorization: Bearer <token>'  │
 │    ├─ Valida firma HMAC-SHA256 (Secret >= 256 bits)    │
 │    ├─ Verifica vigencia temporal (Claims: exp, iat)    │
 │    └─ Carga UserDetails en SecurityContextHolder       │
 │ 5. AuthorizationFilter (RBAC)                          │
 │    ├─ Matchers declarativos por ruta y método HTTP     │
 │    └─ Anotaciones @PreAuthorize a nivel de controlador │
 └─────────────────────────┬──────────────────────────────┘
                           │
             ┌─────────────┴─────────────┐
             ▼                           ▼
    [ Acceso Permitido ]       [ Acceso Denegado ]
             │                           │
             ▼                           ├─ 401 Unauthorized (JwtAuthenticationEntryPoint)
  [ REST Controllers ]                   └─ 403 Forbidden    (CustomAccessDeniedHandler)
             │
             ▼
  [ Capa de Servicios ]
             │
             ▼
[ Spring Data JPA / Hibernate ] ──► Consultas Parametrizadas (PreparedStatements Anti-SQLi)
             │
             ▼
   [( Base de Datos )] ──► H2 en memoria (Dev/Test) / MySQL (Producción)
```

---

## 👥 Matriz de Roles y Control de Acceso (RBAC)

El sistema define 3 roles canónicos según los requerimientos de PetSalud:

| Rol | Descripción | Permisos Clave |
| :--- | :--- | :--- |
| **`CLIENTE`** | Dueños de mascotas registrados en la clínica | Agendar citas para sus mascotas, consultar sus citas y fichas, realizar compras, revisar puntos y notificaciones. |
| **`EMPLEADO`** | Veterinarios y personal médico/asistencial | Actualizar estado de citas, registrar diagnósticos y tratamientos en expedientes médicos, actualizar inventario, monitorear stock crítico. |
| **`GERENTE`** | Dirección y administración superior | Acceso total: listar y gestionar usuarios/roles, consultar ventas consolidadas de todas las sucursales, auditoría y bajas. |

### Matriz de Endpoints y Protección HTTP

| Endpoint | Método | Perfiles Autorizados | Propósito |
| :--- | :---: | :---: | :--- |
| `/api/auth/login` | POST | `permitAll()` | Inicio de sesión y emisión de JWT. |
| `/api/auth/register` | POST | `permitAll()` | Registro de nuevos usuarios con BCrypt. |
| `/api/productos/**` | GET | `permitAll()` | Catálogo público para visualización en tienda. |
| `/swagger-ui/**`, `/v3/api-docs/**` | GET | `permitAll()` | Documentación técnica interactiva de la API. |
| `/h2-console/**` | ALL | `permitAll()` | Consola gráfica de la base de datos de desarrollo. |
| `/api/citas` | POST | `CLIENTE`, `EMPLEADO`, `GERENTE` | Agendar nueva cita veterinaria. |
| `/api/citas/{id}/estado` | PUT | `EMPLEADO`, `GERENTE` | Confirmar o actualizar estado médico de la cita. |
| `/api/expedientes` | POST | `EMPLEADO`, `GERENTE` | Crear ficha médica en historial clínico. |
| `/api/expedientes/{id}` | DELETE | `GERENTE` | Eliminación auditada de registros clínicos. |
| `/api/usuarios` | GET | `GERENTE` | Listado completo de usuarios de la clínica. |
| `/api/usuarios/{id}` | PUT, DELETE | `GERENTE` | Modificación de roles y baja de cuentas. |
| `/api/ventas` | GET | `GERENTE` | Reporte financiero global de todas las sucursales. |

---

## 🔑 Credenciales de Prueba Precargadas

La aplicación incluye un semillero de datos (`DataInitializer.java`) que inicializa automáticamente los siguientes usuarios con contraseñas cifradas en **BCrypt** (factor de costo 12):

| Perfil | Correo Electrónico | Contraseña | Rol Asignado |
| :--- | :--- | :--- | :--- |
| **Cliente** | `cliente@petsalud.cl` | `Cliente123!` | `CLIENTE` |
| **Empleado** | `empleado@petsalud.cl` | `Empleado123!` | `EMPLEADO` |
| **Gerente** | `gerente@petsalud.cl` | `Gerente123!` | `GERENTE` |

---

## 🛡️ Mitigación de Amenazas Comunes

1. **Inyección SQL (SQLi - OWASP A03:2021):**
   - Todas las consultas a la base de datos se ejecutan a través de **Spring Data JPA y Hibernate ORM**, utilizando **PreparedStatements** y vinculación de parámetros tipados (`?`), lo que impide que valores maliciosos (`' OR '1'='1`) alteren la sintaxis de las consultas SQL.
2. **Cross-Site Scripting (XSS - OWASP A03:2021):**
   - Las respuestas REST se serializan exclusivamente con **Jackson HttpMessageConverter** bajo `Content-Type: application/json;charset=UTF-8`, previniendo que los navegadores interpreten código HTML/JavaScript inyectado.
   - Se incluye la cabecera HTTP de seguridad `X-Content-Type-Options: nosniff` para evitar la interpretación errónea de tipos MIME.
3. **Cross-Site Request Forgery (CSRF - OWASP A01:2021):**
   - El backend opera bajo un modelo **Stateless** (sin estado de sesión HTTP ni cookies `JSESSIONID`). La autenticación se realiza exclusivamente mediante la cabecera `Authorization: Bearer <token>`.
   - Dado que los navegadores web **no envían automáticamente cabeceras personalizadas en peticiones cross-site**, el vector de ataque CSRF queda completamente neutralizado por diseño.
4. **Protección de Datos Personales (Ley Chilena 19.628 / 21.096):**
   - Las contraseñas nunca se almacenan en texto claro y se protegen mediante **BCrypt (12 rondas)**.
   - En la serialización JSON de la entidad `Usuario`, el campo `password` está marcado con `@JsonProperty(access = WRITE_ONLY)` y `@ToString.Exclude`, asegurando que ninguna llamada a la API filtre hashes o contraseñas.
   - Las fichas médicas y datos clínicos están segregados y protegidos mediante RBAC estricto.

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
- **Java Development Kit (JDK):** Versión 17 o 21 instalada.
- **Maven:** No requiere instalación previa (se incluye Maven Wrapper `mvnw` y `mvnw.cmd`).

### 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPOSITORIO_GITHUB>
cd "Sumativa 1/backendpetsalud"
```

### 2. Ejecutar la Suite de Pruebas Automatizadas
Para verificar el 100% de los tests de seguridad (JWT, RBAC, SQLi, XSS y CSRF):
```powershell
.\mvnw.cmd clean test
```
*En Linux / macOS:*
```bash
./mvnw clean test
```

### 3. Iniciar el Servidor de Desarrollo
```powershell
.\mvnw.cmd spring-boot:run
```
La aplicación iniciará en el puerto **8080**:
- **API REST Base:** `http://localhost:8080/api`
- **Documentación Swagger UI interactiva:** `http://localhost:8080/swagger-ui.html`
- **Especificación OpenAPI en JSON:** `http://localhost:8080/v3/api-docs`
- **Consola H2 Database:** `http://localhost:8080/h2-console`  
  *(JDBC URL: `jdbc:h2:mem:petsalud_db` | User: `sa` | Password: en blanco)*

---

## 🧪 Pruebas Rápidas con cURL

### 1. Iniciar Sesión y Obtener Token JWT (Rol Cliente)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@petsalud.cl","password":"Cliente123!"}'
```

### 2. Intentar Acceder a Endpoint Protegido sin Token (Esperado: 401 Unauthorized)
```bash
curl -i -X GET http://localhost:8080/api/usuarios
```

### 3. Intentar Acceder a Endpoint de Gerente con Token de Cliente (Esperado: 403 Forbidden)
```bash
curl -i -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <TOKEN_DE_CLIENTE>"
```

### 4. Acceso Exitoso con Token de Gerente (Esperado: 200 OK)
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <TOKEN_DE_GERENTE>"
```

---

## 📁 Estructura del Proyecto

```
backendpetsalud/
├── src/
│   ├── main/
│   │   ├── java/com/petsalud/backendpetsalud/
│   │   │   ├── config/               # Configuración OpenAPI y semillero DataInitializer
│   │   │   ├── controller/           # Controladores REST con @PreAuthorize
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CitaController.java
│   │   │   │   ├── ExpedienteMedicoController.java
│   │   │   │   ├── MascotaController.java
│   │   │   │   ├── NotificacionController.java
│   │   │   │   ├── ProductoController.java
│   │   │   │   ├── RecompensaController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   └── VentaController.java
│   │   │   ├── dto/                  # Objetos de transferencia de datos con validaciones
│   │   │   ├── entity/               # Entidades JPA (Usuario, Rol, Cita, etc.)
│   │   │   ├── repository/           # Repositorios Spring Data JPA
│   │   │   ├── security/             # Seguridad central Spring Security 6
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── UsuarioDetails.java
│   │   │   │   ├── UsuarioDetailsService.java
│   │   │   │   └── jwt/              # Proveedor de tokens, filtros y manejadores 401/403
│   │   │   │       ├── JwtTokenProvider.java
│   │   │   │       ├── JwtAuthenticationFilter.java
│   │   │   │       ├── JwtAuthenticationEntryPoint.java
│   │   │   │       └── CustomAccessDeniedHandler.java
│   │   │   └── service/              # Lógica de negocio y cifrado de contraseñas
│   │   └── resources/
│   │       ├── application.properties        # Perfil por defecto (H2 en memoria)
│   │       └── application-mysql.properties  # Perfil productivo (MySQL)
│   └── test/java/com/petsalud/backendpetsalud/
│       ├── BackendpetsaludApplicationTests.java
│       └── security/
│           ├── JwtAuthenticationTest.java              # Pruebas de tokens JWT
│           ├── RoleBasedAccessControlTest.java         # Pruebas de RBAC (200, 401, 403)
│           └── SecurityVulnerabilityMitigationTest.java# Pruebas SQLi, XSS y CSRF
├── pom.xml                           # Definición de dependencias Maven
├── requests.http                     # Colección HTTP para cliente REST / VS Code
└── README.md                         # Documentación técnica del proyecto
```
