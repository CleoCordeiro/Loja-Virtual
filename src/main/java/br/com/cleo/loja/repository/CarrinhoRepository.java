package br.com.cleo.loja.repository;

import br.com.cleo.loja.domain.Carrinho;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Carrinho entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {}
