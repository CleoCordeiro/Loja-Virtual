package br.com.cleo.loja.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.cleo.loja.IntegrationTest;
import br.com.cleo.loja.domain.Endereco;
import br.com.cleo.loja.repository.EnderecoRepository;
import br.com.cleo.loja.repository.search.EnderecoSearchRepository;
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
 * Integration tests for the {@link EnderecoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnderecoResourceIT {

    private static final String DEFAULT_LOGRADOURO = "AAAAAAAAAA";
    private static final String UPDATED_LOGRADOURO = "BBBBBBBBBB";

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final String DEFAULT_COMPLEMENTO = "AAAAAAAAAA";
    private static final String UPDATED_COMPLEMENTO = "BBBBBBBBBB";

    private static final String DEFAULT_BAIRRO = "AAAAAAAAAA";
    private static final String UPDATED_BAIRRO = "BBBBBBBBBB";

    private static final String DEFAULT_CIDADE = "AAAAAAAAAA";
    private static final String UPDATED_CIDADE = "BBBBBBBBBB";

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final String DEFAULT_CEP = "AAAAAAAAAA";
    private static final String UPDATED_CEP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/enderecos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/enderecos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoSearchRepository enderecoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnderecoMockMvc;

    private Endereco endereco;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(DEFAULT_LOGRADOURO)
            .numero(DEFAULT_NUMERO)
            .complemento(DEFAULT_COMPLEMENTO)
            .bairro(DEFAULT_BAIRRO)
            .cidade(DEFAULT_CIDADE)
            .estado(DEFAULT_ESTADO)
            .cep(DEFAULT_CEP);
        return endereco;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createUpdatedEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cidade(UPDATED_CIDADE)
            .estado(UPDATED_ESTADO)
            .cep(UPDATED_CEP);
        return endereco;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        enderecoSearchRepository.deleteAll();
        assertThat(enderecoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        endereco = createEntity(em);
    }

    @Test
    @Transactional
    void createEndereco() throws Exception {
        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        // Create the Endereco
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(endereco)))
            .andExpect(status().isCreated());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(DEFAULT_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
        assertThat(testEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testEndereco.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testEndereco.getCep()).isEqualTo(DEFAULT_CEP);
    }

    @Test
    @Transactional
    void createEnderecoWithExistingId() throws Exception {
        // Create the Endereco with an existing ID
        endereco.setId(1L);

        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(endereco)))
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEnderecos() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(endereco.getId().intValue())))
            .andExpect(jsonPath("$.[*].logradouro").value(hasItem(DEFAULT_LOGRADOURO)))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].complemento").value(hasItem(DEFAULT_COMPLEMENTO)))
            .andExpect(jsonPath("$.[*].bairro").value(hasItem(DEFAULT_BAIRRO)))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO)))
            .andExpect(jsonPath("$.[*].cep").value(hasItem(DEFAULT_CEP)));
    }

    @Test
    @Transactional
    void getEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get the endereco
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL_ID, endereco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(endereco.getId().intValue()))
            .andExpect(jsonPath("$.logradouro").value(DEFAULT_LOGRADOURO))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.complemento").value(DEFAULT_COMPLEMENTO))
            .andExpect(jsonPath("$.bairro").value(DEFAULT_BAIRRO))
            .andExpect(jsonPath("$.cidade").value(DEFAULT_CIDADE))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO))
            .andExpect(jsonPath("$.cep").value(DEFAULT_CEP));
    }

    @Test
    @Transactional
    void getNonExistingEndereco() throws Exception {
        // Get the endereco
        restEnderecoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        enderecoSearchRepository.save(endereco);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());

        // Update the endereco
        Endereco updatedEndereco = enderecoRepository.findById(endereco.getId()).get();
        // Disconnect from session so that the updates on updatedEndereco are not directly saved in db
        em.detach(updatedEndereco);
        updatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cidade(UPDATED_CIDADE)
            .estado(UPDATED_ESTADO)
            .cep(UPDATED_CEP);

        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEndereco.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEndereco))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testEndereco.getCep()).isEqualTo(UPDATED_CEP);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Endereco> enderecoSearchList = IterableUtils.toList(enderecoSearchRepository.findAll());
                Endereco testEnderecoSearch = enderecoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEnderecoSearch.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
                assertThat(testEnderecoSearch.getNumero()).isEqualTo(UPDATED_NUMERO);
                assertThat(testEnderecoSearch.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
                assertThat(testEnderecoSearch.getBairro()).isEqualTo(UPDATED_BAIRRO);
                assertThat(testEnderecoSearch.getCidade()).isEqualTo(UPDATED_CIDADE);
                assertThat(testEnderecoSearch.getEstado()).isEqualTo(UPDATED_ESTADO);
                assertThat(testEnderecoSearch.getCep()).isEqualTo(UPDATED_CEP);
            });
    }

    @Test
    @Transactional
    void putNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, endereco.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(endereco))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(endereco))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(endereco)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cidade(UPDATED_CIDADE)
            .estado(UPDATED_ESTADO)
            .cep(UPDATED_CEP);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testEndereco.getCep()).isEqualTo(UPDATED_CEP);
    }

    @Test
    @Transactional
    void fullUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cidade(UPDATED_CIDADE)
            .estado(UPDATED_ESTADO)
            .cep(UPDATED_CEP);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testEndereco.getCep()).isEqualTo(UPDATED_CEP);
    }

    @Test
    @Transactional
    void patchNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, endereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(endereco))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(endereco))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(endereco)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);
        enderecoRepository.save(endereco);
        enderecoSearchRepository.save(endereco);

        int databaseSizeBeforeDelete = enderecoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the endereco
        restEnderecoMockMvc
            .perform(delete(ENTITY_API_URL_ID, endereco.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enderecoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEndereco() throws Exception {
        // Initialize the database
        endereco = enderecoRepository.saveAndFlush(endereco);
        enderecoSearchRepository.save(endereco);

        // Search the endereco
        restEnderecoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + endereco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(endereco.getId().intValue())))
            .andExpect(jsonPath("$.[*].logradouro").value(hasItem(DEFAULT_LOGRADOURO)))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].complemento").value(hasItem(DEFAULT_COMPLEMENTO)))
            .andExpect(jsonPath("$.[*].bairro").value(hasItem(DEFAULT_BAIRRO)))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO)))
            .andExpect(jsonPath("$.[*].cep").value(hasItem(DEFAULT_CEP)));
    }
}
