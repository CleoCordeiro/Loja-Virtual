package br.com.cleo.loja.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.cleo.loja.domain.Endereco;
import br.com.cleo.loja.repository.EnderecoRepository;
import br.com.cleo.loja.repository.search.EnderecoSearchRepository;
import br.com.cleo.loja.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.cleo.loja.domain.Endereco}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EnderecoResource {

    private final Logger log = LoggerFactory.getLogger(EnderecoResource.class);

    private static final String ENTITY_NAME = "endereco";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnderecoRepository enderecoRepository;

    private final EnderecoSearchRepository enderecoSearchRepository;

    public EnderecoResource(EnderecoRepository enderecoRepository, EnderecoSearchRepository enderecoSearchRepository) {
        this.enderecoRepository = enderecoRepository;
        this.enderecoSearchRepository = enderecoSearchRepository;
    }

    /**
     * {@code POST  /enderecos} : Create a new endereco.
     *
     * @param endereco the endereco to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new endereco, or with status {@code 400 (Bad Request)} if the endereco has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enderecos")
    public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco) throws URISyntaxException {
        log.debug("REST request to save Endereco : {}", endereco);
        if (endereco.getId() != null) {
            throw new BadRequestAlertException("A new endereco cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Endereco result = enderecoRepository.save(endereco);
        enderecoSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/enderecos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /enderecos/:id} : Updates an existing endereco.
     *
     * @param id the id of the endereco to save.
     * @param endereco the endereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated endereco,
     * or with status {@code 400 (Bad Request)} if the endereco is not valid,
     * or with status {@code 500 (Internal Server Error)} if the endereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enderecos/{id}")
    public ResponseEntity<Endereco> updateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Endereco endereco
    ) throws URISyntaxException {
        log.debug("REST request to update Endereco : {}, {}", id, endereco);
        if (endereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, endereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enderecoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Endereco result = enderecoRepository.save(endereco);
        enderecoSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, endereco.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /enderecos/:id} : Partial updates given fields of an existing endereco, field will ignore if it is null
     *
     * @param id the id of the endereco to save.
     * @param endereco the endereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated endereco,
     * or with status {@code 400 (Bad Request)} if the endereco is not valid,
     * or with status {@code 404 (Not Found)} if the endereco is not found,
     * or with status {@code 500 (Internal Server Error)} if the endereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/enderecos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Endereco> partialUpdateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Endereco endereco
    ) throws URISyntaxException {
        log.debug("REST request to partial update Endereco partially : {}, {}", id, endereco);
        if (endereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, endereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enderecoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Endereco> result = enderecoRepository
            .findById(endereco.getId())
            .map(existingEndereco -> {
                if (endereco.getLogradouro() != null) {
                    existingEndereco.setLogradouro(endereco.getLogradouro());
                }
                if (endereco.getNumero() != null) {
                    existingEndereco.setNumero(endereco.getNumero());
                }
                if (endereco.getComplemento() != null) {
                    existingEndereco.setComplemento(endereco.getComplemento());
                }
                if (endereco.getBairro() != null) {
                    existingEndereco.setBairro(endereco.getBairro());
                }
                if (endereco.getCidade() != null) {
                    existingEndereco.setCidade(endereco.getCidade());
                }
                if (endereco.getEstado() != null) {
                    existingEndereco.setEstado(endereco.getEstado());
                }
                if (endereco.getCep() != null) {
                    existingEndereco.setCep(endereco.getCep());
                }

                return existingEndereco;
            })
            .map(enderecoRepository::save)
            .map(savedEndereco -> {
                enderecoSearchRepository.save(savedEndereco);

                return savedEndereco;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, endereco.getId().toString())
        );
    }

    /**
     * {@code GET  /enderecos} : get all the enderecos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enderecos in body.
     */
    @GetMapping("/enderecos")
    public List<Endereco> getAllEnderecos() {
        log.debug("REST request to get all Enderecos");
        return enderecoRepository.findAll();
    }

    /**
     * {@code GET  /enderecos/:id} : get the "id" endereco.
     *
     * @param id the id of the endereco to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the endereco, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enderecos/{id}")
    public ResponseEntity<Endereco> getEndereco(@PathVariable Long id) {
        log.debug("REST request to get Endereco : {}", id);
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(endereco);
    }

    /**
     * {@code DELETE  /enderecos/:id} : delete the "id" endereco.
     *
     * @param id the id of the endereco to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enderecos/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        log.debug("REST request to delete Endereco : {}", id);
        enderecoRepository.deleteById(id);
        enderecoSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/enderecos?query=:query} : search for the endereco corresponding
     * to the query.
     *
     * @param query the query of the endereco search.
     * @return the result of the search.
     */
    @GetMapping("/_search/enderecos")
    public List<Endereco> searchEnderecos(@RequestParam String query) {
        log.debug("REST request to search Enderecos for query {}", query);
        return StreamSupport.stream(enderecoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
