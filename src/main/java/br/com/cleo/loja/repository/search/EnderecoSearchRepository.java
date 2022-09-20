package br.com.cleo.loja.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.cleo.loja.domain.Endereco;
import br.com.cleo.loja.repository.EnderecoRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Endereco} entity.
 */
public interface EnderecoSearchRepository extends ElasticsearchRepository<Endereco, Long>, EnderecoSearchRepositoryInternal {}

interface EnderecoSearchRepositoryInternal {
    Stream<Endereco> search(String query);

    Stream<Endereco> search(Query query);

    void index(Endereco entity);
}

class EnderecoSearchRepositoryInternalImpl implements EnderecoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EnderecoRepository repository;

    EnderecoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EnderecoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Endereco> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Endereco> search(Query query) {
        return elasticsearchTemplate.search(query, Endereco.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Endereco entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
