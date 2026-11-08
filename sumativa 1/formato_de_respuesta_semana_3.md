# Duoc UC ONLINE

**Semana 3**  
**Desarrollo Backend II (PBY2202)**  
**Formato de respuesta**

| Nombre estudiante: | Víctor Venegas Lindzay |
| :--- | :--- |
| **Asignatura:** | Desarrollo Backend II (PBY2202) |
| **Carrera:** | Ingeniería en Informática |
| **Profesor:** | Docente Evaluador |
| **Fecha:** | Noviembre 2024 |

---

## Descripción de la actividad

En esta tercera semana, realizarás una actividad sumativa grupal (2 a 3 integrantes), llamada **"Integrando seguridad en aplicaciones Backend"**, donde a partir de una aplicación backend previamente desarrollada, analices, configures, desarrolles e integres mecanismos de seguridad utilizando frameworks y herramientas como Spring Security, JWT, LDAPS e IDaaS. Además, tendrás que asegurar la protección de los componentes backend frente a amenazas conocidas y garantizar la interoperabilidad, siguiendo buenas prácticas y estándares de la industria y de acuerdo con requerimientos específicos.

---

## Instrucciones específicas

Para el desarrollo de la actividad sumativa de la semana, recordemos el caso visto durante toda la experiencia, que requiere la implementación de microservicios para mejorar su arquitectura de software:

### Contexto 

La industria veterinaria en Chile ha jugado un papel clave en el cuidado y bienestar de las mascotas desde mediados del siglo XX. **"CLÍNICA PETSALUD"** es una veterinaria de referencia, establecida en Santiago en los años 80. Con más de 40 años de trayectoria, ha ampliado sus servicios y ahora cuenta con 4 sucursales ubicadas en la Región Metropolitana y la Región de Valparaíso, con planes de expandirse a otras regiones del país. 

En **"CLÍNICA PETSALUD"**, los clientes pueden acceder a una amplia gama de servicios y productos para el cuidado de sus mascotas, entre los cuales destacan: 

- **Atención veterinaria:** consultas generales, tratamientos especializados, vacunación y procedimientos quirúrgicos. 
- **Alimentos y suplementos:** alimentos balanceados, dietas especiales y suplementos para diferentes especies y razas. 
- **Accesorios y juguetes:** collares, correas, juguetes interactivos y artículos para el confort de las mascotas. 
- **Servicios complementarios:** peluquería canina, baño y cuidado estético, así como programas de entrenamiento básico.

### Necesidades específicas:

#### Autenticación Segura: 
- Implementar un sistema de autenticación robusto que permita a los usuarios iniciar sesión de manera segura. 
- Utilizar JWT para gestionar las sesiones de los usuarios en un entorno sin estado (stateless). 

#### Autorización Basada en Roles: 
- Definir y gestionar diferentes roles de usuario (clientes, empleados, gerentes). 
- Controlar el acceso a los recursos y operaciones del backend según los roles y permisos asignados.

### Necesidades tecnológicas: 
Con el objetivo de modernizar sus operaciones y optimizar la experiencia del cliente, **"CLÍNICA PETSALUD"** ha desarrollado una aplicación backend que incluye las siguientes funcionalidades:

1. **Gestión de citas y expedientes médicos:**
   - Permitir a los clientes agendar citas con veterinarios según disponibilidad.
   - Mantener un historial médico digitalizado de cada mascota, incluyendo vacunaciones, diagnósticos y tratamientos.
   - Automatizar recordatorios para controles médicos y vacunas.

2. **Gestión de inventario y ventas:**
   - Controlar el stock de productos y medicamentos en tiempo real en todas las sucursales.
   - Automatizar pedidos a proveedores cuando el inventario sea insuficiente.
   - Generar reportes de los servicios y productos más demandados.

3. **Sistema de fidelización y notificaciones:**
   - Registro y autenticación de usuarios, con perfiles personalizados para cada cliente y su mascota.
   - Implementar un sistema de recompensas por compras y servicios utilizados.
   - Enviar notificaciones sobre promociones, campañas de adopción y eventos especiales.

4. **Seguridad y protección de datos:**
   - Asegurar la privacidad de los datos personales y clínicos de los clientes y sus mascotas.
   - Cumplir con la normativa chilena sobre protección de datos personales.
   - Implementar mecanismos sólidos de autenticación y autorización para prevenir accesos no autorizados.

---

## Preguntas de apoyo

### 1. ¿Cuáles son las principales amenazas de seguridad que enfrenta "CLÍNICA PETSALUD" y cómo pueden impactar su negocio?

"CLÍNICA PETSALUD" opera combinando servicios médicos veterinarios presenciales, venta de insumos/fármacos y comercio digital en múltiples sucursales. Su modelo de negocio enfrenta riesgos críticos si la capa backend carece de protección integral:

- **Falla en el Control de Acceso a Nivel de Objetos (BOLA / IDOR):** Al consultar historiales clínicos (`/api/expedientes/{id}`) o ventas (`/api/ventas/usuario/{id}`), un atacante o cliente malintencionado podría alterar los identificadores en la URL para acceder a información confidencial de terceros. Esto generaría una vulneración grave a la privacidad de los clientes y un severo daño reputacional a una marca con más de cuatro décadas en el mercado chileno.
- **Inyección SQL (SQLi):** La manipulación no controlada de entradas en filtros de catálogo (`/api/productos/categoria`), fechas de citas o formularios de autenticación podría permitir a un atacante inyectar sentencias SQL para eludir el inicio de sesión, extraer masivamente la base de datos de usuarios o alterar el inventario de medicamentos.
- **Cross-Site Scripting (XSS):** La inserción de código malicioso en campos de texto libre (nombres de mascotas, diagnósticos médicos, motivos de consulta o alertas) podría ejecutarse en el navegador de los veterinarios o gerentes cuando revisen la plataforma web, propiciando el secuestro de credenciales o la ejecución de operaciones no autorizadas.
- **Fallas en la Autenticación y Gestión de Sesiones:** El almacenamiento de contraseñas en texto claro o el uso de tokens previsibles y sin caducidad expondría al sistema a ataques de fuerza bruta y suplantación de personal médico y directivo.
- **Cross-Site Request Forgery (CSRF):** Si la aplicación utilizara sesiones basadas en cookies tradicionales sin protección, un sitio malicioso podría forzar al navegador de un usuario autenticado a emitir compras involuntarias o cancelar horas de atención veterinaria.

### 2. ¿Qué estrategias de seguridad son más apropiadas para mitigar estas amenazas en el contexto de una aplicación de comercio electrónico?

Para contrarrestar eficazmente estas vulnerabilidades, se adoptaron las siguientes directrices arquitectónicas:

- **Sesiones sin Estado (Stateless) con JWT:** Se elimina la sesión HTTP en memoria del servidor y el uso de cookies automáticas. Cada petición debe incluir explícitamente el token en la cabecera `Authorization: Bearer <token>`. Esto anula el vector de ataque de CSRF, ya que los navegadores no transfieren cabeceras personalizadas de manera cruzada entre dominios ajenos.
- **Cifrado Robusto de Credenciales (BCrypt):** Se aplica un algoritmo adaptativo de derivación de claves (`BCryptPasswordEncoder` con factor de costo 12), impidiendo que ataques de fuerza bruta o filtraciones de la base de datos permitan recuperar las contraseñas en texto claro.
- **Control de Acceso Basado en Roles (RBAC):** Se establece el principio de menor privilegio, diferenciando estrictamente las capacidades de los tres actores de la clínica (`CLIENTE`, `EMPLEADO`, `GERENTE`) tanto a nivel de URL como mediante anotaciones `@PreAuthorize` en métodos de controlador.
- **Persistencia Parametrizada mediante ORM:** Al utilizar Spring Data JPA y Hibernate, todas las consultas a la base de datos se ejecutan como `PreparedStatements`, aislando los datos del usuario de la sintaxis del comando SQL y neutralizando la Inyección SQL.
- **Serialización Estricta y Cabeceras de Seguridad:** Las respuestas se emiten con tipo de contenido `application/json;charset=UTF-8` y se habilita la cabecera `X-Content-Type-Options: nosniff`, impidiendo la ejecución de secuencias de comandos XSS en el cliente.
- **Protección de Datos Sensibles:** En la entidad `Usuario`, el campo de contraseña se marca como de solo escritura (`@JsonProperty(access = WRITE_ONLY)`), impidiendo que cualquier endpoint retorne el hash al cliente.

### 3. ¿Qué framework de seguridad se adapta mejor a los requerimientos de "CLÍNICA PETSALUD" y por qué?

El framework seleccionado es **Spring Security 6** (en conjunto con **Spring Boot 3.3.6**), por las siguientes ventajas técnicas y operacionales:

1. **Integración profunda con el ecosistema Spring:** Se acopla de manera transparente con Spring MVC y Spring Data JPA mediante una cadena de filtros (`SecurityFilterChain`) que intercepta las solicitudes sin requerir código intrusivo en la lógica de negocio.
2. **Soporte nativo para arquitecturas desacopladas y Stateless:** Permite gestionar la autenticación por token JWT sin necesidad de almacenar sesiones en el servidor, lo que facilita el escalamiento horizontal de microservicios necesario para la expansión a Valparaíso y futuras regiones.
3. **Autorización granular declarativa:** Soporta expresiones SpEL mediante `@PreAuthorize`, lo que permite restringir métodos y endpoints según roles de forma legible, mantenible y centralizada.
4. **Manejo estandarizado de excepciones:** Permite definir manejadores personalizados para errores de autenticación (401) y autorización (403), devolviendo respuestas JSON claras y sin exponer trazas internas que puedan beneficiar a un atacante.

### 4. ¿Cómo implementaste la autenticación y autorización para los diferentes roles en el framework seleccionado a través de Spring Security?

La solución se estructuró a través de los siguientes componentes coordinados:

1. **Carga y Verificación de Usuarios:** Se creó la clase `UsuarioDetailsService`, la cual consulta la base de datos a través de `UsuarioRepository.findByEmail()` y encapsula al usuario en un objeto `UsuarioDetails` compatible con Spring Security.
2. **Mapeo de Roles y Autoridades:** En `UsuarioDetails`, cada rol de la entidad (`CLIENTE`, `EMPLEADO`, `GERENTE`) se asigna con el prefijo estándar `ROLE_` y con su nombre simple, garantizando que el motor de seguridad reconozca las reglas tanto con `hasRole()` como con `hasAuthority()`.
3. **Generación y Firma Criptográfica de JWT:** Se diseñó el componente `JwtTokenProvider`, encargado de construir y firmar los tokens con algoritmo **HMAC-SHA256 (HS256)** a partir de una clave secreta de alta entropía inyectada desde la configuración. El token transporta la identidad del usuario, su identificador y su rol con una vigencia de 24 horas.
4. **Filtro de Intercepción de Peticiones (`JwtAuthenticationFilter`):** Heredando de `OncePerRequestFilter`, examina la cabecera `Authorization`, extrae el token Bearer, valida su firma y vigencia, y si es válido, carga los permisos en el `SecurityContextHolder`.
5. **Configuración Central (`SecurityConfig`):** Se definió la cadena de filtros HTTP estableciendo la política de sesión en `STATELESS`, registrando el filtro JWT antes del filtro de usuario/contraseña, y definiendo las restricciones por ruta y rol mediante `authorizeHttpRequests`.

### 5. ¿Qué medidas adicionales implementaste para asegurar el cumplimiento de la Ley de Protección de Datos Personales en Chile?

En atención a la **Ley N° 19.628 sobre Protección de la Vida Privada** y al principio constitucional de protección de datos personales (Art. 19 N° 4 de la Constitución Política de Chile):

1. **Confidencialidad y Segregación de Fichas Clínicas:** Los diagnósticos y tratamientos veterinarios almacenados en `/api/expedientes` constituyen información técnica reservada; solo el personal médico y administrativo acreditado puede crear o modificar dichos expedientes. El propietario solo tiene acceso de consulta sobre los registros de sus propias mascotas, y el borrado físico de registros está limitado a auditoría gerencial.
2. **Minimización de Datos y Cifrado:** Las contraseñas de los usuarios jamás se guardan ni viajan en texto claro; se procesan con **BCrypt** antes de la persistencia y se omiten de cualquier salida JSON mediante anotaciones de serialización (`WRITE_ONLY`).
3. **Supresión de Fugas de Información Técnica:** Los errores de seguridad 401 y 403 se responden con mensajes JSON estandarizados (`ErrorResponse`), suprimiendo trazas de pila (stack traces), nombres de librerías o estructuras de base de datos que pudieran vulnerar la seguridad del sistema.

---

## Entregables

---

### Entregable 1: Análisis y selección de la estrategia y framework de seguridad

#### 1.1 Análisis del Contexto Operacional y Requerimientos de Seguridad

"CLÍNICA PETSALUD" requiere una plataforma que articule tres frentes de trabajo: atención clínica veterinaria, gestión de inventario y ventas, y fidelización de clientes en sus distintas sucursales. Esto exige niveles de acceso rigurosamente diferenciados:

- **Nivel Cliente:** Requiere acceso al catálogo de productos, registro personal, reserva y visualización de citas de sus mascotas, compra de artículos e historial de puntos de fidelización. Bajo ninguna circunstancia debe acceder a datos de otros clientes ni a funciones de edición médica.
- **Nivel Empleado (Veterinarios y Asistentes):** Requiere consultar citas asignadas, actualizar el estado de las atenciones médicas, ingresar diagnósticos y recetas en los expedientes clínicos, monitorear alertas de stock crítico y reponer productos. No debe tener privilegios de borrado de fichas ni visualización de reportes financieros globales.
- **Nivel Gerente:** Administración superior con acceso irrestricto: reportes consolidados de facturación de todas las sucursales, alta y baja de cuentas de empleados, auditoría clínica y modificación de configuraciones del sistema.

#### 1.2 Matriz de Amenazas y Medidas de Mitigación

| Amenaza Identificada | Escenario en PetSalud | Impacto en el Negocio | Estrategia de Mitigación |
| :--- | :--- | :--- | :--- |
| **BOLA / IDOR (Broken Object Level Authorization)** | Un cliente manipula el parámetro ID en `/api/citas/{id}` o `/api/expedientes/{id}` para ver datos ajenos. | Violación de confidencialidad médica y sanciones bajo la Ley 19.628. | Control estricto de roles con `@PreAuthorize` y verificación de contexto en la capa de servicios. |
| **Inyección SQL (SQLi)** | Inserción de sentencias SQL en el login o en el filtro de categorías del catálogo. | Exfiltración de credenciales, alteración de precios o borrado de tablas. | Uso exclusivo de Spring Data JPA con `PreparedStatements` parametrizados por el driver JDBC. |
| **Cross-Site Scripting (XSS)** | Inserción de código `<script>` en nombres de mascotas, motivos de cita o fichas clínicas. | Robo de tokens en navegadores de veterinarios o ejecución de acciones indebidas. | Respuestas REST tipadas como `application/json`, escapado de Jackson y cabecera `X-Content-Type-Options: nosniff`. |
| **Cross-Site Request Forgery (CSRF)** | Engaño al navegador para emitir peticiones en nombre del usuario autenticado. | Agendamiento o cancelación fraudulenta de citas médicas y compras no deseadas. | Modelo Stateless: autenticación por cabecera Bearer Token; el navegador no envía tokens automáticamente en peticiones cruzadas. |
| **Robo de Credenciales / Fuerza Bruta** | Ataques de diccionario contra el endpoint de autenticación. | Compromiso de cuentas de empleados y acceso a expedientes clínicos. | Cifrado adaptativo BCrypt (costo 12) y tokens JWT con expiración acotada (24 horas). |

#### 1.3 Evaluación Comparativa de Opciones de Autenticación

Para definir la mejor estrategia, se contrastaron las alternativas tecnológicas disponibles:

1. **Autenticación en Memoria (`InMemoryUserDetailsManager`):**  
   Almacena credenciales de manera estática en el código o en archivos de configuración. Si bien es rápida para maquetas iniciales, carece de persistencia, no admite registro de nuevos usuarios en tiempo de ejecución y resulta totalmente inviable para producción.
2. **Autenticación Basada en Base de Datos con Sesión Tradicional (JDBC/JPA + Cookies):**  
   Gestiona las credenciales en tablas relacionales pero retiene el estado de sesión en la memoria RAM del servidor web mediante cookies `JSESSIONID`. Esto introduce problemas de escalabilidad al requerir afinidad de sesión (sticky sessions) o replicación de sesiones cuando la clínica agregue nuevas sucursales y servidores.
3. **Integración con Directorios LDAP / LDAPS:**  
   Es una excelente solución para autenticación corporativa interna en grandes redes de oficina (como un Active Directory institucional). Sin embargo, añade una sobrecarga de infraestructura excesiva y no se ajusta a una plataforma de comercio electrónico abierta donde los clientes se registran de forma libre y masiva.
4. **Identidad como Servicio (IDaaS - Auth0, Okta, Firebase):**  
   Delega la autenticación en un proveedor en la nube. Ofrece alta seguridad y características como MFA, pero añade costos recurrentes por volumen de usuarios activos, genera dependencia de terceros (vendor lock-in) y añade latencia externa a las peticiones.
5. **Autenticación Basada en Tokens JWT (JSON Web Token) con Base de Datos:**  
   Combina la persistencia de usuarios en base de datos con un mecanismo de sesión completamente sin estado (**Stateless**). El token viaja en la cabecera HTTP y contiene en su firma criptográfica toda la información necesaria (identidad, expiración y roles).

#### 1.4 Justificación de la Elección de JWT

La arquitectura basada en **Spring Security + JWT** fue seleccionada como la solución óptima para PetSalud por las siguientes razones fundamentales:
- **Escalabilidad Horizontal:** Al no guardar sesiones en memoria de servidor, permite desplegar réplicas del backend en contenedores o instancias en la nube para atender las sucursales de Santiago, Valparaíso y futuras regiones sin sincronización de estado.
- **Rendimiento e Independencia de Consultas:** Dado que el token transporta los roles y el ID del usuario verificados criptográficamente por la firma HMAC-SHA256, el filtro de seguridad puede autorizar peticiones recurrentes sin saturar la base de datos con consultas repetitivas de sesión.
- **Interoperabilidad con Clientes Modernos:** Facilita la conexión desacoplada con aplicaciones web, portales de clientes y futuros aplicativos móviles para veterinarios a través de un estándar abierto de la industria (RFC 7519).

---

### Entregable 2: Guía de configuración y explicación de la implementación

La implementación de la seguridad en Clínica PetSalud se desarrolló siguiendo un procedimiento estructurado en 6 etapas secuenciales, asegurando una arquitectura limpia, desacoplada y orientada a buenas prácticas.

#### Paso 1: Configuración del Entorno y Dependencias en `pom.xml`

El primer paso consistió en preparar el entorno de compilación en Spring Boot 3.3.6 (Java 17/21). Para ello, se incorporaron en el archivo `pom.xml` los módulos fundamentales de seguridad y soporte:

1. **`spring-boot-starter-security`:** Incorpora el motor de Spring Security, los filtros HTTP, la gestión de autenticación y los algoritmos criptográficos.
2. **`spring-boot-starter-validation`:** Proporciona validación declarativa mediante anotaciones Jakarta (`@NotBlank`, `@Email`, `@Size`) para proteger las entradas de los endpoints contra datos malformados o inyecciones.
3. **Librerías JJWT (versión 0.12.6):** Se añadieron las tres dependencias modulares requeridas por la versión moderna de JJWT:
   - `jjwt-api`: Interfaz pública para construcción y verificación de tokens.
   - `jjwt-impl`: Implementación de los algoritmos criptográficos (HMAC, SHA).
   - `jjwt-jackson`: Serializador JSON para procesar los *claims* del token de forma segura.
4. **`h2` (Runtime) y `mysql-connector-j`:** Se configuró H2 en memoria para posibilitar la ejecución y evaluación inmediata del proyecto con cero dependencias externas, manteniendo el driver MySQL para despliegues productivos.
5. **`springdoc-openapi-starter-webmvc-ui` (versión 2.6.0):** Para documentar interactivamente la API y habilitar pruebas directas con soporte de Bearer Token en Swagger UI.

*Nota de Corrección:* Se corrigió además la propiedad de codificación `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>` y se repararon los caracteres no válidos en `application.properties`, subsanando el fallo inicial de compilación del proyecto base.

#### Paso 2: Parametrización de la Seguridad en `application.properties`

En la configuración del sistema se establecieron los parámetros criptográficos del token:
- **Clave Secreta (`jwt.secret`):** Se definió una clave de más de 256 bits de entropía para garantizar que el algoritmo HMAC-SHA256 opere bajo estándares de seguridad informática reconocidos.
- **Tiempo de Expiración (`jwt.expiration`):** Se fijó en 86.400.000 milisegundos (24 horas), balanceando la comodidad del usuario con la reducción de la ventana de riesgo ante una eventual sustracción del token.

Adicionalmente, se habilitó la consola web de H2 en `/h2-console` para auditoría y se creó el perfil complementario `application-mysql.properties` para ambientes de producción.

#### Paso 3: Construcción de la Capa Criptográfica JWT

Se implementó el componente `JwtTokenProvider` en el paquete `security.jwt`. Esta clase asume las siguientes responsabilidades:
- **Inicialización de la Clave Criptográfica:** Mediante `Keys.hmacShaKeyFor()`, convierte la clave configurada en una instancia segura de `SecretKey`.
- **Generación del Token:** Construye el JWT asignando el correo del usuario como `subject`, emitiendo la marca de tiempo actual (`issuedAt`), estableciendo el vencimiento (`expiration`) y adjuntando *claims* personalizados con el ID de usuario, su nombre y su rol (`ROLE_CLIENTE`, `ROLE_EMPLEADO`, `ROLE_GERENTE`). Finalmente, estampa la firma digital con `Jwts.SIG.HS256`.
- **Validación del Token:** Implementa un analizador (`Jwts.parser().verifyWith(secretKey).build()`) que comprueba la validez matemática de la firma y la fecha de expiración. En caso de detectar firmas alteradas, estructuras truncadas o tokens caducados, captura las excepciones de JJWT (`SignatureException`, `MalformedJwtException`, `ExpiredJwtException`) y rechaza la autenticación sin interrumpir la ejecución del servidor.

#### Paso 4: Implementación del Filtro de Autenticación (`JwtAuthenticationFilter`)

Para interceptar y validar los tokens en cada solicitud HTTP entrante antes de que lleguen a los controladores, se construyó `JwtAuthenticationFilter`, el cual extiende de `OncePerRequestFilter`:
1. **Extracción:** Lee la cabecera HTTP `Authorization`. Si está presente y comienza con el prefijo `"Bearer "`, aísla la cadena del token.
2. **Comprobación:** Valida el token con `JwtTokenProvider`. Si la firma y vigencia son legítimas, extrae el correo electrónico del usuario.
3. **Carga de Contexto:** Invoca a `UsuarioDetailsService` para recuperar las autoridades del usuario desde la base de datos y construye un objeto `UsernamePasswordAuthenticationToken`.
4. **Registro en el Contexto de Seguridad:** Establece dicho token en el `SecurityContextHolder`, dejando al usuario formalmente autenticado durante el ciclo de vida de la petición.

#### Paso 5: Configuración Central de Seguridad (`SecurityConfig`)

En `SecurityConfig`, bajo el paradigma moderno de Spring Security 6 basado en beans, se ensamblaron las reglas globales del sistema:
- **Deshabilitación de CSRF:** Se desactiva el filtro CSRF de forma consciente y justificada, dado que el sistema opera bajo una arquitectura REST Stateless donde los tokens se transportan mediante cabeceras y no mediante cookies de navegador.
- **Gestión de Sesión Stateless:** Se instruye a Spring Security a no crear sesiones HTTP (`SessionCreationPolicy.STATELESS`), liberando memoria en el servidor y evitando el almacenamiento de identificadores de sesión.
- **Manejo de Errores Tipados (401 y 403):** Se conectaron dos manejadores especializados:
  - `JwtAuthenticationEntryPoint`: Intercepta peticiones anónimas a rutas protegidas y responde con un código **401 Unauthorized** y un objeto `ErrorResponse` en formato JSON.
  - `CustomAccessDeniedHandler`: Intercepta solicitudes donde el usuario carece de los permisos suficientes y responde con un código **403 Forbidden** y un objeto `ErrorResponse` en formato JSON.
- **Cabeceras de Protección:** Se añadieron cabeceras preventivas contra ataques de clickjacking (`frameOptions().sameOrigin()`, permitiendo a su vez visualizar la consola H2) y contra ataques de interpretación MIME (`contentTypeOptions()`).
- **Codificador de Contraseñas:** Se registró el bean `BCryptPasswordEncoder` con factor de costo 12 para todas las operaciones de registro y validación de credenciales.
- **Reglas de Acceso Declarativas:** Se definieron las rutas públicas (`/api/auth/**`, `/api/productos/**`, `/swagger-ui/**`, `/h2-console/**`), las rutas exclusivas de Gerente (`/api/usuarios/**`, `/api/ventas`) y las rutas operativas para Empleados y Veterinarios (`/api/citas/*/estado`, `/api/expedientes/**`).
- **Posicionamiento del Filtro:** Se intercaló `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`.

#### Paso 6: Autorización Granular en Controladores, DTOs y Semillero

Para completar la protección operativa de la aplicación:
- **Controladores y Anotaciones `@PreAuthorize`:** Se habilitó `@EnableMethodSecurity` y se protegieron los métodos de los controladores (`CitaController`, `ExpedienteMedicoController`, `MascotaController`, `ProductoController`, `VentaController`, `RecompensaController`, `NotificacionController`). Por ejemplo, agendar citas está disponible para cualquier usuario autenticado, pero actualizar el estado clínico de una cita requiere rol `EMPLEADO` o `GERENTE`, y eliminar expedientes médicos queda estrictamente reservado a la `GERENTE`.
- **Controlador de Autenticación (`AuthController`):** Se crearon los endpoints públicos `/api/auth/login` (que autentica con `AuthenticationManager` y emite el token JWT en un `AuthResponse`) y `/api/auth/register` (que valida datos de entrada con `RegisterRequest`, comprueba unicidad de correo, aplica BCrypt y retorna el nuevo usuario autenticado).
- **Cifrado en la Capa de Servicio:** En `UsuarioServiceImpl`, se garantizó que todo registro o actualización de contraseña pase obligatoriamente por `passwordEncoder.encode()`.
- **Semillero de Datos Automatizado (`DataInitializer`):** Mediante un `CommandLineRunner`, se programó la inserción automática al iniciar la aplicación de tres usuarios representativos con contraseñas cifradas en BCrypt:
  - `cliente@petsalud.cl` (contraseña: `Cliente123!`, Rol: `CLIENTE`)
  - `empleado@petsalud.cl` (contraseña: `Empleado123!`, Rol: `EMPLEADO`)
  - `gerente@petsalud.cl` (contraseña: `Gerente123!`, Rol: `GERENTE`)  
  Además de registrar mascotas iniciales ("Max" y "Luna"), catálogo de productos, citas y fichas médicas para pruebas operacionales inmediatas.

---

### Entregable 3: Explicación técnica de cómo las configuraciones implementadas protegen los componentes backend frente a las amenazas identificadas

#### 3.1 Protección frente a Inyección SQL (SQL Injection)

- **Mecanismo Técnico:** La aplicación interactúa con la base de datos exclusivamente mediante la abstracción de Spring Data JPA sobre Hibernate.
- **Funcionamiento Interno:** Cuando una petición llega a un repositorio (por ejemplo, buscar un usuario por correo en la autenticación o filtrar productos por categoría en el catálogo), Hibernate no concatena cadenas de texto sin validar en el comando SQL. En su lugar, utiliza **consultas parametrizadas (PreparedStatements)** a nivel del driver JDBC.
- **Efecto de Seguridad:** Si un atacante ingresa una carga maliciosa como `' OR '1'='1' --` o sentencias del tipo `' UNION SELECT ...`, el motor de base de datos trata el valor recibido como un dato literal de tipo cadena (`String`) y jamás como parte del árbol de sintaxis o ejecución de la consulta. La consulta busca literalmente un texto con ese contenido, arrojando cero coincidencias y neutralizando cualquier evasión de credenciales o filtración de tablas.

#### 3.2 Protección frente a Cross-Site Scripting (XSS)

- **Mecanismo Técnico:** Se combinan tres barreras de protección en la capa web: el convertidor de mensajes de Spring MVC, el serializador Jackson y las cabeceras HTTP de Spring Security.
- **Funcionamiento Interno:** Todas las respuestas de los controladores `@RestController` se procesan mediante `MappingJackson2HttpMessageConverter`, emitiéndose de forma obligatoria bajo la cabecera `Content-Type: application/json;charset=UTF-8`. Adicionalmente, Spring Security inyecta automáticamente la cabecera `X-Content-Type-Options: nosniff`.
- **Efecto de Seguridad:** Si un usuario malintencionado intenta almacenar código ejecutable como `<script>alert('XSS')</script>` o etiquetas `<img>` con eventos dañinos en el nombre de una mascota, en la descripción de un producto o en un diagnóstico médico, el texto se almacena y serializa como datos JSON puros entrecomillados. Cuando un cliente web consume la API, el navegador reconoce que el recurso es de tipo JSON y la cabecera `nosniff` prohíbe que el navegador intente adivinar o interpretar el contenido como código HTML ejecutable, evitando que scripts no deseados se ejecuten en la sesión de los usuarios o del personal médico.

#### 3.3 Protección frente a Cross-Site Request Forgery (CSRF)

- **Mecanismo Técnico:** Implementación de un modelo de autenticación desacoplado y sin estado (**Stateless Authentication**) basado en Bearer Tokens.
- **Funcionamiento Interno:** Las vulnerabilidades CSRF tradicionales explotan el comportamiento predeterminado de los navegadores web, los cuales adjuntan automáticamente las cookies de sesión (`JSESSIONID`) cada vez que se emite una petición hacia el dominio destino, incluso si la petición se originó desde una pestaña o sitio web malicioso de terceros.
- **Efecto de Seguridad:** En nuestra arquitectura, el servidor no emite ni reconoce cookies de sesión; exige que cada petición autenticada transporte la cabecera explícita `Authorization: Bearer <token>`. Debido a las políticas de seguridad del mismo origen (Same-Origin Policy), **los navegadores web no pueden adjuntar cabeceras personalizadas de autorización de forma automática en peticiones cruzadas originadas desde otros sitios**. Por lo tanto, cualquier intento externo de forzar una compra o cancelar una cita médica en nombre de un usuario autenticado llegará al backend sin la cabecera correspondiente y será interceptado de inmediato por `JwtAuthenticationEntryPoint`, devolviendo un código de error **401 Unauthorized**.

#### 3.4 Protección frente a Falla de Control de Acceso (BOLA / IDOR)

- **Mecanismo Técnico:** Control de acceso multidimensional que combina filtros HTTP declarativos y anotaciones `@PreAuthorize` a nivel de método con SpEL (*Spring Expression Language*).
- **Funcionamiento Interno:** Cada petición es autenticada previamente por el filtro JWT, cargando en el hilo de ejecución las autoridades verificadas del usuario. Los controladores verifican si el rol que acompaña a la petición tiene permiso para invocar el recurso solicitado.
- **Efecto de Seguridad:** Si un usuario con perfil `CLIENTE` intenta acceder a endpoints gerenciales (como `/api/usuarios` para listar cuentas o `/api/ventas` para ver la recaudación global) o modificar el estado de una cita médica (`/api/citas/{id}/estado`), Spring Security evalúa la regla de autorización, constata que el rol carece del permiso `ROLE_GERENTE` o `ROLE_EMPLEADO`, e interrumpe de inmediato la ejecución devolviendo un código **403 Forbidden** a través de `CustomAccessDeniedHandler`, evitando cualquier manipulación o exposición indebida de recursos.

#### 3.5 Cumplimiento de la Ley 19.628 de Protección de Datos Personales en Chile

- **Mecanismo Técnico:** Principio de minimización de datos, no divulgación de credenciales y segregación de expedientes de salud.
- **Funcionamiento Interno:** En la entidad `Usuario.java`, el campo de contraseña se encuentra blindado mediante `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` y `@ToString.Exclude`. Asimismo, las contraseñas se almacenan mediante el algoritmo criptográfico **BCrypt**.
- **Efecto de Seguridad:** Este diseño asegura que ninguna consulta al API (sea individual o en listas masivas) emita hashes de contraseña en el cuerpo JSON devuelto al cliente, y previene que queden registradas accidentalmente en los archivos de bitácora (logs) del servidor. Del mismo modo, el acceso a los historiales clínicos veterinarios queda estrictamente restringido al personal médico acreditado y a los dueños de cada paciente, garantizando el respeto al derecho a la privacidad consagrado en el ordenamiento jurídico nacional.

---

*Reservados todos los derechos Fundación Instituto Profesional Duoc UC. No se permite copiar, reproducir, reeditar, descargar, publicar, emitir, difundir, de forma total o parcial la presente obra, ni su incorporación a un sistema informático, ni su transmisión en cualquier forma o por cualquier medio (electrónico, mecánico, fotocopia, grabación u otros) sin autorización previa y por escrito de Fundación Instituto Profesional Duoc UC. La infracción de dichos derechos puede constituir un delito contra la propiedad intelectual.*
