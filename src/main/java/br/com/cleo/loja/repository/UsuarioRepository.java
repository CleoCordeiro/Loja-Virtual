package br.com.cleo.loja.repository;

import br.com.cleo.loja.domain.Usuario;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Usuario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
