package br.com.cleo.loja.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.cleo.loja.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarrinhoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carrinho.class);
        Carrinho carrinho1 = new Carrinho();
        carrinho1.setId(1L);
        Carrinho carrinho2 = new Carrinho();
        carrinho2.setId(carrinho1.getId());
        assertThat(carrinho1).isEqualTo(carrinho2);
        carrinho2.setId(2L);
        assertThat(carrinho1).isNotEqualTo(carrinho2);
        carrinho1.setId(null);
        assertThat(carrinho1).isNotEqualTo(carrinho2);
    }
}
