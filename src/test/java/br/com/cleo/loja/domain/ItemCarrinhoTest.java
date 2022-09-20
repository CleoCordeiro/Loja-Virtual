package br.com.cleo.loja.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.cleo.loja.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemCarrinhoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemCarrinho.class);
        ItemCarrinho itemCarrinho1 = new ItemCarrinho();
        itemCarrinho1.setId(1L);
        ItemCarrinho itemCarrinho2 = new ItemCarrinho();
        itemCarrinho2.setId(itemCarrinho1.getId());
        assertThat(itemCarrinho1).isEqualTo(itemCarrinho2);
        itemCarrinho2.setId(2L);
        assertThat(itemCarrinho1).isNotEqualTo(itemCarrinho2);
        itemCarrinho1.setId(null);
        assertThat(itemCarrinho1).isNotEqualTo(itemCarrinho2);
    }
}
