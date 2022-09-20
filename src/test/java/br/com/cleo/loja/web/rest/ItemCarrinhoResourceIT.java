package br.com.cleo.loja.web.rest;

import static br.com.cleo.loja.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.cleo.loja.IntegrationTest;
import br.com.cleo.loja.domain.ItemCarrinho;
import br.com.cleo.loja.repository.ItemCarrinhoRepository;
import br.com.cleo.loja.repository.search.ItemCarrinhoSearchRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ItemCarrinhoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ItemCarrinhoResourceIT {

    private static final Integer DEFAULT_QUANTIDADE = 1;
    private static final Integer UPDATED_QUANTIDADE = 2;

    private static final BigDecimal DEFAULT_PRECO_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECO_TOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/item-carrinhos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/item-carrinhos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ItemCarrinhoSearchRepository itemCarrinhoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restItemCarrinhoMockMvc;

    private ItemCarrinho itemCarrinho;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCarrinho createEntity(EntityManager em) {
        ItemCarrinho itemCarrinho = new ItemCarrinho().quantidade(DEFAULT_QUANTIDADE).precoTotal(DEFAULT_PRECO_TOTAL);
        return itemCarrinho;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCarrinho createUpdatedEntity(EntityManager em) {
        ItemCarrinho itemCarrinho = new ItemCarrinho().quantidade(UPDATED_QUANTIDADE).precoTotal(UPDATED_PRECO_TOTAL);
        return itemCarrinho;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        itemCarrinhoSearchRepository.deleteAll();
        assertThat(itemCarrinhoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        itemCarrinho = createEntity(em);
    }

    @Test
    @Transactional
    void createItemCarrinho() throws Exception {
        int databaseSizeBeforeCreate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        // Create the ItemCarrinho
        restItemCarrinhoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(itemCarrinho)))
            .andExpect(status().isCreated());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ItemCarrinho testItemCarrinho = itemCarrinhoList.get(itemCarrinhoList.size() - 1);
        assertThat(testItemCarrinho.getQuantidade()).isEqualTo(DEFAULT_QUANTIDADE);
        assertThat(testItemCarrinho.getPrecoTotal()).isEqualByComparingTo(DEFAULT_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void createItemCarrinhoWithExistingId() throws Exception {
        // Create the ItemCarrinho with an existing ID
        itemCarrinho.setId(1L);

        int databaseSizeBeforeCreate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemCarrinhoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(itemCarrinho)))
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllItemCarrinhos() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);

        // Get all the itemCarrinhoList
        restItemCarrinhoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(itemCarrinho.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].precoTotal").value(hasItem(sameNumber(DEFAULT_PRECO_TOTAL))));
    }

    @Test
    @Transactional
    void getItemCarrinho() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);

        // Get the itemCarrinho
        restItemCarrinhoMockMvc
            .perform(get(ENTITY_API_URL_ID, itemCarrinho.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(itemCarrinho.getId().intValue()))
            .andExpect(jsonPath("$.quantidade").value(DEFAULT_QUANTIDADE))
            .andExpect(jsonPath("$.precoTotal").value(sameNumber(DEFAULT_PRECO_TOTAL)));
    }

    @Test
    @Transactional
    void getNonExistingItemCarrinho() throws Exception {
        // Get the itemCarrinho
        restItemCarrinhoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingItemCarrinho() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);

        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        itemCarrinhoSearchRepository.save(itemCarrinho);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());

        // Update the itemCarrinho
        ItemCarrinho updatedItemCarrinho = itemCarrinhoRepository.findById(itemCarrinho.getId()).get();
        // Disconnect from session so that the updates on updatedItemCarrinho are not directly saved in db
        em.detach(updatedItemCarrinho);
        updatedItemCarrinho.quantidade(UPDATED_QUANTIDADE).precoTotal(UPDATED_PRECO_TOTAL);

        restItemCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedItemCarrinho.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedItemCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        ItemCarrinho testItemCarrinho = itemCarrinhoList.get(itemCarrinhoList.size() - 1);
        assertThat(testItemCarrinho.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testItemCarrinho.getPrecoTotal()).isEqualByComparingTo(UPDATED_PRECO_TOTAL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ItemCarrinho> itemCarrinhoSearchList = IterableUtils.toList(itemCarrinhoSearchRepository.findAll());
                ItemCarrinho testItemCarrinhoSearch = itemCarrinhoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testItemCarrinhoSearch.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
                assertThat(testItemCarrinhoSearch.getPrecoTotal()).isEqualByComparingTo(UPDATED_PRECO_TOTAL);
            });
    }

    @Test
    @Transactional
    void putNonExistingItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, itemCarrinho.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(itemCarrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(itemCarrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(itemCarrinho)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateItemCarrinhoWithPatch() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);

        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();

        // Update the itemCarrinho using partial update
        ItemCarrinho partialUpdatedItemCarrinho = new ItemCarrinho();
        partialUpdatedItemCarrinho.setId(itemCarrinho.getId());

        partialUpdatedItemCarrinho.quantidade(UPDATED_QUANTIDADE);

        restItemCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemCarrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedItemCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        ItemCarrinho testItemCarrinho = itemCarrinhoList.get(itemCarrinhoList.size() - 1);
        assertThat(testItemCarrinho.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testItemCarrinho.getPrecoTotal()).isEqualByComparingTo(DEFAULT_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateItemCarrinhoWithPatch() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);

        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();

        // Update the itemCarrinho using partial update
        ItemCarrinho partialUpdatedItemCarrinho = new ItemCarrinho();
        partialUpdatedItemCarrinho.setId(itemCarrinho.getId());

        partialUpdatedItemCarrinho.quantidade(UPDATED_QUANTIDADE).precoTotal(UPDATED_PRECO_TOTAL);

        restItemCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemCarrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedItemCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        ItemCarrinho testItemCarrinho = itemCarrinhoList.get(itemCarrinhoList.size() - 1);
        assertThat(testItemCarrinho.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testItemCarrinho.getPrecoTotal()).isEqualByComparingTo(UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, itemCarrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(itemCarrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(itemCarrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamItemCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        itemCarrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(itemCarrinho))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemCarrinho in the database
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteItemCarrinho() throws Exception {
        // Initialize the database
        itemCarrinhoRepository.saveAndFlush(itemCarrinho);
        itemCarrinhoRepository.save(itemCarrinho);
        itemCarrinhoSearchRepository.save(itemCarrinho);

        int databaseSizeBeforeDelete = itemCarrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the itemCarrinho
        restItemCarrinhoMockMvc
            .perform(delete(ENTITY_API_URL_ID, itemCarrinho.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ItemCarrinho> itemCarrinhoList = itemCarrinhoRepository.findAll();
        assertThat(itemCarrinhoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(itemCarrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchItemCarrinho() throws Exception {
        // Initialize the database
        itemCarrinho = itemCarrinhoRepository.saveAndFlush(itemCarrinho);
        itemCarrinhoSearchRepository.save(itemCarrinho);

        // Search the itemCarrinho
        restItemCarrinhoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + itemCarrinho.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(itemCarrinho.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].precoTotal").value(hasItem(sameNumber(DEFAULT_PRECO_TOTAL))));
    }
}
