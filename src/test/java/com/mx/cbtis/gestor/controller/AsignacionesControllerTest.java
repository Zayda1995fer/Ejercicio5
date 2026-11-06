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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

/** Pruebas unitarias de AsignacionesController (repositorio simulado con Mockito). */
class AsignacionesControllerTest {

	private AsignacionesRepository repo;
	private AsignacionesController controller;

	@BeforeEach
	void preparar() {
		repo = mock(AsignacionesRepository.class);
		controller = new AsignacionesController();
		controller.asignacionestable = repo;
	}

	private Asignaciones asignacion() {
		Asignaciones a = new Asignaciones();
		a.setId_asignacion(3);
		a.setId_usuario(1);
		a.setId_tarea(5);
		return a;
	}

	@Test
	@DisplayName("UT-22 getAsignacion: id existente devuelve la asignacion")
	void ut22_buscarExistente() {
		when(repo.findById(3)).thenReturn(Optional.of(asignacion()));

		Asignaciones resultado = controller.getAsignacion(3);

		assertEquals(Integer.valueOf(5), resultado.getId_tarea());
	}

	@Test
	@DisplayName("UT-23 addAsignacion: guarda y devuelve 1")
	void ut23_agregar() {
		Asignaciones nueva = asignacion();

		assertEquals(1, controller.addAsignacion(nueva));
		verify(repo).save(nueva);
	}

	@Test
	@DisplayName("UT-24 getAsignaciones: devuelve la lista completa")
	void ut24_listar() {
		when(repo.findAll()).thenReturn(Arrays.asList(asignacion(), asignacion()));

		List<Asignaciones> lista = controller.getAsignaciones();

		assertEquals(2, lista.size());
	}

	// ---- PRACTICA 3: casos agregados para cubrir todos los metodos y ramas ----

	@Test
	@DisplayName("UT-47 getAsignacion: id inexistente devuelve una asignacion vacia")
	void ut47_buscarInexistente() {
		when(repo.findById(999)).thenReturn(Optional.empty());

		assertNull(controller.getAsignacion(999).getId_asignacion());
	}

	@Test
	@DisplayName("UT-48 addAsignacion: sin cuerpo (null) no guarda y devuelve 0")
	void ut48_agregarNull() {
		assertEquals(0, controller.addAsignacion(null));
		verify(repo, never()).save(any(Asignaciones.class));
	}

	@Test
	@DisplayName("UT-49 updateAsignacion: guarda los cambios y devuelve 1")
	void ut49_actualizar() {
		Asignaciones editada = asignacion();
		editada.setId_usuario(2);

		assertEquals(1, controller.updateAsignacion(editada));
		verify(repo).save(editada);
	}

	@Test
	@DisplayName("UT-50 updateAsignacion: sin cuerpo (null) no guarda y devuelve 0")
	void ut50_actualizarNull() {
		assertEquals(0, controller.updateAsignacion(null));
		verify(repo, never()).save(any(Asignaciones.class));
	}

	@Test
	@DisplayName("UT-51 deleteAsignacion: elimina y devuelve 1")
	void ut51_eliminar() {
		Asignaciones aBorrar = asignacion();

		assertEquals(1, controller.deleteAsignacion(aBorrar));
		verify(repo).delete(aBorrar);
	}

	@Test
	@DisplayName("UT-52 deleteAsignacion: sin cuerpo (null) no elimina y devuelve 0")
	void ut52_eliminarNull() {
		assertEquals(0, controller.deleteAsignacion(null));
		verify(repo, never()).delete(any(Asignaciones.class));
	}
}
