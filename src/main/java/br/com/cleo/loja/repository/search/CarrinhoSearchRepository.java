package br.com.cleo.loja.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.cleo.loja.domain.Carrinho;
import br.com.cleo.loja.repository.CarrinhoRepository;
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
 * Spring Data Elasticsearch repository for the {@link Carrinho} entity.
 */
public interface CarrinhoSearchRepository extends ElasticsearchRepository<Carrinho, Long>, CarrinhoSearchRepositoryInternal {}

interface CarrinhoSearchRepositoryInternal {
    Stream<Carrinho> search(String query);

    Stream<Carrinho> search(Query query);

    void index(Carrinho entity);
}

class CarrinhoSearchRepositoryInternalImpl implements CarrinhoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CarrinhoRepository repository;

    CarrinhoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CarrinhoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Carrinho> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Carrinho> search(Query query) {
        return elasticsearchTemplate.search(query, Carrinho.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Carrinho entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
