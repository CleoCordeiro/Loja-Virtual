package br.com.cleo.loja.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.cleo.loja.domain.ItemCarrinho;
import br.com.cleo.loja.repository.ItemCarrinhoRepository;
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
 * Spring Data Elasticsearch repository for the {@link ItemCarrinho} entity.
 */
public interface ItemCarrinhoSearchRepository extends ElasticsearchRepository<ItemCarrinho, Long>, ItemCarrinhoSearchRepositoryInternal {}

interface ItemCarrinhoSearchRepositoryInternal {
    Stream<ItemCarrinho> search(String query);

    Stream<ItemCarrinho> search(Query query);

    void index(ItemCarrinho entity);
}

class ItemCarrinhoSearchRepositoryInternalImpl implements ItemCarrinhoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ItemCarrinhoRepository repository;

    ItemCarrinhoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ItemCarrinhoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ItemCarrinho> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<ItemCarrinho> search(Query query) {
        return elasticsearchTemplate.search(query, ItemCarrinho.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ItemCarrinho entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
