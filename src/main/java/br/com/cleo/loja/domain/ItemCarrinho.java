package br.com.cleo.loja.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ItemCarrinho.
 */
@Entity
@Table(name = "item_carrinho")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "itemcarrinho")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCarrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "preco_total", precision = 21, scale = 2)
    private BigDecimal precoTotal;

    @ManyToOne
    @JsonIgnoreProperties(value = { "usuario" }, allowSetters = true)
    private Carrinho carrinho;

    @ManyToOne
    private Produto produto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ItemCarrinho id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return this.quantidade;
    }

    public ItemCarrinho quantidade(Integer quantidade) {
        this.setQuantidade(quantidade);
        return this;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoTotal() {
        return this.precoTotal;
    }

    public ItemCarrinho precoTotal(BigDecimal precoTotal) {
        this.setPrecoTotal(precoTotal);
        return this;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    public Carrinho getCarrinho() {
        return this.carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public ItemCarrinho carrinho(Carrinho carrinho) {
        this.setCarrinho(carrinho);
        return this;
    }

    public Produto getProduto() {
        return this.produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public ItemCarrinho produto(Produto produto) {
        this.setProduto(produto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemCarrinho)) {
            return false;
        }
        return id != null && id.equals(((ItemCarrinho) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCarrinho{" +
            "id=" + getId() +
            ", quantidade=" + getQuantidade() +
            ", precoTotal=" + getPrecoTotal() +
            "}";
    }
}
