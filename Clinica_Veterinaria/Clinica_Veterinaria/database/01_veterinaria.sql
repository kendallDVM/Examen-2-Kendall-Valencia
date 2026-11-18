
DROP DATABASE IF EXISTS veterinaria;
CREATE DATABASE veterinaria;
USE veterinaria;

-- ---------- TABLAS ----------

CREATE TABLE cliente (
    cedula   VARCHAR(20) PRIMARY KEY,
    nombre   VARCHAR(80) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    edad     INT NOT NULL,
    CHECK (cedula <> ''),
    CHECK (nombre <> ''),
    CHECK (telefono <> ''),
    CHECK (edad BETWEEN 18 AND 60)
);

CREATE TABLE mascota (
    codigo         VARCHAR(15) PRIMARY KEY,
    nombre         VARCHAR(80) NOT NULL,
    especie        ENUM('PERRO','GATO','OTRO') NOT NULL,
    edad           INT NOT NULL,
    cedula_cliente VARCHAR(20) NOT NULL,
    CHECK (codigo <> ''),
    CHECK (nombre <> ''),
    CHECK (edad BETWEEN 0 AND 40),
    FOREIGN KEY (cedula_cliente) REFERENCES cliente(cedula) ON DELETE RESTRICT
);

-- ---------- USUARIOS ----------

DROP USER IF EXISTS 'vet_definer'@'localhost';
DROP USER IF EXISTS 'vet_app'@'localhost';
CREATE USER 'vet_definer'@'localhost' IDENTIFIED BY 'CAMBIAR_PASSWORD_DEFINER';
CREATE USER 'vet_app'@'localhost' IDENTIFIED BY 'CAMBIAR_PASSWORD_APP';

GRANT SELECT, INSERT ON veterinaria.cliente TO 'vet_definer'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON veterinaria.mascota TO 'vet_definer'@'localhost';
GRANT EXECUTE ON veterinaria.* TO 'vet_definer'@'localhost'; -- necesario para que sus procedimientos se puedan ejecutar

-- ---------- PROCEDIMIENTOS ----------

DELIMITER $$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_listar_clientes()
SQL SECURITY DEFINER
BEGIN
    SELECT cedula, nombre, telefono, edad FROM cliente;
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_insertar_cliente(p_cedula VARCHAR(20), p_nombre VARCHAR(80), p_telefono VARCHAR(20), p_edad INT)
SQL SECURITY DEFINER
BEGIN
    INSERT INTO cliente VALUES (p_cedula, p_nombre, p_telefono, p_edad);
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_listar_mascotas()
SQL SECURITY DEFINER
BEGIN
    SELECT codigo, nombre, especie, edad, cedula_cliente FROM mascota;
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_insertar_mascota(p_codigo VARCHAR(15), p_nombre VARCHAR(80), p_especie VARCHAR(10), p_edad INT, p_cedula VARCHAR(20))
SQL SECURITY DEFINER
BEGIN
    INSERT INTO mascota VALUES (p_codigo, p_nombre, p_especie, p_edad, p_cedula);
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_actualizar_mascota(p_codigo VARCHAR(15), p_nombre VARCHAR(80), p_especie VARCHAR(10), p_edad INT, p_cedula VARCHAR(20))
SQL SECURITY DEFINER
BEGIN
    UPDATE mascota SET nombre = p_nombre, especie = p_especie, edad = p_edad, cedula_cliente = p_cedula
    WHERE codigo = p_codigo;
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe la mascota.';
    END IF;
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_eliminar_mascota(p_codigo VARCHAR(15))
SQL SECURITY DEFINER
BEGIN
    DELETE FROM mascota WHERE codigo = p_codigo;
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe la mascota.';
    END IF;
END$$

CREATE DEFINER='vet_definer'@'localhost' PROCEDURE sp_listar_mascotas_con_dueno()
SQL SECURITY DEFINER
BEGIN
    SELECT m.codigo, m.nombre, m.especie, m.edad, m.cedula_cliente, c.nombre AS nombre_cliente
    FROM mascota m INNER JOIN cliente c ON m.cedula_cliente = c.cedula;
END$$

DELIMITER ;

-- ---------- PERMISOS DE vet_app ----------

GRANT EXECUTE ON PROCEDURE veterinaria.sp_listar_clientes TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_insertar_cliente TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_listar_mascotas TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_insertar_mascota TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_actualizar_mascota TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_eliminar_mascota TO 'vet_app'@'localhost';
GRANT EXECUTE ON PROCEDURE veterinaria.sp_listar_mascotas_con_dueno TO 'vet_app'@'localhost';

-- ---------- DATOS DE EJEMPLO ----------

CALL sp_insertar_cliente('101110111', 'Ana Mora', '88881111', 34);
CALL sp_insertar_cliente('202220222', 'Luis Solis', '87772222', 45);
CALL sp_insertar_mascota('M01', 'Luna', 'GATO', 3, '101110111');
CALL sp_insertar_mascota('M02', 'Rocky', 'PERRO', 5, '202220222');
CALL sp_insertar_mascota('M03', 'Nina', 'PERRO', 1, '101110111');

