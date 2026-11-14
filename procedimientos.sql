-- Procedimiento almacenado del módulo Habitaciones (Persona 3).
-- Ejecutar en MySQL Workbench sobre la base de datos hotel_management_db.

USE hotel_management_db;

DROP PROCEDURE IF EXISTS BuscarHabitacionesPorCategoria;

DELIMITER //
CREATE PROCEDURE BuscarHabitacionesPorCategoria(IN p_categoria VARCHAR(20))
BEGIN
    SELECT numero, capacidad, precio_noche, categoria, detalle_extra, id_hotel
    FROM habitaciones
    WHERE categoria = p_categoria
    ORDER BY precio_noche;
END //
DELIMITER ;

-- Datos de prueba opcionales (descomentar solo si la BBDD está vacía).
-- La tabla habitaciones necesita un hotel existente por la clave foránea.
-- INSERT INTO hoteles (nombre, ciudad) VALUES ('Hotel Prueba', 'Barcelona');
-- INSERT INTO habitaciones (numero, capacidad, precio_noche, categoria, detalle_extra, id_hotel) VALUES
--   ('101', 2, 80.00, 'Estándar', 'Doble', 1),
--   ('201', 3, 120.00, 'Superior', 'true', 1),
--   ('301', 4, 250.00, 'Suite', 'true', 1);

USE hotel_management_db;

DROP PROCEDURE IF EXISTS BuscarClientePorApellido;

DELIMITER //
CREATE PROCEDURE BuscarClientePorApellido(IN p_apellido VARCHAR(100))
BEGIN
    SELECT id_cliente, nombre, apellido, email
    FROM clientes
    WHERE apellido LIKE CONCAT('%', p_apellido, '%')
ORDER BY apellido, nombre;
END //
DELIMITER ;