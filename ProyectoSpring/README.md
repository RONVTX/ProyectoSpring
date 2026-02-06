# SaaS Platform - Plataforma de Suscripciones

## Descripción

Plataforma SaaS (Software as a Service) completa desarrollada con Spring Boot 4.0.2, JPA, Hibernate Envers y MySQL. El sistema permite a los usuarios registrarse, elegir planes de suscripción y genera facturas automáticas cada 30 días.

## Características Principales

✅ **Registro de Usuarios**: Formulario de registro seguro con validación
✅ **Planes de Suscripción**: 3 planes disponibles (Basic, Premium, Enterprise)
✅ **Cambio de Planes**: Permite cambiar entre planes con cálculo automático de prorrateo
✅ **Facturación Automática**: Se generan facturas cada 30 días
✅ **Prorrateo Inteligente**: Si cambias a un plan más caro, se cobra solo por los días restantes
✅ **Auditoría Completa**: Historial de cambios con Hibernate Envers
✅ **Interfaz Web**: Vistas intuitivas con Thymeleaf y Bootstrap
✅ **Pruebas Unitarias**: Suite completa de tests con JUnit 5 y Mockito

## Requisitos Previos

- Java 21 o superior
- MySQL 8.0 o superior
- Maven 3.8.1 o superior

## Configuración de MySQL

Antes de ejecutar la aplicación, crea la base de datos:

```sql
CREATE DATABASE saas_platform;
USE saas_platform;
```

> **Nota**: La aplicación creará automáticamente las tablas con `spring.jpa.hibernate.ddl-auto=update`

## Estructura del Proyecto

```
ProyectoSpring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/ProyectoSpring/
│   │   │       ├── controllers/        # Controladores MVC
│   │   │       ├── entities/           # Modelos JPA
│   │   │       ├── repositories/       # Acceso a datos
│   │   │       ├── services/           # Lógica de negocio
│   │   │       ├── dtos/               # Data Transfer Objects
│   │   │       ├── enums/              # Enumeraciones
│   │   │       └── config/             # Configuración
│   │   └── resources/
│   │       ├── templates/              # Vistas Thymeleaf
│   │       └── application.properties  # Configuración
│   └── test/
│       └── java/
│           └── com/example/ProyectoSpring/
│               └── services/           # Tests unitarios
└── pom.xml                             # Dependencias Maven
```

## Entidades Principales

### Usuario
- Email único
- Nombre y apellido
- Contraseña (almacenar con hash en producción)
- Estado activo/inactivo
- Fecha de registro
- Auditado con Envers

### Plan
- 3 niveles: BASIC ($9.99), PREMIUM ($29.99), ENTERPRISE ($99.99)
- Descripción y límite de características
- Enum `NivelPlan` para facilitar el manejo

### Suscripción
- Usuario - Plan
- Estado: ACTIVA, CANCELADA, MOROSA
- Fechas de inicio, renovación y próximo pago
- Auto-renovación configurable
- Auditado con Envers para historial completo

### Factura
- Número único de factura
- Montos: base, prorrateo y total
- Estado: PENDIENTE, PAGADA, VENCIDA, CANCELADA
- Fechas de emisión, vencimiento y pago

### Pago (Herencia)
- Clase base con herencia SINGLE_TABLE
- Subclases: PagoTarjeta, PagoPaypal, PagoTransferencia
- Referencia de transacción
- Auditado

## Instalación y Ejecución

### 1. Clonar o descargar el proyecto

```bash
cd ProyectoSpring
```

### 2. Configurar MySQL

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_platform
spring.datasource.username=root
spring.datasource.password=
```

### 3. Construir el proyecto

```bash
./mvnw clean install
```

o en Windows:

```bash
mvnw.cmd clean install
```

### 4. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## Uso de la Aplicación

### Flujo Principal

1. **Inicio** (`/`): Página de bienvenida
2. **Registrarse** (`/usuarios/registro`): Crear nueva cuenta
3. **Ver Planes** (`/planes`): Listar todos los planes
4. **Seleccionar Plan** (`/planes/{usuarioId}/seleccionar`): Elegir un plan
5. **Suscripción** (`/suscripciones/{id}`): Ver detalles de la suscripción
6. **Cambiar Plan** (`/suscripciones/{id}/cambiar-plan`): Cambiar a otro plan
7. **Facturas** (`/facturas`): Ver todas las facturas

### Ejemplo de Cambio de Plan con Prorrateo

1. Usuario con plan BASIC ($9.99/mes) se registra el 1 de febrero
2. El 10 de febrero, cambia a plan PREMIUM ($29.99/mes)
3. Sistema calcula:
   - Diferencia: $29.99 - $9.99 = $20.00
   - Días restantes: 18 días (hasta el 28 de febrero)
   - Prorrateo: ($20.00 / 30) × 18 = $12.00
4. Se genera factura por $12.00 (prorrateo) + $29.99 (premium) = $41.99

## Pruebas Unitarias

Ejecutar todas las pruebas:

```bash
./mvnw test
```

Ejecutar pruebas específicas:

```bash
./mvnw test -Dtest=ProrrateoServiceTest
./mvnw test -Dtest=SuscripcionServiceTest
./mvnw test -Dtest=FacturaServiceTest
```

## Cobertura de Pruebas

- **ProrrateoServiceTest**: 10 test cases
  - Cálculo de prorrateo con plan más caro
  - Validación de parámetros
  - Cálculo de días restantes
  - Casos especiales (planes iguales, plan más barato)

- **SuscripcionServiceTest**: 6 test cases
  - Creación de suscripción
  - Cambio de plan
  - Cancelación
  - Obtención de suscripción activa

- **UsuarioServiceTest**: 6 test cases
  - Registro de usuario
  - Validación de email duplicado
  - Búsqueda por ID y email
  - Desactivación de usuario

- **FacturaServiceTest**: 8 test cases
  - Generación de factura
  - Cálculo de prorrateo en factura
  - Marcación como pagada
  - Búsqueda de facturas vencidas

- **PlanServiceTest**: 5 test cases
  - Obtención de planes
  - Inicialización de planes
  - Filtrado de planes activos

**Total: 35 test cases**

## APIs HTTP Principales

### Usuarios
- `GET /usuarios/registro` - Formulario de registro
- `POST /usuarios/registro` - Registrar usuario
- `GET /usuarios/{id}` - Ver perfil

### Planes
- `GET /planes` - Listar planes
- `GET /planes/{usuarioId}/seleccionar` - Formulario de selección
- `POST /planes/{usuarioId}/seleccionar` - Seleccionar plan

### Suscripciones
- `GET /suscripciones/{id}` - Ver detalles
- `GET /suscripciones/{id}/cambiar-plan` - Formulario de cambio
- `POST /suscripciones/{id}/cambiar-plan` - Cambiar plan
- `GET /suscripciones/{id}/cancelar` - Confirmar cancelación
- `POST /suscripciones/{id}/cancelar` - Cancelar

### Facturas
- `GET /facturas` - Listar pendientes
- `GET /facturas/vencidas` - Listar vencidas
- `GET /facturas/{id}` - Ver detalle
- `POST /facturas/{id}/pagar` - Marcar como pagada

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| Spring Boot | 4.0.2 | Framework principal |
| Spring Data JPA | 4.0.2 | Acceso a datos |
| Hibernate | 6.4.x | ORM |
| Hibernate Envers | 6.4.x | Auditoría y historial |
| MySQL | 8.0+ | Base de datos |
| Thymeleaf | 3.1.x | Motor de plantillas |
| Bootstrap | 5.3.0 | Estilos CSS |
| JUnit 5 | 5.x | Testing |
| Mockito | 5.x | Mock objects |
| Lombok | 1.18.x | Reducción de boilerplate |

## Diagrama E-R

```
┌─────────────┐
│   Usuario   │
├─────────────┤
│ id (PK)     │
│ email       │
│ nombre      │
│ apellido    │
│ password    │
└─────────────┘
       │
       ├─────────────────┐
       │                 │
       │            ┌──────────────┐
       │            │   Suscripción│
       │            ├──────────────┤
       └────1:N────>│ id (PK)      │
                    │ usuario_id   │
                    │ plan_id (FK) │
                    │ estado       │
                    └──────────────┘
                            │
                      ┌─────┘
                      │
                 ┌─────────┐
                 │  Plan   │
                 ├─────────┤
         1:N────>│ id (PK) │
                 │ nivel   │
                 │ precio  │
                 └─────────┘
```

## Configuración de Base de Datos

### application.properties

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/saas_platform
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Envers
spring.jpa.properties.org.hibernate.envers.audit_table_suffix=_audit
spring.jpa.properties.org.hibernate.envers.revision_field_name=rev
spring.jpa.properties.org.hibernate.envers.revision_type_field_name=revtype

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML
```

## Lógica de Prorrateo

La lógica se encuentra en `ProrrateoService`:

```
Fórmula: Prorrateo = ((Precio nuevo - Precio anterior) / 30) × Días restantes

Ejemplo:
- Plan anterior: BASIC ($9.99)
- Plan nuevo: PREMIUM ($29.99)
- Días restantes: 20

Cálculo:
1. Diferencia: $29.99 - $9.99 = $20.00
2. Precio diario: $20.00 / 30 = $0.67
3. Prorrateo: $0.67 × 20 = $13.40
```

## Auditoría con Envers

Cada cambio en Usuario, Suscripción, Plan y Factura se registra automáticamente:

- **Tabla de revisiones**: `revisiones`
- **Tablas de auditoría**: `usuarios_audit`, `suscripciones_audit`, etc.
- **Información capturada**:
  - Qué cambió (campo y valor anterior/nuevo)
  - Quién lo cambió
  - Cuándo se cambió
  - Número de revisión

## Seguridad

⚠️ **Importante para Producción**:
- Hashear contraseñas con BCrypt o similar
- Usar HTTPS
- Implementar JWT o sesiones seguras
- Validar y sanitizar entrada
- Implementar rate limiting
- Usar variables de entorno para credenciales

## Troubleshooting

### Error de conexión a MySQL

```
Error: Unable to connect to database
```

**Solución**: Verificar que MySQL está corriendo y que las credenciales en `application.properties` son correctas.

### Error: "Table 'saas_platform.usuarios' doesn't exist"

**Solución**: La base de datos se crea automáticamente. Si persiste, ejecutar:

```sql
CREATE DATABASE IF NOT EXISTS saas_platform;
```

### Error de puertos

```
Address already in use: java.net.BindException
```

**Solución**: Cambiar puerto en `application.properties`:

```properties
server.port=8081
```

## Próximas Mejoras

- [ ] Integración de pagos (Stripe/PayPal)
- [ ] Notificaciones por email
- [ ] Dashboard de administrador
- [ ] API REST completa
- [ ] Autenticación con OAuth2
- [ ] Múltiples idiomas
- [ ] Modo oscuro
- [ ] Reportes de ingresos

## Licencia

Este proyecto es de código abierto. Úsalo libremente.

## Contacto

Para preguntas o reportar bugs, contacta a través de Issues en el repositorio.

---

**Versión**: 1.0.0  
**Última actualización**: Febrero 2026
