package br.com.alura.codechella.infra.database;

import br.com.alura.codechella.domain.Ingresso;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IngressoRepository extends ReactiveCrudRepository<Ingresso, Long> {
}