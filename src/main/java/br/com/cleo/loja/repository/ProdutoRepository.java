package br.com.cleo.loja.repository;

import br.com.cleo.loja.domain.Produto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Produto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
