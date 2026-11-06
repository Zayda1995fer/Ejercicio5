package com.mx.cbtis.gestor.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.modelo.Usuario;

/**
 * Verifica las consultas nativas (@Query) de los repositorios contra una BD H2 real en memoria.
 */
@DataJpaTest
class RepositoryConsultasTest {

	@Autowired
	UsuarioRepository usuarios;

	@Autowired
	TareasRepository tareas;

	@Autowired
	ComentariosRepository comentarios;

	@Test
	void findByCorreoPassDevuelveUsuarioConCredencialesCorrectas() {
		usuarios.save(new Usuario(null, "Ana", "ana@cbtis75.edu.mx", "0722", "Estudiante", "#70FAB7"));

		Optional<Usuario> encontrado = usuarios.findByCorreoPass("ana@cbtis75.edu.mx", "0722");

		assertTrue(encontrado.isPresent());
		assertEquals("Ana", encontrado.get().getNombre());
	}

	@Test
	void findByCorreoPassVacioConContrasenaIncorrecta() {
		usuarios.save(new Usuario(null, "Ana", "ana@cbtis75.edu.mx", "0722", "Estudiante", "#70FAB7"));

		assertFalse(usuarios.findByCorreoPass("ana@cbtis75.edu.mx", "mala").isPresent());
		assertFalse(usuarios.findByCorreoPass("otro@cbtis75.edu.mx", "0722").isPresent());
	}

	@Test
	void findByIdProyectoSoloDevuelveTareasDeEseProyecto() {
		tareas.save(new Tareas(null, "T1", "d", "2024-06-13", "danger", "pendiente", 197));
		tareas.save(new Tareas(null, "T2", "d", "2024-06-14", "info", "terminado", 197));
		tareas.save(new Tareas(null, "T3", "d", "2024-06-15", "info", "pendiente", 300));

		List<Tareas> resultado = tareas.findByIdProyecto(197);

		assertEquals(2, resultado.size());
		assertTrue(resultado.stream().allMatch(t -> t.getId_proyecto() == 197));
		assertTrue(tareas.findByIdProyecto(999).isEmpty());
	}

	@Test
	void findByTareaSoloDevuelveComentariosDeEsaTarea() {
		comentarios.save(new Comentarios(null, "Perla", 199, "Subirlo YA!!!", "2024-06-26 13:48:00"));
		comentarios.save(new Comentarios(null, "Liliana", 199, "hola", "2024-06-26 13:49:00"));
		comentarios.save(new Comentarios(null, "Ana", 500, "otro", "2024-06-26 13:50:00"));

		List<Comentarios> resultado = comentarios.findByTarea(199);

		assertEquals(2, resultado.size());
		assertTrue(resultado.stream().allMatch(c -> c.getId_tarea() == 199));
		assertTrue(comentarios.findByTarea(999).isEmpty());
	}
}
