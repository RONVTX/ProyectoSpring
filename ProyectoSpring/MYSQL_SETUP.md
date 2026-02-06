# Configuración de MySQL para SaaS Platform

## Instalación de MySQL

### Windows
1. Descargar desde: https://dev.mysql.com/downloads/mysql/
2. Ejecutar el instalador
3. Seguir los pasos del asistente
4. Recordar el usuario (por defecto `root`) y contraseña

### macOS
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo mysql_secure_installation
```

## Verificar Instalación

```bash
mysql --version
mysql -u root -p
```

## Crear Base de Datos

```sql
-- Conectar a MySQL
mysql -u root -p

-- Crear la base de datos
CREATE DATABASE saas_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Ver bases de datos
SHOW DATABASES;

-- Salir
EXIT;
```

## Configuración en application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_platform
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA_AQUI
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

# Si tienes MySQL en otro servidor
# spring.datasource.url=jdbc:mysql://192.168.1.100:3306/saas_platform
```

## Cambiar Contraseña de MySQL

```bash
# En Linux/macOS
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nueva_contraseña';
FLUSH PRIVILEGES;

# En Windows (Command Prompt)
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nueva_contraseña';
FLUSH PRIVILEGES;
```

## Crear Usuario Específico para la App

```sql
-- Conectar como root
mysql -u root -p

-- Crear usuario
CREATE USER 'saas_app'@'localhost' IDENTIFIED BY 'password_seguro';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON saas_platform.* TO 'saas_app'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW GRANTS FOR 'saas_app'@'localhost';
```

Luego actualizar `application.properties`:
```properties
spring.datasource.username=saas_app
spring.datasource.password=password_seguro
```

## Verificar Conexión

Ejecutar la aplicación:
```bash
./mvnw spring-boot:run
```

Si ves en los logs:
```
Hibernate: create table usuarios ...
Hibernate: create table suscripciones ...
```

¡La conexión es exitosa!

## Backup y Restore

### Hacer Backup
```bash
mysqldump -u root -p saas_platform > backup_saas.sql
```

### Restaurar Backup
```bash
mysql -u root -p saas_platform < backup_saas.sql
```

## Troubleshooting

### "Access denied for user 'root'@'localhost'"
- Verificar contraseña
- Asegurarse de que MySQL está corriendo: `mysql.server status` (macOS) o ver servicios (Windows)

### "Can't connect to MySQL server on 'localhost:3306'"
- MySQL no está corriendo
- Inicia el servicio: `mysql.server start` (macOS) o busca MySQL en servicios (Windows)

### "Unknown database 'saas_platform'"
- Ejecutar: `CREATE DATABASE saas_platform;`
- Reiniciar la aplicación

## Monitorear la Base de Datos

```sql
-- Ver usuarios conectados
SHOW PROCESSLIST;

-- Ver tamaño de la base de datos
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.TABLES
WHERE table_schema = 'saas_platform'
ORDER BY size_mb DESC;

-- Ver número de registros
SELECT TABLE_NAME, TABLE_ROWS 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'saas_platform';
```

## Configuración Recomendada para Producción

```properties
# Encriptación SSL
spring.datasource.url=jdbc:mysql://localhost:3306/saas_platform?useSSL=true&serverTimezone=UTC

# Pool de conexiones
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Validación de conexiones
spring.datasource.hikari.connection-test-query=SELECT 1

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

---

Para más información, consulta: https://dev.mysql.com/doc/
