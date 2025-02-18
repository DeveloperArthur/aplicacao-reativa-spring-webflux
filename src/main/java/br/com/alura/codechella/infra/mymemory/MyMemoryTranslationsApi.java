package br.com.alura.codechella.infra.mymemory;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class MyMemoryTranslationsApi {

  public static Mono<String> obterTraducao(String texto, String idioma){
    WebClient webClient = WebClient.builder()
        .baseUrl("https://api-free.deepl.com/v2/translate")
        .build();

    MultiValueMap<String, String> req = new LinkedMultiValueMap<>();
    req.add("text", texto);
    req.add("target_lang", idioma);

    return webClient.post()
        .header("Authorization", "DeepL-Auth-Key")
        .body(BodyInserters.fromFormData(req))
        .retrieve()
        .bodyToMono(TranslateData.class)
        .map(TranslateData::getTranslatedText);
  }

  public static Mono<String> obterTraducaoMyMemory(String texto, String idioma){
    String ENDPOINT_MYMEMORY = "https://api.mymemory.translated.net/get?q="+texto+"&langpair=pt-br|"+idioma;

    WebClient webClient = WebClient.builder()
        .baseUrl(ENDPOINT_MYMEMORY)
        .build();

    return webClient.get()
        .retrieve()
        .bodyToMono(TranslateData.class)
        .map(TranslateData::getTranslatedText);
  }

}
