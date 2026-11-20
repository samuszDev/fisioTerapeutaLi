# FisioterapiaLi — La Fisioterapeuta Li

Plataforma de gestión para el consultorio de fisioterapia de **Lina Murillo** (Boyacá, Colombia), desarrollada para el **Reto Hackathon 2026**.

Los pacientes agendan sus citas desde un **bot de Telegram** con inteligencia artificial; la administradora gestiona agenda, pacientes, servicios e historias clínicas desde un **panel web**.

---

## Tabla de contenido

- [Funcionalidades](#funcionalidades)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Integración con el bot (n8n)](#integración-con-el-bot-n8n)
- [API principal](#api-principal)
- [Reglas de negocio](#reglas-de-negocio)
- [Ramas del repositorio](#ramas-del-repositorio)
- [Equipo](#equipo)

---

## Funcionalidades

**Para pacientes (Telegram)**
- Agendar citas conversando en lenguaje natural con el asistente.
- Consultar horarios disponibles por fecha.
- Enviar la foto del comprobante de pago para confirmar la cita.
- Recibir la confirmación por Telegram y por correo.

**Para la administradora (Telegram + panel web)**
- Consultar la agenda del día, cancelar y reprogramar citas desde Telegram.
- Aprobar o rechazar comprobantes de pago con un botón.
- Consultar carpetas de Google Drive (comprobantes, clientes, finanzas…).
- Panel web con dashboard, agenda, pacientes, servicios, reportes e historia clínica.

**Sitio público**
- Páginas de inicio, servicios, sobre nosotros y contacto.

---

## Arquitectura

```
Paciente / Lina ──► Telegram ──► n8n ──► IA (Gemini / Groq)
                                  │
                                  ├──► API /api/bot/**  (Spring Boot) ──► PostgreSQL
                                  ├──► Google Calendar  (eventos de citas)
                                  ├──► Google Drive     (comprobantes)
                                  └──► Gmail            (correo de confirmación)

Lina ──► Panel web /admin (Spring Boot + Thymeleaf) ──► PostgreSQL
```

Principio de diseño: **la IA interpreta, el backend valida, n8n orquesta.**
La IA solo extrae los datos del mensaje; todas las reglas (horario, disponibilidad, sede, precio) las aplica el backend.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 17, Spring Boot 4, Spring Data JPA, Spring Security, Lombok |
| Vistas | Thymeleaf, HTML, CSS, JavaScript |
| Base de datos | PostgreSQL |
| Automatización | n8n (Telegram, Google Calendar, Google Drive, Gmail) |
| IA | Google Gemini 2.5 Flash, Groq (modelo de respaldo) |
| Build | Maven (wrapper incluido) |

---

## Estructura del proyecto

```
fisioTerapeutaLi/
├── FisioterapiaLi/                  # Backend Spring Boot + panel web
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/usta/fisioterapiali/
│       │   ├── controllers/     # Vistas, API del panel (/api/modal) y API del bot (/api/bot)
│       │   ├── entities/        # Entidades JPA (usuario, reserva, servicio, horario, historia clínica…)
│       │   ├── enums/           # Catálogos (EPS, motivo de consulta, tipo de dolor, roles…)
│       │   ├── models/Daos/     # Repositorios Spring Data
│       │   ├── models/Service/  # Servicios de negocio
│       │   ├── dto/             # Objetos de entrada/salida del bot
│       │   ├── security/        # Configuración de Spring Security
│       │   ├── components/      # Datos iniciales (usuarios, servicios, horarios)
│       │   └── util/            # Cálculo de signos vitales (IMC, tensión, dolor EVA…)
│       └── resources/
│           ├── application.properties
│           ├── templates/       # Páginas públicas y panel administrativo (Admin/)
│           └── static/          # CSS, JS e imágenes
└── n8n/                             # Automatizaciones (bot de Telegram y recordatorios)
    ├── README.md
    └── workflows/               # Flujos de n8n exportados en JSON
```

---

## Requisitos

- **JDK 17** o superior
- **PostgreSQL** 14 o superior
- **n8n** (opcional, solo para el bot de Telegram)

---

## Instalación y ejecución

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/samuszDev/fisioTerapeutaLi.git
   cd fisioTerapeutaLi/FisioterapiaLi
   ```

2. **Crear la base de datos** en PostgreSQL

   ```sql
   CREATE DATABASE "fisioterapiaLi";
   CREATE USER user_java WITH PASSWORD 'tu_clave';
   GRANT ALL PRIVILEGES ON DATABASE "fisioterapiaLi" TO user_java;
   ```

3. **Configurar la conexión** en `src/main/resources/application.properties`

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/fisioterapiaLi
   spring.datasource.username=user_java
   spring.datasource.password=tu_clave
   ```

4. **Ejecutar la aplicación**

   ```bash
   ./mvnw spring-boot:run        # Linux / macOS
   mvnw.cmd spring-boot:run      # Windows
   ```

5. Abrir **http://localhost:8080**. El panel administrativo está en **http://localhost:8080/admin**.

Al iniciar por primera vez, la aplicación crea las tablas automáticamente y carga datos iniciales: usuarios de prueba (administradora, fisioterapeuta y paciente), el catálogo de servicios y el horario de atención. Las credenciales de prueba están en `components/DataInitializer.java` — **cámbialas antes de usar el sistema en producción.**

---

## Integración con el bot (n8n)

Los flujos de n8n están en la carpeta [`n8n/workflows/`](n8n/workflows):

| Flujo | Qué hace |
|---|---|
| Asistente Empresarial Fisioterapeuta Li (Telegram + IA local) | Bot de Telegram con IA: agenda, consulta, cancela y reprograma citas, y gestiona la aprobación de pagos |
| Recordatorios Automáticos | Envía todos los días recordatorios por correo a los pacientes con citas próximas |

La explicación detallada de cada flujo y los pasos de instalación están en el [README de n8n](n8n/README.md). En resumen:

1. Importar los JSON en n8n.
2. Configurar las credenciales: Telegram, Google Calendar, Google Drive, Gmail, Gemini y Groq.
3. Verificar que los nodos HTTP apunten al backend (por defecto `http://host.docker.internal:8080`).
4. Registrar en la tabla `usuario` el **ID de Telegram de la administradora** con rol `ADMIN`, para que el bot la reconozca.
5. Activar los flujos.

**Flujo de una cita**

1. El paciente escribe al bot; la IA recoge nombre, teléfono, correo, servicio y fecha.
2. n8n llama a `POST /api/bot/pre-reserva`; el backend valida y aparta el espacio (`PRE_RESERVA`).
3. Se crea el evento en Google Calendar con la etiqueta `[PENDIENTE PAGO]`.
4. El paciente envía la foto del comprobante; se guarda en Drive y se le envía a Lina.
5. Lina aprueba con un botón → `PATCH /api/bot/reserva/{id}/confirmar` → la cita queda `CONFIRMADA`, se actualiza el calendario y se envía el correo.

---

## API principal

### Bot (`/api/bot`) — consumida por n8n

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/pre-reserva` | Crea una pre-reserva validando horario y disponibilidad |
| `PATCH` | `/reserva/{id}/calendar` | Guarda el ID del evento de Google Calendar |
| `PATCH` | `/reserva/{id}/confirmar` | Confirma el pago de una reserva |
| `PATCH` | `/reserva/{id}/cancelar` | Cancela una reserva y libera el horario |
| `PATCH` | `/reserva/{id}/reprogramar?fechaInicio=` | Mueve una reserva a otra fecha y hora |
| `GET` | `/disponibilidad?fecha=` | Horas libres de un día (para pacientes) |
| `GET` | `/agenda-admin?fecha=` | Agenda del día con pacientes (para la administradora) |
| `GET` | `/usuario/{telegramUserId}` | Identifica al usuario y su rol |
| `GET` | `/reserva/pendiente/{telegramUserId}` | Reserva pendiente de pago del paciente |

### Panel (`/api/modal`) — requiere sesión iniciada

Citas, pacientes, servicios, historia clínica, estadísticas del dashboard y reportes (`/citas`, `/pacientes`, `/servicios`, `/historia-clinica/{idUsuario}`, `/dashboard/stats`, `/reportes/historial`).

---

## Reglas de negocio

- **Horario de atención:** todos los días de 7:00 a.m. a 8:00 p.m.
- **Almuerzo:** de 12:00 p.m. a 2:00 p.m. no se agendan citas.
- **Sedes:** Tunja de lunes a viernes; Turmequé sábados y domingos.
- **Duración:** la hora de fin se calcula según la duración del servicio.
- **Pagos:** la cita queda como `PRE_RESERVA` hasta que la administradora confirma el comprobante. Si la agenda la propia administradora, queda `CONFIRMADA` de inmediato.
- **Datos personales:** el tratamiento de datos se informa conforme a la Ley 1581 de 2012.

---

## Ramas del repositorio

| Rama | Contenido |
|---|---|
| `main` | Versión estable del proyecto completo (backend y flujos de n8n) |
| `Staging` | Rama de integración y pruebas antes de pasar a `main` |
| `n8n` | Rama de trabajo de los flujos de n8n (la versión estable se publica en `main`, carpeta `n8n/`) |

**Flujo de trabajo sugerido:** desarrollar en una rama propia → unir a `Staging` → probar → pasar a `main`.

---

## Equipo

Proyecto desarrollado por estudiantes de la **Universidad Santo Tomás** para el Reto Hackathon 2026:

1. Samuel Salazar Zuluaga
2. Jimmy Mateo Valero Cuadrado
3. Javier Alexander Pedraza Rodríguez
4. Andres Felipe Arias Vargas
5. Lina Fernanda Rodríguez Barreto
6. Laura Sofia Camacho Mojica
7. David Felipe Pérez Jaime
8. Paula Andrea Fuquene García
