package com.redesocial.efg.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.redesocial.efg.model.Usuario;



public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Optional<Usuario> findByUsuario(String usuario);
}