# Gestión de Solicitudes – DSY1105 (Evaluación 3)

Aplicación Android que registra y da seguimiento a las solicitudes que los clientes envían a una empresa de servicios informáticos (hoy llegan por WhatsApp y se pierden). Esta entrega evoluciona la app de la Evaluación 2: la información ya no vive solo en el teléfono, sino en **microservicios propios en Spring Boot** con **persistencia remota**, además de consumir una **API externa** e incluir **pruebas unitarias**.

## Integrantes
- Manuel Enrrique Pérez Rodrigues
- José Tomás Reyes Pérez

## Planificación
Tablero Trello: https://trello.com/b/kB2Tu1mX/gesti%C3%B3n-de-solicitudes-dsy1105

## Estructura del repositorio
```
Gestion-de-Solicitudes-DSY1105-EV3/
├── app-movil/                 Proyecto Android (Kotlin + Jetpack Compose, MVVM)
├── microservicios/
│   ├── ms-usuarios/           Spring Boot · puerto 8081 · login y registro
│   └── ms-solicitudes/        Spring Boot · puerto 8082 · CRUD de solicitudes
└── docs/capturas/             Capturas de la app funcionando
```

## Arquitectura
```
App (View → ViewModel → Repository → Retrofit) ──HTTP/JSON──► Microservicio (Controller → Service → Repository JPA) ──► Base de datos
                                         └──────HTTPS──────► API externa mindicador.cl
```

### App móvil (MVVM)
```
app-movil/app/src/main/java/cl/duoc/gestionsolicitudes/
├── data/
│   ├── remote/        RetrofitService, ApiResultado (llamarApi)
│   │   ├── api/       UsuarioApi, SolicitudApi, IndicadoresApi
│   │   └── dto/       LoginRequest, UsuarioDto, SolicitudRequest, IndicadoresResponse
│   └── repository/    UsuarioRepository, SolicitudRepository, IndicadoresRepository, QrRepository
├── model/             Solicitud, Indicador, LoginUiState, SolicitudUiState, ListaUiState, Catalogo
├── viewmodel/         LoginViewModel, SolicitudViewModel, IndicadoresViewModel, QrViewModel, AppViewModelFactory
├── ui/screen/         Login, Menú, Lista, Formulario, Indicadores, Lector QR
├── navigation/        AppNavigation
└── utils/             Cámara, QR, notificaciones, formato
```

### Microservicios (capas)
```
cl.duoc.<servicio>/
├── controller/   Endpoints REST (@RestController)
├── service/      Reglas del negocio (@Service)
├── repository/   Acceso a datos (Spring Data JPA)
├── model/        Entidades JPA (@Entity)
├── dto/          Objetos de entrada/salida con validaciones
├── exception/    Errores y @RestControllerAdvice
└── config/       Beans y datos iniciales (solo ms-usuarios)
```

## Endpoints
### ms-usuarios (http://localhost:8081)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/usuarios/login` | Inicia sesión (200 / 401) |
| POST | `/api/usuarios/registro` | Registra un usuario (201 / 409) |
| GET | `/api/usuarios` | Lista usuarios (sin contraseñas) |
| GET | `/api/usuarios/{id}` | Busca un usuario |

### ms-solicitudes (http://localhost:8082)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/solicitudes` | Lista todas (opcional `?correo=`) |
| GET | `/api/solicitudes/{id}` | Busca por id |
| GET | `/api/solicitudes/codigo/{codigo}` | Busca por código (lector QR) |
| POST | `/api/solicitudes` | Crea (código, fecha y estado "Recibida" los pone el servidor) |
| PUT | `/api/solicitudes/{id}` | Edita los datos |
| PATCH | `/api/solicitudes/{id}/estado` | Cambia solo el estado |
| DELETE | `/api/solicitudes/{id}` | Elimina (204) |

### API externa
`GET https://mindicador.cl/api` → UF, dólar, euro y UTM del día (pantalla *Indicadores económicos*).

## Cómo ejecutar
**Requisitos:** Android Studio (incluye el JDK 21) y un emulador API 26+.

1. Levantar los microservicios (dos terminales):
   ```bash
   cd microservicios/ms-usuarios && ./mvnw spring-boot:run
   ```
   ```bash
   cd microservicios/ms-solicitudes && ./mvnw spring-boot:run
   ```
   En Windows: `mvnw.cmd spring-boot:run`. También se pueden abrir en IntelliJ / VS Code y ejecutar la clase `*Application`.
2. Comprobar: http://localhost:8081/api/usuarios y http://localhost:8082/api/solicitudes
3. Abrir `app-movil/` en Android Studio, esperar el *Gradle Sync* y presionar **Run ▶**.
4. Ingresar con `cliente@demo.cl` / `demo123`.

> El emulador llega al computador por `10.0.2.2`. Para un teléfono físico, cambiar las URL en `app-movil/app/build.gradle.kts` (`buildConfigField`) por la IP del PC y agregarla a `res/xml/network_security_config.xml`.

**Base de datos:** H2 en archivo (`microservicios/*/data/`), consola en `/h2-console` (JDBC URL `jdbc:h2:file:./data/usuariosdb` o `./data/solicitudesdb`, usuario `sa`, sin clave). Para MySQL: `./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql`.

## Pruebas unitarias
| Proyecto | Comando | Qué se prueba |
|---|---|---|
| ms-usuarios | `./mvnw test` | `UsuarioServiceTest` (Mockito) y `UsuarioControllerTest` (MockMvc) – 8 pruebas |
| ms-solicitudes | `./mvnw test` | `SolicitudServiceTest` (Mockito) y `SolicitudControllerTest` (MockMvc) – 11 pruebas |
| app-movil | `./gradlew testDebugUnitTest` | ViewModels con MockK y repositorios con MockWebServer – 20 pruebas |

## Datos
Todos los usuarios y solicitudes son **ficticios**.
