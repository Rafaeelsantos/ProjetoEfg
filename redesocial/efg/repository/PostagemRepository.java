package com.redesocial.efg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.redesocial.efg.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long>{

	List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);
	
	// SELECT * FROM tb_postagens WHERE titulo LIKE "%titulo%";
}