-- Agregar columna is_admin a la tabla usuarios
ALTER TABLE usuarios ADD COLUMN is_admin BOOLEAN DEFAULT FALSE;

-- Crear índice para is_admin si es necesario
CREATE INDEX idx_usuarios_is_admin ON usuarios(is_admin);
