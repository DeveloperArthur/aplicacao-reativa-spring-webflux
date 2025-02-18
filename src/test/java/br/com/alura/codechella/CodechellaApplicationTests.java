package br.com.alura.codechella;

import br.com.alura.codechella.api.dto.EventoDto;
import br.com.alura.codechella.domain.TipoEvento;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CodechellaApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void cadastraNovoEvento() {
		EventoDto dto = new EventoDto(null, TipoEvento.SHOW, "Kiss",
				LocalDate.parse("2025-01-01"), "Show da melhor banda que existe");

		webTestClient.post()
				.uri("/eventos")
				.bodyValue(dto)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(EventoDto.class)
				.value(response -> {
					Assertions.assertNotNull(response.id());
					Assertions.assertEquals(dto.tipo(), response.tipo());
					Assertions.assertEquals(dto.nome(), response.nome());
					Assertions.assertEquals(dto.data(), response.data());
					Assertions.assertEquals(dto.descricao(), response.descricao());
				});
	}

	@Test
	void buscarEvento() {
		EventoDto dto = new EventoDto(13L, TipoEvento.SHOW, "The Weeknd",
				LocalDate.parse("2025-11-02"), "Um show eletrizante ao ar livre com muitos efeitos especiais.");

		webTestClient.get()
				.uri("/eventos")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(EventoDto.class)
				.value(response -> {
					EventoDto eventoResponse = response.get(12);
					Assertions.assertEquals(dto.id(), eventoResponse.id());
					Assertions.assertEquals(dto.tipo(), eventoResponse.tipo());
					Assertions.assertEquals(dto.nome(), eventoResponse.nome());
					Assertions.assertEquals(dto.data(), eventoResponse.data());
					Assertions.assertEquals(dto.descricao(), eventoResponse.descricao());
				});
	}

}
