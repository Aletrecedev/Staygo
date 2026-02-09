CREATE DATABASE IF NOT EXISTS staygo_db;
USE staygo_db;

-- Tabla 1: Usuario
CREATE TABLE usuarios (
                          id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellidos VARCHAR(100) NOT NULL,
                          telefono VARCHAR(20),
                          email VARCHAR(100) UNIQUE NOT NULL,
                          contrasena VARCHAR(255) NOT NULL,
                          nombre_usuario VARCHAR(50) UNIQUE,
                          rol ENUM('ADMIN', 'PROPIETARIO', 'CLIENTE') NOT NULL -- Para diferenciar tipos de usuario
);

-- Tabla 2: Propietario
CREATE TABLE propietarios (
                              id_propietario INT AUTO_INCREMENT PRIMARY KEY,
                              id_usuario INT NOT NULL,
                              FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla 3: Cliente
CREATE TABLE clientes (
                          id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                          id_usuario INT NOT NULL,
                          FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla 4: Apartamento
CREATE TABLE apartamentos (
                              id_apartamento INT AUTO_INCREMENT PRIMARY KEY,
                              nombre VARCHAR(150) NOT NULL,
                              direccion VARCHAR(255) NOT NULL,
                              precio_noche DECIMAL(10, 2) NOT NULL,
                              capacidad INT NOT NULL,
                              descripcion TEXT,
                              id_propietario INT NOT NULL,
                              FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
);

-- Tabla 5: Reserva
CREATE TABLE reservas (
                          id_reserva INT AUTO_INCREMENT PRIMARY KEY,
                          fecha_inicio DATE NOT NULL,
                          fecha_fin DATE NOT NULL,
                          importe_total DECIMAL(10, 2) NOT NULL,
                          estado VARCHAR(50) DEFAULT 'PENDIENTE', -- [cite: 87]
                          valoracion_huesped VARCHAR(255), -- [cite: 89]
                          valoracion_propietario VARCHAR(255), -- [cite: 90]
                          id_apartamento INT NOT NULL,
                          id_cliente INT NOT NULL,
                          FOREIGN KEY (id_apartamento) REFERENCES apartamentos(id_apartamento),
                          FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

-- Tabla 6: Pago
CREATE TABLE pagos (
                       id_pago INT AUTO_INCREMENT PRIMARY KEY,
                       fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP, -- [cite: 133]
                       metodo_pago VARCHAR(50), -- [cite: 134]
                       id_reserva INT NOT NULL,
                       FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
);