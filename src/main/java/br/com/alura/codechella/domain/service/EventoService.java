package br.com.alura.codechella.domain.service;

import br.com.alura.codechella.infra.database.EventoRepository;
import br.com.alura.codechella.api.dto.EventoDto;
import br.com.alura.codechella.infra.mymemory.MyMemoryTranslationsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventoService {

  @Autowired
  private EventoRepository repository;

  public Flux<EventoDto> obterTodos() {
    return repository.findAll().map(EventoDto::toDto);
  }

  public Mono<EventoDto> obterPorId(Long id) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
        .map(EventoDto::toDto);
  }

  public Mono<EventoDto> cadastrar(EventoDto dto) {
    return repository.save(dto.toEntity())
        .map(EventoDto::toDto);
  }

  public Mono<Void> excluir(Long id) {
    return repository.findById(id)
        .flatMap(repository::delete);
  }

  public Mono<EventoDto> atualizar(Long id, EventoDto dto) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
        .flatMap(eventoExistente -> {
          eventoExistente.setId(id);
          eventoExistente.setTipo(dto.tipo());
          eventoExistente.setNome(dto.nome());
          eventoExistente.setData(dto.data());
          eventoExistente.setDescricao(dto.descricao());
          return repository.save(eventoExistente);
        }).map(EventoDto::toDto);
  }

  public Flux<EventoDto> obterPorTipo(String tipo) {
    return repository.findByTipo(tipo.toUpperCase())
        .map(EventoDto::toDto);
  }

  public Mono<String> obterTraducao(Long id, String idioma) {
    return repository.findById(id)
        .flatMap(e -> MyMemoryTranslationsApi.obterTraducaoMyMemory(e.getDescricao(), idioma));
  }
}
