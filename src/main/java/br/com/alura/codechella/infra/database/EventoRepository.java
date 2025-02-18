package br.com.alura.codechella.infra.database;

import br.com.alura.codechella.domain.Evento;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EventoRepository extends ReactiveCrudRepository<Evento, Long> {
  Flux<Evento> findByTipo(String upperCase);
}
