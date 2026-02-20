INSERT INTO usuarios (nombre, apellidos, telefono, email, contrasena, nombre_usuario, rol)
VALUES ('Carlos Alejandro', 'Duré', '1234567890', 'hello@sololog.com', '1234', 'cdure', 'PROPIETARIO');

INSERT INTO propietarios (id_usuario) VALUES (1);

INSERT INTO apartamentos (nombre, direccion, precio_noche, capacidad, descripcion, id_propietario)
VALUES ('Loft de Lujo con Vistas', 'Barcelona, España', 200.00, 6, 'Precioso loft con vistas al mar', 1);

INSERT INTO apartamentos (nombre, direccion, precio_noche, capacidad, descripcion, id_propietario)
VALUES ('Apartamento', 'Madrid, España', 120.00, 2, 'Acogedor apartamento en el centro', 1);