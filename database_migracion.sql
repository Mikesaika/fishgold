-- Ejecutar sobre una BD fishgold_db ya existente (respaldar antes).
USE fishgold_db;

-- Precio por kilo (Verónica): actualizar semilla si aplica
INSERT IGNORE INTO configuracion_pago (id, pago_kilo_base) VALUES (1, 2.50);
UPDATE configuracion_pago SET pago_kilo_base = 2.50 WHERE id = 1 AND pago_kilo_base = 5.0;

-- Planificación: soft delete y estado Cancelada
ALTER TABLE planificaciones
    ADD COLUMN IF NOT EXISTS activo TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE planificaciones
    MODIFY COLUMN estado ENUM('Pendiente', 'En Curso', 'Finalizado', 'Cancelada') DEFAULT 'Pendiente';

-- Asistencia: estado del tripulante
ALTER TABLE faena_asistencia
    ADD COLUMN IF NOT EXISTS estado_asistencia ENUM('Presente', 'Ausente', 'Justificado') NOT NULL DEFAULT 'Presente';
