package br.com.cleo.loja.repository;

import br.com.cleo.loja.domain.ItemCarrinho;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ItemCarrinho entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {}
