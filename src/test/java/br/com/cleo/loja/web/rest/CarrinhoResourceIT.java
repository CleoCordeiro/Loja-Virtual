package br.com.cleo.loja.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.cleo.loja.IntegrationTest;
import br.com.cleo.loja.domain.Carrinho;
import br.com.cleo.loja.repository.CarrinhoRepository;
import br.com.cleo.loja.repository.search.CarrinhoSearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link CarrinhoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarrinhoResourceIT {

    private static final LocalDate DEFAULT_DATA_CRIACAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CRIACAO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_ALTERACAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ALTERACAO = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/carrinhos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/carrinhos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoSearchRepository carrinhoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarrinhoMockMvc;

    private Carrinho carrinho;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrinho createEntity(EntityManager em) {
        Carrinho carrinho = new Carrinho().dataCriacao(DEFAULT_DATA_CRIACAO).dataAlteracao(DEFAULT_DATA_ALTERACAO);
        return carrinho;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrinho createUpdatedEntity(EntityManager em) {
        Carrinho carrinho = new Carrinho().dataCriacao(UPDATED_DATA_CRIACAO).dataAlteracao(UPDATED_DATA_ALTERACAO);
        return carrinho;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        carrinhoSearchRepository.deleteAll();
        assertThat(carrinhoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        carrinho = createEntity(em);
    }

    @Test
    @Transactional
    void createCarrinho() throws Exception {
        int databaseSizeBeforeCreate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        // Create the Carrinho
        restCarrinhoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carrinho)))
            .andExpect(status().isCreated());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Carrinho testCarrinho = carrinhoList.get(carrinhoList.size() - 1);
        assertThat(testCarrinho.getDataCriacao()).isEqualTo(DEFAULT_DATA_CRIACAO);
        assertThat(testCarrinho.getDataAlteracao()).isEqualTo(DEFAULT_DATA_ALTERACAO);
    }

    @Test
    @Transactional
    void createCarrinhoWithExistingId() throws Exception {
        // Create the Carrinho with an existing ID
        carrinho.setId(1L);

        int databaseSizeBeforeCreate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarrinhoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carrinho)))
            .andExpect(status().isBadRequest());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarrinhos() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);

        // Get all the carrinhoList
        restCarrinhoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carrinho.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataCriacao").value(hasItem(DEFAULT_DATA_CRIACAO.toString())))
            .andExpect(jsonPath("$.[*].dataAlteracao").value(hasItem(DEFAULT_DATA_ALTERACAO.toString())));
    }

    @Test
    @Transactional
    void getCarrinho() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);

        // Get the carrinho
        restCarrinhoMockMvc
            .perform(get(ENTITY_API_URL_ID, carrinho.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carrinho.getId().intValue()))
            .andExpect(jsonPath("$.dataCriacao").value(DEFAULT_DATA_CRIACAO.toString()))
            .andExpect(jsonPath("$.dataAlteracao").value(DEFAULT_DATA_ALTERACAO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCarrinho() throws Exception {
        // Get the carrinho
        restCarrinhoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarrinho() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);

        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        carrinhoSearchRepository.save(carrinho);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());

        // Update the carrinho
        Carrinho updatedCarrinho = carrinhoRepository.findById(carrinho.getId()).get();
        // Disconnect from session so that the updates on updatedCarrinho are not directly saved in db
        em.detach(updatedCarrinho);
        updatedCarrinho.dataCriacao(UPDATED_DATA_CRIACAO).dataAlteracao(UPDATED_DATA_ALTERACAO);

        restCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCarrinho.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        Carrinho testCarrinho = carrinhoList.get(carrinhoList.size() - 1);
        assertThat(testCarrinho.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCarrinho.getDataAlteracao()).isEqualTo(UPDATED_DATA_ALTERACAO);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Carrinho> carrinhoSearchList = IterableUtils.toList(carrinhoSearchRepository.findAll());
                Carrinho testCarrinhoSearch = carrinhoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCarrinhoSearch.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
                assertThat(testCarrinhoSearch.getDataAlteracao()).isEqualTo(UPDATED_DATA_ALTERACAO);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carrinho.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carrinho)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarrinhoWithPatch() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);

        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();

        // Update the carrinho using partial update
        Carrinho partialUpdatedCarrinho = new Carrinho();
        partialUpdatedCarrinho.setId(carrinho.getId());

        partialUpdatedCarrinho.dataCriacao(UPDATED_DATA_CRIACAO).dataAlteracao(UPDATED_DATA_ALTERACAO);

        restCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        Carrinho testCarrinho = carrinhoList.get(carrinhoList.size() - 1);
        assertThat(testCarrinho.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCarrinho.getDataAlteracao()).isEqualTo(UPDATED_DATA_ALTERACAO);
    }

    @Test
    @Transactional
    void fullUpdateCarrinhoWithPatch() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);

        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();

        // Update the carrinho using partial update
        Carrinho partialUpdatedCarrinho = new Carrinho();
        partialUpdatedCarrinho.setId(carrinho.getId());

        partialUpdatedCarrinho.dataCriacao(UPDATED_DATA_CRIACAO).dataAlteracao(UPDATED_DATA_ALTERACAO);

        restCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarrinho))
            )
            .andExpect(status().isOk());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        Carrinho testCarrinho = carrinhoList.get(carrinhoList.size() - 1);
        assertThat(testCarrinho.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCarrinho.getDataAlteracao()).isEqualTo(UPDATED_DATA_ALTERACAO);
    }

    @Test
    @Transactional
    void patchNonExistingCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carrinho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carrinho))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarrinho() throws Exception {
        int databaseSizeBeforeUpdate = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        carrinho.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarrinhoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(carrinho)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrinho in the database
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarrinho() throws Exception {
        // Initialize the database
        carrinhoRepository.saveAndFlush(carrinho);
        carrinhoRepository.save(carrinho);
        carrinhoSearchRepository.save(carrinho);

        int databaseSizeBeforeDelete = carrinhoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carrinho
        restCarrinhoMockMvc
            .perform(delete(ENTITY_API_URL_ID, carrinho.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Carrinho> carrinhoList = carrinhoRepository.findAll();
        assertThat(carrinhoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carrinhoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarrinho() throws Exception {
        // Initialize the database
        carrinho = carrinhoRepository.saveAndFlush(carrinho);
        carrinhoSearchRepository.save(carrinho);

        // Search the carrinho
        restCarrinhoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carrinho.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carrinho.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataCriacao").value(hasItem(DEFAULT_DATA_CRIACAO.toString())))
            .andExpect(jsonPath("$.[*].dataAlteracao").value(hasItem(DEFAULT_DATA_ALTERACAO.toString())));
    }
}
