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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

/** Pruebas unitarias de ProyectosController (repositorio simulado con Mockito). */
class ProyectosControllerTest {

	private ProtectosRepository repo;
	private ProyectosController controller;

	@BeforeEach
	void preparar() {
		repo = mock(ProtectosRepository.class);
		controller = new ProyectosController();
		controller.proyectostable = repo;
	}

	private Proyectos proyecto() {
		Proyectos p = new Proyectos();
		p.setId_proyecto(10);
		p.setNombre("Proyecto de prueba");
		p.setDescripcion("Creado desde una prueba unitaria");
		p.setEstado_proyecto("Activo");
		return p;
	}

	@Test
	@DisplayName("UT-10 getProyecto: id existente devuelve el proyecto")
	void ut10_buscarExistente() {
		when(repo.findById(10)).thenReturn(Optional.of(proyecto()));

		Proyectos resultado = controller.getProyecto(10);

		assertEquals("Proyecto de prueba", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-11 getProyecto: id inexistente devuelve un proyecto vacio")
	void ut11_buscarInexistente() {
		when(repo.findById(999)).thenReturn(Optional.empty());

		Proyectos resultado = controller.getProyecto(999);

		assertNull(resultado.getId_proyecto());
	}

	@Test
	@DisplayName("UT-12 addProyecto: guarda y devuelve 1")
	void ut12_agregar() {
		Proyectos nuevo = proyecto();

		assertEquals(1, controller.addProyecto(nuevo));
		verify(repo).save(nuevo);
	}

	@Test
	@DisplayName("UT-13 updateProyecto: guarda los cambios y devuelve 1")
	void ut13_actualizar() {
		Proyectos editado = proyecto();
		editado.setEstado_proyecto("Terminado");

		assertEquals(1, controller.updateProyecto(editado));
		verify(repo).save(editado);
	}

	@Test
	@DisplayName("UT-14 deleteProyecto: elimina y devuelve 1")
	void ut14_eliminar() {
		Proyectos aBorrar = proyecto();

		assertEquals(1, controller.deleteProyecto(aBorrar));
		verify(repo).delete(aBorrar);
	}

	@Test
	@DisplayName("UT-15 getProyectos: devuelve la lista completa")
	void ut15_listar() {
		when(repo.findAll()).thenReturn(Arrays.asList(proyecto(), proyecto(), proyecto()));

		List<Proyectos> lista = controller.getProyectos();

		assertEquals(3, lista.size());
	}

	// ---- PRACTICA 3: casos agregados para cubrir todas las ramas ----

	@Test
	@DisplayName("UT-30 addProyecto: sin cuerpo (null) no guarda y devuelve 0")
	void ut30_agregarNull() {
		assertEquals(0, controller.addProyecto(null));
		verify(repo, never()).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-31 updateProyecto: sin cuerpo (null) no guarda y devuelve 0")
	void ut31_actualizarNull() {
		assertEquals(0, controller.updateProyecto(null));
		verify(repo, never()).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-32 deleteProyecto: sin cuerpo (null) no elimina y devuelve 0")
	void ut32_eliminarNull() {
		assertEquals(0, controller.deleteProyecto(null));
		verify(repo, never()).delete(any(Proyectos.class));
	}
}
