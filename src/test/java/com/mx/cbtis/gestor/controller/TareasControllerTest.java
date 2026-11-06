package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

/** Pruebas unitarias de TareasController (repositorio simulado con Mockito). */
class TareasControllerTest {

	private TareasRepository repo;
	private TareasController controller;

	@BeforeEach
	void preparar() {
		repo = mock(TareasRepository.class);
		controller = new TareasController();
		controller.tareastable = repo;
	}

	private Tareas tarea() {
		Tareas t = new Tareas();
		t.setId_tarea(5);
		t.setNombre("Tarea de prueba");
		t.setEstado("Pendiente");
		t.setId_proyecto(10);
		return t;
	}

	@Test
	@DisplayName("UT-16 getTarea(POST): devuelve las tareas del proyecto indicado")
	void ut16_tareasPorProyecto() {
		when(repo.findByIdProyecto(10)).thenReturn(Arrays.asList(tarea(), tarea()));

		List<Tareas> lista = controller.getTarea(tarea());

		assertEquals(2, lista.size());
		verify(repo).findByIdProyecto(10);
	}

	@Test
	@DisplayName("UT-17 getTarea(GET): id existente devuelve la tarea")
	void ut17_buscarExistente() {
		when(repo.findById(5)).thenReturn(Optional.of(tarea()));

		Tareas resultado = controller.getTarea(5);

		assertEquals("Tarea de prueba", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-18 addTareas: guarda y devuelve 1")
	void ut18_agregar() {
		Tareas nueva = tarea();

		assertEquals(1, controller.addTareas(nueva));
		verify(repo).save(nueva);
	}

	@Test
	@DisplayName("UT-19 deleteTareas: elimina y devuelve 1")
	void ut19_eliminar() {
		Tareas aBorrar = tarea();

		assertEquals(1, controller.deleteTareas(aBorrar));
		verify(repo).delete(aBorrar);
	}

	// ---- PRACTICA 3: casos agregados para cubrir todos los metodos y ramas ----

	@Test
	@DisplayName("UT-33 getTarea(GET): id inexistente devuelve una tarea vacia")
	void ut33_buscarInexistente() {
		when(repo.findById(999)).thenReturn(Optional.empty());

		Tareas resultado = controller.getTarea(999);

		assertNull(resultado.getId_tarea());
		assertNull(resultado.getNombre());
	}

	@Test
	@DisplayName("UT-34 addTareas: sin cuerpo (null) no guarda y devuelve 0")
	void ut34_agregarNull() {
		assertEquals(0, controller.addTareas(null));
		verify(repo, never()).save(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-35 updateTareas: guarda los cambios y devuelve 1")
	void ut35_actualizar() {
		Tareas editada = tarea();
		editada.setEstado("Terminado");

		assertEquals(1, controller.updateTareas(editada));
		verify(repo).save(editada);
	}

	@Test
	@DisplayName("UT-36 updateTareas: sin cuerpo (null) no guarda y devuelve 0")
	void ut36_actualizarNull() {
		assertEquals(0, controller.updateTareas(null));
		verify(repo, never()).save(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-37 deleteTareas: sin cuerpo (null) no elimina y devuelve 0")
	void ut37_eliminarNull() {
		assertEquals(0, controller.deleteTareas(null));
		verify(repo, never()).delete(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-38 getTareas: devuelve la lista completa")
	void ut38_listar() {
		when(repo.findAll()).thenReturn(Arrays.asList(tarea(), tarea(), tarea()));

		List<Tareas> lista = controller.getTareas();

		assertEquals(3, lista.size());
	}
}
