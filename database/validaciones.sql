-- =====================================================================
-- PRACTICA 3 - Validaciones de calidad de la base de datos dbgestor
-- Se ejecuta en el pipeline de BD despues de cargar database/dbgestor.sql
-- en una base temporal. Si una regla CRITICA falla, SIGNAL detiene el
-- pipeline (codigo de salida distinto de 0). Las ADVERTENCIAS solo se reportan.
-- =====================================================================

DROP PROCEDURE IF EXISTS validar_calidad;

DELIMITER $$
CREATE PROCEDURE validar_calidad()
BEGIN
	DECLARE n INT;

	-- CRITICA 1: existen las 5 tablas que usa el API (entidades JPA)
	SELECT COUNT(*) INTO n FROM information_schema.tables
	 WHERE table_schema = DATABASE()
	   AND table_name IN ('usuario', 'proyectos', 'tareas', 'comentarios', 'asignaciones');
	IF n <> 5 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: faltan tablas requeridas por el API';
	END IF;

	-- CRITICA 2: toda tabla tiene llave primaria
	SELECT COUNT(*) INTO n FROM information_schema.tables t
	 WHERE t.table_schema = DATABASE() AND t.table_type = 'BASE TABLE'
	   AND NOT EXISTS (SELECT 1 FROM information_schema.table_constraints c
	                    WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
	                      AND c.constraint_type = 'PRIMARY KEY');
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: hay tablas sin PRIMARY KEY';
	END IF;

	-- CRITICA 3: integridad referencial de los datos (no hay FOREIGN KEY declaradas)
	SELECT COUNT(*) INTO n FROM tareas t
	 WHERE t.id_proyecto IS NOT NULL
	   AND NOT EXISTS (SELECT 1 FROM proyectos p WHERE p.id_proyecto = t.id_proyecto);
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: tareas con id_proyecto inexistente';
	END IF;

	SELECT COUNT(*) INTO n FROM comentarios c
	 WHERE c.id_tarea IS NOT NULL
	   AND NOT EXISTS (SELECT 1 FROM tareas t WHERE t.id_tarea = c.id_tarea);
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: comentarios con id_tarea inexistente';
	END IF;

	SELECT COUNT(*) INTO n FROM asignaciones a
	 WHERE (a.id_tarea IS NOT NULL AND NOT EXISTS (SELECT 1 FROM tareas t WHERE t.id_tarea = a.id_tarea))
	    OR (a.id_usuario IS NOT NULL AND NOT EXISTS (SELECT 1 FROM usuario u WHERE u.id_usuario = a.id_usuario));
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: asignaciones con usuario o tarea inexistente';
	END IF;

	-- CRITICA 4: el login (findByCorreoPass) asume un correo por usuario
	SELECT COUNT(*) INTO n FROM (SELECT correo_electronico FROM usuario
	                              GROUP BY correo_electronico HAVING COUNT(*) > 1) d;
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: correos de usuario duplicados';
	END IF;

	-- CRITICA 5: ningun usuario sin correo o sin contrasena (no podria iniciar sesion)
	SELECT COUNT(*) INTO n FROM usuario
	 WHERE correo_electronico IS NULL OR correo_electronico = ''
	    OR contrasena IS NULL OR contrasena = '';
	IF n > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CRITICA: usuarios sin correo o sin contrasena';
	END IF;
END$$
DELIMITER ;

CALL validar_calidad();
SELECT 'OK: todas las validaciones CRITICAS pasaron' AS resultado;

-- ---------------------------------------------------------------------
-- ADVERTENCIAS (no detienen el pipeline; quedan en el log como evidencia)
-- ---------------------------------------------------------------------
SELECT 'ADVERTENCIA: contrasenas en texto plano (no tienen formato hash bcrypt de 60 caracteres)' AS hallazgo,
       COUNT(*) AS usuarios_afectados
  FROM usuario WHERE CHAR_LENGTH(contrasena) < 60;

SELECT 'ADVERTENCIA: tabla con charset latin1 (se recomienda utf8mb4)' AS hallazgo, table_name AS tabla
  FROM information_schema.tables
 WHERE table_schema = DATABASE() AND table_collation LIKE 'latin1%';

SELECT 'ADVERTENCIA: indice con nombre _FK sin FOREIGN KEY declarada' AS hallazgo,
       s.table_name AS tabla, s.index_name AS indice
  FROM information_schema.statistics s
 WHERE s.table_schema = DATABASE() AND s.index_name LIKE '%\_FK'
   AND NOT EXISTS (SELECT 1 FROM information_schema.referential_constraints r
                    WHERE r.constraint_schema = s.table_schema AND r.constraint_name = s.index_name);

-- Resumen de datos cargados
SELECT 'usuario' AS tabla, COUNT(*) AS registros FROM usuario
UNION ALL SELECT 'proyectos', COUNT(*) FROM proyectos
UNION ALL SELECT 'tareas', COUNT(*) FROM tareas
UNION ALL SELECT 'comentarios', COUNT(*) FROM comentarios
UNION ALL SELECT 'asignaciones', COUNT(*) FROM asignaciones;

DROP PROCEDURE IF EXISTS validar_calidad;
