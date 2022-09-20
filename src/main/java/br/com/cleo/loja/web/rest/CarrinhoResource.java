package br.com.cleo.loja.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.cleo.loja.domain.Carrinho;
import br.com.cleo.loja.repository.CarrinhoRepository;
import br.com.cleo.loja.repository.search.CarrinhoSearchRepository;
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
 * REST controller for managing {@link br.com.cleo.loja.domain.Carrinho}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarrinhoResource {

    private final Logger log = LoggerFactory.getLogger(CarrinhoResource.class);

    private static final String ENTITY_NAME = "carrinho";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarrinhoRepository carrinhoRepository;

    private final CarrinhoSearchRepository carrinhoSearchRepository;

    public CarrinhoResource(CarrinhoRepository carrinhoRepository, CarrinhoSearchRepository carrinhoSearchRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.carrinhoSearchRepository = carrinhoSearchRepository;
    }

    /**
     * {@code POST  /carrinhos} : Create a new carrinho.
     *
     * @param carrinho the carrinho to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carrinho, or with status {@code 400 (Bad Request)} if the carrinho has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carrinhos")
    public ResponseEntity<Carrinho> createCarrinho(@RequestBody Carrinho carrinho) throws URISyntaxException {
        log.debug("REST request to save Carrinho : {}", carrinho);
        if (carrinho.getId() != null) {
            throw new BadRequestAlertException("A new carrinho cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Carrinho result = carrinhoRepository.save(carrinho);
        carrinhoSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/carrinhos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /carrinhos/:id} : Updates an existing carrinho.
     *
     * @param id the id of the carrinho to save.
     * @param carrinho the carrinho to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carrinho,
     * or with status {@code 400 (Bad Request)} if the carrinho is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carrinho couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carrinhos/{id}")
    public ResponseEntity<Carrinho> updateCarrinho(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Carrinho carrinho
    ) throws URISyntaxException {
        log.debug("REST request to update Carrinho : {}, {}", id, carrinho);
        if (carrinho.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carrinho.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carrinhoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Carrinho result = carrinhoRepository.save(carrinho);
        carrinhoSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carrinho.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /carrinhos/:id} : Partial updates given fields of an existing carrinho, field will ignore if it is null
     *
     * @param id the id of the carrinho to save.
     * @param carrinho the carrinho to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carrinho,
     * or with status {@code 400 (Bad Request)} if the carrinho is not valid,
     * or with status {@code 404 (Not Found)} if the carrinho is not found,
     * or with status {@code 500 (Internal Server Error)} if the carrinho couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/carrinhos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Carrinho> partialUpdateCarrinho(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Carrinho carrinho
    ) throws URISyntaxException {
        log.debug("REST request to partial update Carrinho partially : {}, {}", id, carrinho);
        if (carrinho.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carrinho.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carrinhoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Carrinho> result = carrinhoRepository
            .findById(carrinho.getId())
            .map(existingCarrinho -> {
                if (carrinho.getDataCriacao() != null) {
                    existingCarrinho.setDataCriacao(carrinho.getDataCriacao());
                }
                if (carrinho.getDataAlteracao() != null) {
                    existingCarrinho.setDataAlteracao(carrinho.getDataAlteracao());
                }

                return existingCarrinho;
            })
            .map(carrinhoRepository::save)
            .map(savedCarrinho -> {
                carrinhoSearchRepository.save(savedCarrinho);

                return savedCarrinho;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carrinho.getId().toString())
        );
    }

    /**
     * {@code GET  /carrinhos} : get all the carrinhos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carrinhos in body.
     */
    @GetMapping("/carrinhos")
    public List<Carrinho> getAllCarrinhos() {
        log.debug("REST request to get all Carrinhos");
        return carrinhoRepository.findAll();
    }

    /**
     * {@code GET  /carrinhos/:id} : get the "id" carrinho.
     *
     * @param id the id of the carrinho to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carrinho, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carrinhos/{id}")
    public ResponseEntity<Carrinho> getCarrinho(@PathVariable Long id) {
        log.debug("REST request to get Carrinho : {}", id);
        Optional<Carrinho> carrinho = carrinhoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(carrinho);
    }

    /**
     * {@code DELETE  /carrinhos/:id} : delete the "id" carrinho.
     *
     * @param id the id of the carrinho to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carrinhos/{id}")
    public ResponseEntity<Void> deleteCarrinho(@PathVariable Long id) {
        log.debug("REST request to delete Carrinho : {}", id);
        carrinhoRepository.deleteById(id);
        carrinhoSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/carrinhos?query=:query} : search for the carrinho corresponding
     * to the query.
     *
     * @param query the query of the carrinho search.
     * @return the result of the search.
     */
    @GetMapping("/_search/carrinhos")
    public List<Carrinho> searchCarrinhos(@RequestParam String query) {
        log.debug("REST request to search Carrinhos for query {}", query);
        return StreamSupport.stream(carrinhoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
