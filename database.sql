CREATE DATABASE IF NOT EXISTS fishgold_db;
USE fishgold_db;

-- ==========================================================
-- 1. USUARIOS (Para el Login del sistema)
-- ==========================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre_usuario VARCHAR(100) DEFAULT NULL,
    rol ENUM('Administrador', 'Capitan', 'Digitador') DEFAULT 'Digitador',
    fecha_creacion TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 2. CONFIGURACIÓN (Cerebro financiero del precio del kilo)
-- ==========================================================
CREATE TABLE IF NOT EXISTS configuracion_pago (
    id INT NOT NULL DEFAULT 1,
    pago_kilo_base DOUBLE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unica_fila CHECK (id = 1) -- Garantiza que solo exista una configuración
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 3. TRABAJADORES
-- ==========================================================
CREATE TABLE IF NOT EXISTS trabajadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula_dni VARCHAR(15) UNIQUE NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    rol_cargo VARCHAR(50) NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    direccion VARCHAR(255) DEFAULT NULL,
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    fecha_registro TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 4. PLANIFICACIONES (Evolucionada: Sin monto base estático)
-- ==========================================================
CREATE TABLE IF NOT EXISTS planificaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_viaje VARCHAR(20) UNIQUE NOT NULL,
    embarcacion_nombre VARCHAR(100) NOT NULL,
    destino_ruta VARCHAR(150) DEFAULT NULL,
    fecha_salida_programada DATE NOT NULL,
    meta_peso_kg DECIMAL(10,2) NOT NULL,
    estado ENUM('Pendiente', 'En Curso', 'Finalizado', 'Cancelada') DEFAULT 'Pendiente',
    activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 5. FAENA ASISTENCIA (Con índice único para evitar duplicados)
-- ==========================================================
CREATE TABLE IF NOT EXISTS faena_asistencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    planificacion_id INT NOT NULL,
    trabajador_id INT NOT NULL,
    fecha_asistencia TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    estado_asistencia ENUM('Presente', 'Ausente', 'Justificado') NOT NULL DEFAULT 'Presente',
    FOREIGN KEY (planificacion_id) REFERENCES planificaciones(id) ON DELETE CASCADE,
    FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE,
    UNIQUE KEY idx_viaje_trabajador (planificacion_id, trabajador_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- 6. LIQUIDACIÓN CAPTURA
-- ==========================================================
CREATE TABLE IF NOT EXISTS liquidacion_captura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    planificacion_id INT UNIQUE NOT NULL,
    peso_total_pescado DECIMAL(10,2) NOT NULL,
    monto_final_calculado DECIMAL(12,2) DEFAULT NULL,
    registrado_por_capitan VARCHAR(150) DEFAULT NULL,
    fecha_cierre TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    FOREIGN KEY (planificacion_id) REFERENCES planificaciones(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- DATOS INICIALES (SEMILLAS)
-- ==========================================================

-- Usuario Admin por defecto
INSERT IGNORE INTO usuarios (username, password, nombre_usuario, rol) 
VALUES ('admin', 'admin123', 'Miguel Administrador', 'Administrador');

-- Precio del kilo inicial (Verónica: $2.50 por kg)
INSERT IGNORE INTO configuracion_pago (id, pago_kilo_base) 
VALUES (1, 2.50);