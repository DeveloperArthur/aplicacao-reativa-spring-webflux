package br.com.alura.codechella.infra.database;

import br.com.alura.codechella.domain.Venda;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface VendaRepository extends ReactiveCrudRepository<Venda, Long> {
}