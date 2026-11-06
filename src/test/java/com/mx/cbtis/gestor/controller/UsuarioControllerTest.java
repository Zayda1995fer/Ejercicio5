package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

/**
 * Pruebas unitarias de UsuarioController.
 * El repositorio se simula con Mockito, por lo que NO se necesita la base de datos encendida.
 */
class UsuarioControllerTest {

	private UsuarioRepository repo;
	private UsuarioController controller;

	@BeforeEach
	void preparar() {
		repo = mock(UsuarioRepository.class);
		controller = new UsuarioController();
		controller.usuariotable = repo;
	}

	private Usuario usuarioValido() {
		return new Usuario(1, "Ana Prueba", "ana@correo.com", "1234", "Estudiante", "foto.png");
	}

	@Test
	@DisplayName("UT-01 getUsuarioLogin: credenciales correctas devuelve el usuario")
	void ut01_loginCorrecto() {
		when(repo.findByCorreoPass("ana@correo.com", "1234")).thenReturn(Optional.of(usuarioValido()));

		Usuario resultado = controller.getUsuarioLogin(usuarioValido());

		assertEquals("Ana Prueba", resultado.getNombre());
		assertEquals(Integer.valueOf(1), resultado.getId_usuario());
	}

	@Test
	@DisplayName("UT-02 getUsuarioLogin: credenciales incorrectas devuelve nombre 000")
	void ut02_loginIncorrecto() {
		when(repo.findByCorreoPass("ana@correo.com", "mala")).thenReturn(Optional.empty());
		Usuario intento = new Usuario(null, null, "ana@correo.com", "mala", null, null);

		Usuario resultado = controller.getUsuarioLogin(intento);

		assertEquals("000", resultado.getNombre());
		assertNull(resultado.getId_usuario());
	}

	@Test
	@DisplayName("UT-03 getUsuario: id existente devuelve el usuario")
	void ut03_buscarExistente() {
		when(repo.findById(1)).thenReturn(Optional.of(usuarioValido()));

		Usuario resultado = controller.getUsuario(1);

		assertEquals("ana@correo.com", resultado.getCorreo_electronico());
	}

	@Test
	@DisplayName("UT-04 getUsuario: id inexistente devuelve un usuario vacio")
	void ut04_buscarInexistente() {
		when(repo.findById(999)).thenReturn(Optional.empty());

		Usuario resultado = controller.getUsuario(999);

		assertNull(resultado.getId_usuario());
		assertNull(resultado.getNombre());
	}

	@Test
	@DisplayName("UT-05 addUsuario: datos validos guarda y devuelve 1")
	void ut05_agregarValido() {
		Usuario nuevo = usuarioValido();

		int flag = controller.addUsuario(nuevo);

		assertEquals(1, flag);
		verify(repo).save(nuevo);
	}

	@Test
	@DisplayName("UT-06 addUsuario: sin correo ni contrasena responde 400 y no guarda")
	void ut06_agregarInvalido() {
		Usuario vacio = new Usuario();

		ResponseStatusException error = assertThrows(ResponseStatusException.class,
				() -> controller.addUsuario(vacio));

		// PRACTICA 3: en Spring 6+ getStatus() se reemplazo por getStatusCode()
		assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
		verify(repo, never()).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-07 updateUsuario: guarda los cambios y devuelve 1")
	void ut07_actualizar() {
		Usuario editado = usuarioValido();
		editado.setRol("Docente");

		int flag = controller.updateUsuario(editado);

		assertEquals(1, flag);
		verify(repo).save(editado);
	}

	@Test
	@DisplayName("UT-08 deleteUsuario: elimina y devuelve 1")
	void ut08_eliminar() {
		Usuario aBorrar = usuarioValido();

		int flag = controller.deleteUsuario(aBorrar);

		assertEquals(1, flag);
		verify(repo).delete(aBorrar);
	}

	@Test
	@DisplayName("UT-09 getUsuarios: devuelve la lista completa")
	void ut09_listar() {
		when(repo.findAll()).thenReturn(Arrays.asList(usuarioValido(), usuarioValido()));

		List<Usuario> lista = controller.getUsuarios();

		assertEquals(2, lista.size());
	}

	// ---- PRACTICA 3: casos agregados para cubrir todas las ramas ----

	@Test
	@DisplayName("UT-25 addUsuario: sin cuerpo (null) no guarda y devuelve 0")
	void ut25_agregarNull() {
		assertEquals(0, controller.addUsuario(null));
		verify(repo, never()).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-26 addUsuario: correo en blanco responde 400 y no guarda")
	void ut26_agregarCorreoEnBlanco() {
		Usuario sinCorreo = new Usuario(null, "Ana", "   ", "1234", "Estudiante", null);

		ResponseStatusException error = assertThrows(ResponseStatusException.class,
				() -> controller.addUsuario(sinCorreo));

		assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
		verify(repo, never()).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-27 addUsuario: contrasena vacia responde 400 y no guarda")
	void ut27_agregarSinContrasena() {
		Usuario sinContrasena = new Usuario(null, "Ana", "ana@correo.com", "", "Estudiante", null);

		ResponseStatusException error = assertThrows(ResponseStatusException.class,
				() -> controller.addUsuario(sinContrasena));

		assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
		verify(repo, never()).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-28 updateUsuario: sin cuerpo (null) no guarda y devuelve 0")
	void ut28_actualizarNull() {
		assertEquals(0, controller.updateUsuario(null));
		verify(repo, never()).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-29 deleteUsuario: sin cuerpo (null) no elimina y devuelve 0")
	void ut29_eliminarNull() {
		assertEquals(0, controller.deleteUsuario(null));
		verify(repo, never()).delete(any(Usuario.class));
	}
}
