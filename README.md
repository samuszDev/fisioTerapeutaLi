# FisioterapiaLi — Flujos de n8n

Esta rama contiene los **flujos de automatización en n8n** de la plataforma *La Fisioterapeuta Li*, desarrollada para el **Reto Hackathon 2026**.

El backend (Spring Boot + PostgreSQL) y el panel web están en la rama [`main`](https://github.com/samuszDev/fisioTerapeutaLi/tree/main). Los flujos de esta rama se comunican con ese backend a través de su API `/api/bot/**`.

---

## Contenido

| Archivo | Qué hace |
|---|---|
| [`workflows/Asistente Empresarial Fisioterapeuta Li (Telegram + IA local).json`](workflows/) | Bot de Telegram con IA: agenda, consulta, cancela y reprograma citas, y gestiona la aprobación de pagos |
| [`workflows/Recordatorios Automáticos.json`](workflows/) | Envía todos los días recordatorios por correo a los pacientes con citas próximas |

---

## Arquitectura

```
Paciente / Lina ──► Telegram ──► n8n ──► IA (Gemini / Groq)
                                  │
                                  ├──► Backend /api/bot/**  (Spring Boot) ──► PostgreSQL
                                  ├──► Google Calendar      (eventos de citas)
                                  ├──► Google Drive         (comprobantes de pago)
                                  └──► Gmail                (confirmaciones y recordatorios)
```

Principio de diseño: **la IA interpreta, el backend valida, n8n orquesta.**
La IA solo extrae los datos del mensaje; las reglas de negocio (horario, disponibilidad, sede, precio) las aplica el backend.

---

## Flujo 1: Asistente Empresarial (Telegram + IA)

Recibe los mensajes del bot de Telegram, identifica quién escribe y responde según su rol.

### Identificación del usuario

1. **Telegram Trigger** recibe el mensaje, la foto o el botón presionado.
2. **Tipo de Update** separa tres casos: botón de aprobación, foto de comprobante o mensaje de texto.
3. **Identificar usuario** consulta `GET /api/bot/usuario/{telegramId}` y obtiene el rol.
4. **Rol** envía el mensaje al agente correspondiente:
   - `ADMIN` / `FISIO` → **Agente Admin**
   - Cualquier otro → **Agente Cliente**

### Agentes de IA

Los dos agentes usan **Google Gemini 2.5 Flash** como modelo principal y **Groq** como respaldo, con memoria de conversación por chat. Responden siempre con un JSON de la forma:

```json
{ "accion": "agendar", "datos": { ... }, "respuesta": "texto para el usuario" }
```

| Acción | Agente Cliente | Agente Admin |
|---|:---:|:---:|
| `agendar` / `faltan_datos` — apartar una cita | ✅ | ✅ |
| `consultar` — horarios libres o agenda del día | ✅ (solo horas libres) | ✅ (agenda con pacientes) |
| `cancelar` — cancelar una cita por su número | | ✅ |
| `reprogramar` — mover una cita a otra fecha | | ✅ |
| `drive` — listar archivos de una carpeta | | ✅ |
| `otro` — saludos, precios, servicios, ubicación | ✅ | ✅ |

El nodo **Parsear salida** guarda un borrador de la cita por chat durante 30 minutos, para que el paciente pueda dar los datos en varios mensajes. El comando `/reset` borra el borrador.

### Agendar una cita

1. La IA recoge los 5 datos: nombre, teléfono, correo, servicio y fecha.
2. **Crear pre-reserva** → `POST /api/bot/pre-reserva`.
3. Según la respuesta del backend:
   - **201 (creada):** se crea el evento en Google Calendar con la etiqueta `[PENDIENTE PAGO]`, se guarda su ID con `PATCH /api/bot/reserva/{id}/calendar` y se le envían al paciente los datos para el pago.
   - **409 (ocupado):** se ofrecen horarios alternativos del mismo día.
   - **400 (datos inválidos):** se explica el problema al paciente.

### Confirmación del pago

1. El paciente envía la **foto del comprobante** al chat.
2. Se busca su reserva pendiente (`GET /api/bot/reserva/pendiente/{telegramId}`).
3. La foto se guarda en **Google Drive** y se le reenvía a Lina con dos botones: **Confirmar pago** y **Rechazar**.
4. Al presionar un botón se verifica que quien lo hace sea `ADMIN`.
   - **Confirmar:** `PATCH /api/bot/reserva/{id}/confirmar` → se quita la etiqueta del evento de Calendar, se envía el correo de confirmación y se avisa al paciente y a Lina por Telegram.
   - **Rechazar:** se le pide al paciente que reenvíe el comprobante. La cita sigue apartada.

### Cancelar y reprogramar (solo administración)

- **Cancelar:** `PATCH /api/bot/reserva/{id}/cancelar` y se borra el evento de Calendar.
- **Reprogramar:** `PATCH /api/bot/reserva/{id}/reprogramar?fechaInicio=...` y se mueve el evento de Calendar. Si el nuevo horario no está disponible, se ofrecen alternativas.

---

## Flujo 2: Recordatorios Automáticos

Se ejecuta **todos los días a las 8:00 a.m.**

1. **Consultar Agenda Backend** → `GET /api/bot/agenda?desde=mañana&hasta=pasado mañana`.
2. **Normalizar Citas** filtra las citas que faltan 1 o 2 días, que no estén canceladas y cuyo paciente tenga correo.
3. **Recorrer Citas** envía a cada paciente un **correo de recordatorio** con la fecha, la hora, la sede, el servicio, las indicaciones para la sesión y la política de cancelación.
4. Al terminar, se envía a Lina un **correo de resumen** con la lista de pacientes notificados.

---

## Endpoints del backend que usan los flujos

| Método | Ruta | Flujo |
|---|---|---|
| `GET` | `/api/bot/usuario/{telegramUserId}` | Asistente |
| `POST` | `/api/bot/pre-reserva` | Asistente |
| `PATCH` | `/api/bot/reserva/{id}/calendar` | Asistente |
| `PATCH` | `/api/bot/reserva/{id}/confirmar` | Asistente |
| `PATCH` | `/api/bot/reserva/{id}/cancelar` | Asistente |
| `PATCH` | `/api/bot/reserva/{id}/reprogramar` | Asistente |
| `GET` | `/api/bot/disponibilidad?fecha=` | Asistente |
| `GET` | `/api/bot/agenda-admin?fecha=` | Asistente |
| `GET` | `/api/bot/reserva/pendiente/{telegramUserId}` | Asistente |
| `GET` | `/api/bot/agenda?desde=&hasta=` | Recordatorios |

---

## Requisitos

- **n8n** (probado con n8n en Docker).
- El **backend** de la rama `main` en ejecución en el puerto `8080`.
- Un **bot de Telegram** creado con [@BotFather](https://t.me/BotFather).
- Cuentas de **Google** (Calendar, Drive y Gmail), **Google Gemini** y **Groq**.

---

## Instalación

1. **Levantar el backend** siguiendo el README de la rama `main`.

2. **Importar los flujos** en n8n: *Workflows → Import from File* y seleccionar cada JSON de la carpeta `workflows/`.

3. **Crear las credenciales** en n8n y asignarlas a los nodos correspondientes:

   | Credencial | Nodos |
   |---|---|
   | Telegram API (token del bot) | Telegram Trigger y todos los nodos de envío por Telegram |
   | Google Calendar OAuth2 | Crear Pre-Reserva, Quitar PENDIENTE PAGO, Borrar Evento, Mover Evento |
   | Google Drive OAuth2 | Subir a Drive, Buscar carpeta, Listar Archivos |
   | Gmail OAuth2 | Correo de confirmación, recordatorios y resumen |
   | Google Gemini API | Google Gemini Chat Model |
   | Groq API | Groq Chat Model |

   Los JSON **no incluyen claves ni tokens**: solo la referencia a cada credencial, que hay que volver a crear en cada instalación de n8n.

4. **Ajustar los datos propios** del consultorio:
   - La **URL del backend** en los nodos HTTP. Por defecto es `http://host.docker.internal:8080`, que funciona cuando n8n corre en Docker y el backend en la misma máquina.
   - El **calendario** de Google Calendar y la **carpeta** de Google Drive.
   - El **ID de chat de Telegram de la administradora** en los nodos *Comprobante a Lina* y *Botones de aprobación*.
   - El **correo de destino del resumen** en el nodo *Send a message* del flujo de recordatorios.

5. **Registrar a la administradora** en la tabla `usuario` del backend con su ID de Telegram y el rol `ADMIN`, para que el bot la reconozca.

6. **Configurar la zona horaria** de n8n en `America/Bogota` (variable `GENERIC_TIMEZONE`), para que las fechas de las citas y la hora de los recordatorios sean correctas.

7. **Activar** los dos flujos.

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
