package br.com.alura.codechella.api;

import br.com.alura.codechella.domain.service.EventoService;
import br.com.alura.codechella.api.dto.EventoDto;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/eventos")
public class EventoController {

  private final EventoService service;
  private final Sinks.Many<EventoDto> eventoSink;

  @Autowired
  public EventoController(EventoService service) {
    this.service = service;
    this.eventoSink = Sinks.many()
        .multicast() // sincronizando vários novos events e enviando para todos os clientes conectados
        .onBackpressureBuffer(); // controlando como o envio dos events vao acontecer de forma equilibrada,
        // para que nao sobrecarregue o servidor cliente, prevenindo que um consumidor receba uma gama de events
        // muito maior do que ele consegue processar
  }

  // essa anotação TEXT_EVENT_STREAM_VALUE vai servir pra trabalhar com Server-Sent Events
  // que é o que utilizamos quando queremos trabalhar com Event Streaming
  // esse endpoint produz um fluxo de dados, stream de valores, de texto
  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  //flux devolve nenhum ou uma lista de eventos
  public Flux<EventoDto> obterTodos() {
    return service.obterTodos();
  }

  // por estar utilizando Sink, a conexão com cliente se mantem aberta, ela nao é fechada
  @GetMapping(value = "/categoria/{tipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<EventoDto> obterPorTipo(@PathVariable String tipo) {
    // merge = misturar os dados que ele ja esta enviando com o que estiver sendo enviado para o eventoSink
    return Flux.merge(service.obterPorTipo(tipo), eventoSink.asFlux())
        .delayElements(Duration.ofSeconds(4)); // só pra ver o postman retornando os dados
  }

  @GetMapping("/{id}")
  //mono devolve nenhum ou 1 evento
  public Mono<EventoDto> obterPorId(@PathVariable Long id){
    return service.obterPorId(id);
  }

  @GetMapping("/{id}/traduzir/{idioma}")
  public Mono<String> obterTraducao(@PathVariable Long id, @PathVariable String idioma){
    return service.obterTraducao(id, idioma);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<EventoDto> cadastrar(@RequestBody EventoDto dto){
    return service.cadastrar(dto)
        .doOnSuccess(eventoDto -> eventoSink.tryEmitNext(eventoDto)); // se o cadastro
        // foi realizado com sucesso, envie esse dado para o eventoSink
  }

  @DeleteMapping("/{id}")
  public Mono<Void> excluir(@PathVariable Long id){
    return service.excluir(id);
  }

  @PutMapping("/{id}")
  public Mono<EventoDto> atualizar(@PathVariable Long id, @RequestBody EventoDto dto){
    return service.atualizar(id, dto)
        .doOnSuccess(eventoDto -> eventoSink.tryEmitNext(eventoDto));
  }


}
