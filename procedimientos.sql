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

-- =========================================================
-- Procedimientos almacenados del módulo Hoteles (Persona 5)
-- =========================================================
USE hotel_management_db;

-- Alta de hotel: inserta y devuelve el ID autonumérico generado como fila de resultado
-- (se evita un parámetro OUT porque el driver JDBC necesita permisos extra de metadatos para leerlo).
DROP PROCEDURE IF EXISTS InsertarHotel;

DELIMITER //
CREATE PROCEDURE InsertarHotel(IN p_nombre VARCHAR(100), IN p_ciudad VARCHAR(100))
BEGIN
    INSERT INTO hoteles (nombre, ciudad) VALUES (p_nombre, p_ciudad);
    SELECT LAST_INSERT_ID() AS id_hotel;
END //
DELIMITER ;

-- Búsqueda de hoteles por ciudad (coincidencia parcial).
DROP PROCEDURE IF EXISTS BuscarHotelesPorCiudad;

DELIMITER //
CREATE PROCEDURE BuscarHotelesPorCiudad(IN p_ciudad VARCHAR(100))
BEGIN
    SELECT id_hotel, nombre, ciudad
    FROM hoteles
    WHERE ciudad LIKE CONCAT('%', p_ciudad, '%')
    ORDER BY ciudad, nombre;
END //
DELIMITER ;
