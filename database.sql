
CREATE DATABASE fishgold_db;
USE fishgold_db;

-- ==========================================================
-- MÓDULO 1: TRABAJADORES (CRUD completo)
-- ==========================================================
CREATE TABLE trabajadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula_dni VARCHAR(15) UNIQUE NOT NULL, -- Identificador único para búsquedas
    nombre_completo VARCHAR(150) NOT NULL,
    rol_cargo VARCHAR(50) NOT NULL,        -- Capitán, Motorista, Pescador, etc.
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- MÓDULO 2: PLANIFICACIÓN (CRUD completo)
-- ==========================================================
CREATE TABLE planificaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_viaje VARCHAR(20) UNIQUE NOT NULL, -- Ej: VIAJE-2026-001
    embarcacion_nombre VARCHAR(100) NOT NULL,
    destino_ruta VARCHAR(150),
    fecha_salida_programada DATE NOT NULL,
    monto_base_tripulacion DECIMAL(12,2) NOT NULL, -- Pago total ofrecido
    meta_peso_kg DECIMAL(10,2) NOT NULL,          -- Peso objetivo para el bono
    estado ENUM('Pendiente', 'En Curso', 'Finalizado') DEFAULT 'Pendiente'
);

-- ==========================================================
-- MÓDULO 3: FAENA (Solo Registro, Reporte y Búsqueda)
-- ==========================================================
CREATE TABLE faena_asistencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    planificacion_id INT NOT NULL,
    trabajador_id INT NOT NULL,
    fecha_asistencia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Nota: No incluimos DELETE ni UPDATE en la lógica de la App para esta tabla
    FOREIGN KEY (planificacion_id) REFERENCES planificaciones(id),
    FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id)
);

-- ==========================================================
-- MÓDULO 4: CAPTURA Y LIQUIDACIÓN (Registro de Peso y Pago)
-- ==========================================================
CREATE TABLE liquidacion_captura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    planificacion_id INT UNIQUE NOT NULL,
    peso_total_pescado DECIMAL(10,2) NOT NULL,
    monto_final_calculado DECIMAL(12,2), -- Aquí se guarda el base o el base + 15%
    registrado_por_capitan VARCHAR(150),
    fecha_cierre TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    FOREIGN KEY (planificacion_id) REFERENCES planificaciones(id)
);
-- Insertar un usuario inicial para que puedas entrar
INSERT INTO usuarios (username, password, nombre_usuario, rol) 
VALUES ('admin', 'admin123', 'Miguel Administrador', 'Administrador');