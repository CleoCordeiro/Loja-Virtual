package br.com.cleo.loja.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Carrinho.
 */
@Entity
@Table(name = "carrinho")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "carrinho")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Carrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "data_alteracao")
    private LocalDate dataAlteracao;

    @ManyToOne
    @JsonIgnoreProperties(value = { "endereco" }, allowSetters = true)
    private Usuario usuario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Carrinho id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataCriacao() {
        return this.dataCriacao;
    }

    public Carrinho dataCriacao(LocalDate dataCriacao) {
        this.setDataCriacao(dataCriacao);
        return this;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataAlteracao() {
        return this.dataAlteracao;
    }

    public Carrinho dataAlteracao(LocalDate dataAlteracao) {
        this.setDataAlteracao(dataAlteracao);
        return this;
    }

    public void setDataAlteracao(LocalDate dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Carrinho usuario(Usuario usuario) {
        this.setUsuario(usuario);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Carrinho)) {
            return false;
        }
        return id != null && id.equals(((Carrinho) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Carrinho{" +
            "id=" + getId() +
            ", dataCriacao='" + getDataCriacao() + "'" +
            ", dataAlteracao='" + getDataAlteracao() + "'" +
            "}";
    }
}
