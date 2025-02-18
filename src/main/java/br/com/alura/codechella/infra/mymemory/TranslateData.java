package br.com.alura.codechella.infra.mymemory;

public record TranslateData(ResponseData responseData) {

  public String getTranslatedText() {
    return responseData.translatedText();
  }
}
