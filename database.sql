CREATE DATABASE IF NOT EXISTS fishgold_db;
USE fishgold_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT IGNORE INTO usuarios (username, password) VALUES ('admin', 'admin');

CREATE TABLE IF NOT EXISTS trabajadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    tiene_licencia BOOLEAN NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    contacto_emergencia_nombre VARCHAR(150) NOT NULL,
    contacto_emergencia_relacion VARCHAR(50) NOT NULL,
    contacto_emergencia_telefono VARCHAR(20) NOT NULL,
    puestos_anteriores VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'Activo'
);

CREATE TABLE IF NOT EXISTS embarcaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    propietario VARCHAR(150) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL,
    anio_compra INT NOT NULL,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    estado VARCHAR(20) DEFAULT 'Activa'
);

CREATE TABLE IF NOT EXISTS faenas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    fecha DATE NOT NULL,
    hora_embarco TIME NOT NULL,
    embarcacion_id INT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente',
    FOREIGN KEY (embarcacion_id) REFERENCES embarcaciones(id)
);

CREATE TABLE IF NOT EXISTS tripulacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faena_id INT NOT NULL,
    trabajador_id INT NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    asistencia_embarco BOOLEAN DEFAULT NULL,
    asistencia_desembarco BOOLEAN DEFAULT NULL,
    FOREIGN KEY (faena_id) REFERENCES faenas(id) ON DELETE CASCADE,
    FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE
);
