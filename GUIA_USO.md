# Guía de Uso - SaaS Platform

## Primeros Pasos

### 1. Iniciar la Aplicación

#### Windows
```bash
# En PowerShell o Command Prompt
.\start.bat
```

#### macOS/Linux
```bash
bash start.sh
```

O manualmente:
```bash
./mvnw spring-boot:run
```

### 2. Acceder a la Aplicación

Abre tu navegador y ve a: **http://localhost:8080**

Deberías ver la página de inicio con un botón "Comenzar Ahora".

## Flujos de Usuario

### Flujo 1: Registro y Primera Suscripción

1. **Haz clic en "Comenzar Ahora"** o ve a `/usuarios/registro`

2. **Completa el formulario de registro**:
   - Nombre: Juan
   - Apellido: Pérez
   - Email: juan@example.com
   - Contraseña: password123
   - Confirmar Contraseña: password123

3. **Haz clic en "Registrarse"**
   - Se crea tu cuenta
   - Serás redirigido automáticamente a la página de selección de planes

4. **Selecciona un plan**:
   - **BASIC**: $9.99/mes - 100 características
   - **PREMIUM**: $29.99/mes - 500 características
   - **ENTERPRISE**: $99.99/mes - 5000 características

5. **Verás los detalles de tu suscripción**:
   - Plan actual
   - Fecha de inicio
   - Próximo pago (30 días después)
   - Botones para cambiar plan o cancelar

### Flujo 2: Cambiar de Plan (con Prorrateo)

Suponiendo que registraste el 1 de febrero con plan BASIC:

1. **En la página de suscripción, haz clic en "Cambiar Plan"**

2. **Selecciona el nuevo plan**: Por ejemplo, PREMIUM

3. **Se genera automáticamente una factura de prorrateo**:
   - Si cambias el 10 de febrero:
     - Días restantes: 18 (hasta el 28 de febrero)
     - Prorrateo: ($29.99 - $9.99) / 30 × 18 = $12.00
     - Total a cobrar: $12.00

4. **Revisa la factura en el apartado de Facturas**

### Flujo 3: Gestión de Facturas

1. **Ve a "Facturas"** en el menú principal

2. **Verás dos opciones**:
   - **Facturas Pendientes**: Facturas sin pagar
   - **Facturas Vencidas**: Facturas vencidas

3. **Para cada factura puedes**:
   - Ver detalles haciendo clic en "Ver"
   - Marcar como pagada si está pendiente

4. **En el detalle de la factura verás**:
   - Número de factura
   - Datos del cliente
   - Desglose de montos (base + prorrateo)
   - Fechas importantes

### Flujo 4: Cancelar Suscripción

1. **En la página de suscripción, haz clic en "Cancelar Suscripción"**

2. **Confirma la cancelación**:
   - Lee la información importante
   - Haz clic en "Sí, Cancelar mi Suscripción"

3. **Tu suscripción pasará a estado CANCELADA**:
   - No se generarán nuevas facturas
   - Perderás acceso a las características premium

## Casos de Uso Prácticos

### Caso 1: Escalamiento de Negocio

Un freelancer comienza con BASIC y después de 15 días necesita más características:

```
1. Se registra el 1 de febrero con BASIC ($9.99)
2. El 16 de febrero cambia a PREMIUM ($29.99)
3. Sistema calcula automáticamente:
   - Prorrateo: $12.00
   - Próxima facturación completa: 28 de febrero
4. El 28 de febrero se genera factura de $29.99
5. El 28 de marzo se genera factura de $29.99
```

### Caso 2: Downgrade de Plan

Si cambias a un plan más barato:

```
1. Tienes PREMIUM hasta el 28 de febrero
2. El 10 de febrero cambias a BASIC
3. Sistema NO cobra prorrateo (plan más barato)
4. El cambio entra en vigor el 28 de febrero
5. Desde marzo pagas $9.99/mes
```

### Caso 3: Ciclo de Facturación

```
Feb 1: Registrado con BASIC
Feb 1: Factura #1 generada ($9.99) - Vencimiento: Feb 15
Feb 15: Factura pagada
Mar 1: Factura #2 generada ($9.99) - Vencimiento: Mar 15
Mar 10: Cambio a PREMIUM
        Factura #2A generada ($12.00 prorrateo)
Mar 1-31: PREMIUM activo
Apr 1: Factura #3 generada ($29.99)
```

## Navegación de Vistas

### Página de Inicio (`/`)
- Información sobre la plataforma
- Botón para registrarse
- Características principales

### Registro (`/usuarios/registro`)
- Formulario con validación
- Campos: Nombre, Apellido, Email, Contraseña
- Validación de email duplicado

### Planes (`/planes`)
- Lista de todos los planes activos
- Precio y características de cada plan
- Botón para seleccionar

### Seleccionar Plan (`/planes/{usuarioId}/seleccionar`)
- Detalles completos de cada plan
- Botón de selección para cada uno

### Suscripción (`/suscripciones/{id}`)
- Estado actual de la suscripción
- Información del plan
- Fechas importantes
- Botones de acción

### Cambiar Plan (`/suscripciones/{id}/cambiar-plan`)
- Plan actual en comparación
- Selector de nuevo plan
- Información sobre prorrateo

### Cancelar (`/suscripciones/{id}/cancelar`)
- Confirmación con advertencias
- Información sobre consecuencias

### Facturas (`/facturas`)
- Tabla de facturas pendientes
- Links a facturas vencidas
- Botones de acción para cada factura

### Detalle de Factura (`/facturas/{id}`)
- Información completa del documento
- Desglose de montos
- Datos del cliente
- Opción de marcar como pagada

## Consejos y Trucos

### ✅ Lo que puedes hacer
- Cambiar de plan en cualquier momento
- Cancelar tu suscripción en cualquier momento
- Ver el historial completo de facturas
- Descargar facturas (próxima versión)

### ❌ Lo que NO puedes hacer
- Cambiar el precio de los planes (administrador solo)
- Ver facturas de otros usuarios
- Modificar facturas después de emitidas
- Crear múltiples suscripciones simultáneamente

## Datos de Prueba

Si quieres probar la aplicación rápidamente:

```
Nombre: Demo
Apellido: User
Email: demo@example.com
Contraseña: demo123
```

Después de registrarse, selecciona cualquier plan y experimenta con los cambios.

## Problemas Comunes

### "Email ya está registrado"
- Intenta con otro email
- O recupera tu contraseña (próxima versión)

### "La suscripción no se creó"
- Verifica que el plan existe
- Comprueba que no tienes otra suscripción activa

### "La factura no se generó"
- Espera a que se ejecute el trabajo programado
- Recarga la página

### "No puedo cambiar el plan"
- Solo puedes cambiar desde una suscripción ACTIVA
- Cancela cualquier otra suscripción si la hay

## Validaciones y Reglas

### Validaciones de Registro
- Email debe ser válido y único
- Contraseña debe coincidir
- Todos los campos obligatorios

### Validaciones de Suscripción
- Un usuario solo puede tener una suscripción activa
- Solo se puede cambiar plan desde estado ACTIVA
- No se puede cambiar si ya estás cancelando

### Validaciones de Factura
- No se puede marcar como pagada si ya está pagada
- El vencimiento es 15 días después de emisión
- El prorrateo solo se aplica para planes más caros

## Soporte y Ayuda

### Para reportar un bug
- Ve al repositorio del proyecto
- Abre un Issue describiendo el problema
- Incluye pasos para reproducir

### Para sugerencias
- También puedes abrir un Issue (tipo Enhancement)
- Describe tu idea y por qué sería útil

---

¡Disfruta usando SaaS Platform! 🚀
