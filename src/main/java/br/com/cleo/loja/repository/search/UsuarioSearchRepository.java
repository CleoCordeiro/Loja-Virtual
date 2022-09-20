package br.com.cleo.loja.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.cleo.loja.domain.Usuario;
import br.com.cleo.loja.repository.UsuarioRepository;
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
 * Spring Data Elasticsearch repository for the {@link Usuario} entity.
 */
public interface UsuarioSearchRepository extends ElasticsearchRepository<Usuario, Long>, UsuarioSearchRepositoryInternal {}

interface UsuarioSearchRepositoryInternal {
    Stream<Usuario> search(String query);

    Stream<Usuario> search(Query query);

    void index(Usuario entity);
}

class UsuarioSearchRepositoryInternalImpl implements UsuarioSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final UsuarioRepository repository;

    UsuarioSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, UsuarioRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Usuario> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Usuario> search(Query query) {
        return elasticsearchTemplate.search(query, Usuario.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Usuario entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
