-- Actualizar usuarios sin país asignado a ES (España) por defecto
UPDATE usuarios SET pais = 'ES' WHERE pais IS NULL OR pais = '';
