package br.com.cleo.loja.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.cleo.loja.domain.Produto;
import br.com.cleo.loja.repository.ProdutoRepository;
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
 * Spring Data Elasticsearch repository for the {@link Produto} entity.
 */
public interface ProdutoSearchRepository extends ElasticsearchRepository<Produto, Long>, ProdutoSearchRepositoryInternal {}

interface ProdutoSearchRepositoryInternal {
    Stream<Produto> search(String query);

    Stream<Produto> search(Query query);

    void index(Produto entity);
}

class ProdutoSearchRepositoryInternalImpl implements ProdutoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ProdutoRepository repository;

    ProdutoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ProdutoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Produto> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Produto> search(Query query) {
        return elasticsearchTemplate.search(query, Produto.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Produto entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
