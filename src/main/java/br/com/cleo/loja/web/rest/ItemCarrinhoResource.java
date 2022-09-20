package br.com.cleo.loja.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.cleo.loja.domain.ItemCarrinho;
import br.com.cleo.loja.repository.ItemCarrinhoRepository;
import br.com.cleo.loja.repository.search.ItemCarrinhoSearchRepository;
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
 * REST controller for managing {@link br.com.cleo.loja.domain.ItemCarrinho}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ItemCarrinhoResource {

    private final Logger log = LoggerFactory.getLogger(ItemCarrinhoResource.class);

    private static final String ENTITY_NAME = "itemCarrinho";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ItemCarrinhoRepository itemCarrinhoRepository;

    private final ItemCarrinhoSearchRepository itemCarrinhoSearchRepository;

    public ItemCarrinhoResource(ItemCarrinhoRepository itemCarrinhoRepository, ItemCarrinhoSearchRepository itemCarrinhoSearchRepository) {
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.itemCarrinhoSearchRepository = itemCarrinhoSearchRepository;
    }

    /**
     * {@code POST  /item-carrinhos} : Create a new itemCarrinho.
     *
     * @param itemCarrinho the itemCarrinho to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemCarrinho, or with status {@code 400 (Bad Request)} if the itemCarrinho has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/item-carrinhos")
    public ResponseEntity<ItemCarrinho> createItemCarrinho(@RequestBody ItemCarrinho itemCarrinho) throws URISyntaxException {
        log.debug("REST request to save ItemCarrinho : {}", itemCarrinho);
        if (itemCarrinho.getId() != null) {
            throw new BadRequestAlertException("A new itemCarrinho cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ItemCarrinho result = itemCarrinhoRepository.save(itemCarrinho);
        itemCarrinhoSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/item-carrinhos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /item-carrinhos/:id} : Updates an existing itemCarrinho.
     *
     * @param id the id of the itemCarrinho to save.
     * @param itemCarrinho the itemCarrinho to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCarrinho,
     * or with status {@code 400 (Bad Request)} if the itemCarrinho is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemCarrinho couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/item-carrinhos/{id}")
    public ResponseEntity<ItemCarrinho> updateItemCarrinho(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemCarrinho itemCarrinho
    ) throws URISyntaxException {
        log.debug("REST request to update ItemCarrinho : {}, {}", id, itemCarrinho);
        if (itemCarrinho.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCarrinho.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemCarrinhoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ItemCarrinho result = itemCarrinhoRepository.save(itemCarrinho);
        itemCarrinhoSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemCarrinho.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /item-carrinhos/:id} : Partial updates given fields of an existing itemCarrinho, field will ignore if it is null
     *
     * @param id the id of the itemCarrinho to save.
     * @param itemCarrinho the itemCarrinho to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCarrinho,
     * or with status {@code 400 (Bad Request)} if the itemCarrinho is not valid,
     * or with status {@code 404 (Not Found)} if the itemCarrinho is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemCarrinho couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/item-carrinhos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ItemCarrinho> partialUpdateItemCarrinho(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ItemCarrinho itemCarrinho
    ) throws URISyntaxException {
        log.debug("REST request to partial update ItemCarrinho partially : {}, {}", id, itemCarrinho);
        if (itemCarrinho.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCarrinho.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemCarrinhoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ItemCarrinho> result = itemCarrinhoRepository
            .findById(itemCarrinho.getId())
            .map(existingItemCarrinho -> {
                if (itemCarrinho.getQuantidade() != null) {
                    existingItemCarrinho.setQuantidade(itemCarrinho.getQuantidade());
                }
                if (itemCarrinho.getPrecoTotal() != null) {
                    existingItemCarrinho.setPrecoTotal(itemCarrinho.getPrecoTotal());
                }

                return existingItemCarrinho;
            })
            .map(itemCarrinhoRepository::save)
            .map(savedItemCarrinho -> {
                itemCarrinhoSearchRepository.save(savedItemCarrinho);

                return savedItemCarrinho;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemCarrinho.getId().toString())
        );
    }

    /**
     * {@code GET  /item-carrinhos} : get all the itemCarrinhos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of itemCarrinhos in body.
     */
    @GetMapping("/item-carrinhos")
    public List<ItemCarrinho> getAllItemCarrinhos() {
        log.debug("REST request to get all ItemCarrinhos");
        return itemCarrinhoRepository.findAll();
    }

    /**
     * {@code GET  /item-carrinhos/:id} : get the "id" itemCarrinho.
     *
     * @param id the id of the itemCarrinho to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemCarrinho, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/item-carrinhos/{id}")
    public ResponseEntity<ItemCarrinho> getItemCarrinho(@PathVariable Long id) {
        log.debug("REST request to get ItemCarrinho : {}", id);
        Optional<ItemCarrinho> itemCarrinho = itemCarrinhoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(itemCarrinho);
    }

    /**
     * {@code DELETE  /item-carrinhos/:id} : delete the "id" itemCarrinho.
     *
     * @param id the id of the itemCarrinho to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/item-carrinhos/{id}")
    public ResponseEntity<Void> deleteItemCarrinho(@PathVariable Long id) {
        log.debug("REST request to delete ItemCarrinho : {}", id);
        itemCarrinhoRepository.deleteById(id);
        itemCarrinhoSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/item-carrinhos?query=:query} : search for the itemCarrinho corresponding
     * to the query.
     *
     * @param query the query of the itemCarrinho search.
     * @return the result of the search.
     */
    @GetMapping("/_search/item-carrinhos")
    public List<ItemCarrinho> searchItemCarrinhos(@RequestParam String query) {
        log.debug("REST request to search ItemCarrinhos for query {}", query);
        return StreamSupport.stream(itemCarrinhoSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
