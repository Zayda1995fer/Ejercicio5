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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

/** Pruebas unitarias de ComentariosController (repositorio simulado con Mockito). */
class ComentariosControllerTest {

	private ComentariosRepository repo;
	private ComentariosController controller;

	@BeforeEach
	void preparar() {
		repo = mock(ComentariosRepository.class);
		controller = new ComentariosController();
		controller.comentariostable = repo;
	}

	private Comentarios comentario() {
		Comentarios c = new Comentarios();
		c.setId_comentario(7);
		c.setId_tarea(5);
		c.setTexto_comentario("Comentario de prueba");
		return c;
	}

	@Test
	@DisplayName("UT-20 getComentario(POST): devuelve los comentarios de la tarea indicada")
	void ut20_comentariosPorTarea() {
		when(repo.findByTarea(5)).thenReturn(Arrays.asList(comentario()));

		List<Comentarios> lista = controller.getComentario(comentario());

		assertEquals(1, lista.size());
		assertEquals("Comentario de prueba", lista.get(0).getTexto_comentario());
	}

	@Test
	@DisplayName("UT-21 addComentario: guarda y devuelve 1")
	void ut21_agregar() {
		Comentarios nuevo = comentario();

		assertEquals(1, controller.addComentario(nuevo));
		verify(repo).save(nuevo);
	}

	// ---- PRACTICA 3: casos agregados para cubrir todos los metodos y ramas ----

	@Test
	@DisplayName("UT-39 getComentario(GET): id existente devuelve el comentario")
	void ut39_buscarExistente() {
		when(repo.findById(7)).thenReturn(Optional.of(comentario()));

		Comentarios resultado = controller.getComentario(7);

		assertEquals("Comentario de prueba", resultado.getTexto_comentario());
	}

	@Test
	@DisplayName("UT-40 getComentario(GET): id inexistente devuelve un comentario vacio")
	void ut40_buscarInexistente() {
		when(repo.findById(999)).thenReturn(Optional.empty());

		assertNull(controller.getComentario(999).getId_comentario());
	}

	@Test
	@DisplayName("UT-41 addComentario: sin cuerpo (null) no guarda y devuelve 0")
	void ut41_agregarNull() {
		assertEquals(0, controller.addComentario(null));
		verify(repo, never()).save(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-42 updateComentario: guarda los cambios y devuelve 1")
	void ut42_actualizar() {
		Comentarios editado = comentario();
		editado.setTexto_comentario("Comentario editado");

		assertEquals(1, controller.updateComentario(editado));
		verify(repo).save(editado);
	}

	@Test
	@DisplayName("UT-43 updateComentario: sin cuerpo (null) no guarda y devuelve 0")
	void ut43_actualizarNull() {
		assertEquals(0, controller.updateComentario(null));
		verify(repo, never()).save(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-44 deleteComentario: elimina y devuelve 1")
	void ut44_eliminar() {
		Comentarios aBorrar = comentario();

		assertEquals(1, controller.deleteComentario(aBorrar));
		verify(repo).delete(aBorrar);
	}

	@Test
	@DisplayName("UT-45 deleteComentario: sin cuerpo (null) no elimina y devuelve 0")
	void ut45_eliminarNull() {
		assertEquals(0, controller.deleteComentario(null));
		verify(repo, never()).delete(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-46 getComentarios: devuelve la lista completa")
	void ut46_listar() {
		when(repo.findAll()).thenReturn(Arrays.asList(comentario(), comentario()));

		List<Comentarios> lista = controller.getComentarios();

		assertEquals(2, lista.size());
	}
}
