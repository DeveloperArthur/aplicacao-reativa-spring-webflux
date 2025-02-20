# Aplicação Reativa com Spring WebFlux

Curso excelente, construímos uma aplicação reativa com Spring WebFlux
que realiza operações assíncronas e não bloqueantes, processamento em tempo real
e propagação de mudanças com Event Streaming, Server-Sent Events, utilizando Netty, 
R2DBC, Flyway e Sink

## Stack:
- Netty como servidor de aplicação
- R2DBC para banco de dados relacional, pois suporta operações assíncronas e não bloqueants
- Flyway para apoio ao ORM, pois no Spring Data JPA as anotações criam as tabelas e as colunas, 
mas no reativo não, tem q ser criado através de scripts com Flyway
- Sink para possibilitar a propagação de mudanças no banco de dados

## Processamento em tempo real com propagação de mudanças

Quando um sistema está inscrito em uma fila e outro sistema envia um evento, e o sistema que está 
inscrito consome o evento, isso é processamento em tempo real, o sistema está sendo notificado 
por mudanças de posição em tempo real, não só com broker de eventos, também é possível fazer 
processamento em tempo real com Server-Sent Events

Na nossa aplicação, nós utilizamos Server-Sent Events para fazer o processamento em tempo real,
o [endpoint que lista os eventos por categoria](https://github.com/DeveloperArthur/aplicacao-reativa-spring-webflux/blob/main/src/main/java/br/com/alura/codechella/api/EventoController.java) por exemplo, é uma request que se mantem aberta, 
e ao ser cadastrado um novo evento para aquela categoria, o cliente que enviou a request, e 
está com ela aberta, recebe o novo evento cadastrado também

### Contagem de ingressos em tempo real

A classe [Ingresso.java](https://github.com/DeveloperArthur/aplicacao-reativa-spring-webflux/blob/main/src/main/java/br/com/alura/codechella/domain/Ingresso.java) tem a informação da quantidade de ingressos disponíveis, e a classe
[Venda.java](https://github.com/DeveloperArthur/aplicacao-reativa-spring-webflux/blob/main/src/main/java/br/com/alura/codechella/domain/Venda.java) tem as informações ingressoId e total de ingressos que serão comprados

O [endpoint que lista todos os ingressos](https://github.com/DeveloperArthur/aplicacao-reativa-spring-webflux/blob/main/src/main/java/br/com/alura/codechella/api/IngressoController.java) vai se manter aberta, mostrando a quantidade de ingressos
disponíveis, o [endpoint de compra](https://github.com/DeveloperArthur/aplicacao-reativa-spring-webflux/blob/main/src/main/java/br/com/alura/codechella/api/IngressoController.java) vai realizar o checkout recebendo id do ingresso, ele subtrai
a quantidade de ingressos pela quantidade que será comprada, e atualiza a tabela ingresso

Por causa do Sink, o endpoint de ingressos vai atualizar a quantidade de ingresos disponíveis
ou seja, conforme o cliente for comprando, o site vai mostrando quantos restam...

## Processamento assincrono e não bloqueante

O [endpoint que busca todos os eventos]() utiliza Event Streaming com Server-Sent Events, que 
faz uma [comunicação unilateral em tempo real](https://www.linkedin.com/feed/update/urn:li:ugcPost:7288700306794684417/?commentUrn=urn%3Ali%3Acomment%3A%28ugcPost%3A7288700306794684417%2C7297693732919734272%29&dashCommentUrn=urn%3Ali%3Afsd_comment%3A%287297693732919734272%2Curn%3Ali%3AugcPost%3A7288700306794684417%29), na imagem abaixo podemos ver que o Postman 
abre uma conexão, vai recebendo os dados conforme vão chegando em um fluxo contínuo e fecha a 
conexão quando a busca finaliza

![img](./assets/Captura%20de%20Tela%202025-02-17%20às%2017.20.07.png)
![img](./assets/Captura%20de%20Tela%202025-02-17%20às%2017.20.34.png)

### Consumindo API e gravando registro no banco de forma não bloqueante

No paradigma reativo, a ideia é utilizar um modelo de programação assíncrono e não bloqueante, onde as operações de I/O (como chamadas de rede e acesso a banco de dados) não bloqueiam a execução das threads. Em vez disso, elas utilizam callbacks ou promessas para lidar com a conclusão dessas operações.

1. Escritas no Banco de Dados:

    Em um ambiente reativo, as operações de escrita no banco de dados são feitas de forma assíncrona. Isso significa que, quando você faz uma operação de escrita, ela é iniciada e, em vez de bloquear a thread até que a operação termine, o controle é devolvido imediatamente. Quando a operação de escrita é concluída, um callback é acionado para lidar com o resultado.
    O uso de drivers reativos para bancos de dados, como o R2DBC, permite que essas operações sejam realizadas de forma não bloqueante.

2. Requisições para APIs Externas:

    Similarmente, quando uma requisição é feita a uma API externa, a chamada é feita de forma assíncrona. O controle é devolvido imediatamente após a requisição ser enviada, e a resposta é tratada por meio de callbacks ou fluxos reativos quando chega.
    WebClient, por exemplo, é uma ferramenta em Spring WebFlux que facilita essas chamadas de forma não bloqueante.
    Mesmo que você esteja esperando a resposta para continuar o processamento (o que parece sincrônico), a diferença está em como os recursos do sistema são gerenciados. Em um modelo não bloqueante, o sistema pode continuar a processar outras tarefas enquanto aguarda a conclusão das operações de I/O, tornando-o mais eficiente em termos de uso de recursos.

Essas chamadas assíncronas são incríveis, principalmente porque a thread não fica bloqueada em uma conexão de rede, ela entra em um estado de espera sem consumir recursos, aguardando o callback.

Fonte: https://cursos.alura.com.br/forum/topico-duvida-como-as-escritas-no-banco-de-dados-e-a-integracao-da-api-sao-feitas-de-forma-nao-bloqueante-484190

Outras formas de fazer programação não bloqueante:
- Enviar diversas requests para N servidores diferentes ao mesmo tempo, em um processo sincrono por 
exemplo, a API iria fazer uma request, e aguardar o response para fazer outra request, e em um 
processo assincrono com programação não bloqueante, enquanto uma request é feita, outra já está 
sendo feita também, mesmo sem a resposta da primeira, [exemplo prático: calcula_folha_pagamento.go](https://github.com/DeveloperArthur/golang-first-api-rest/blob/main/service/calcula_folha_pagamento.go)
- [Melhorando uma aplicação com Single-Thread Server](https://github.com/DeveloperArthur/arquitetura-escalabilidade-com-php?tab=readme-ov-file#melhorando-disponibilidade-da-aplica%C3%A7%C3%A3o)

## Outros conteúdos sobre programação reativa
- https://github.com/DeveloperArthur/algoritmos-guias-anotacoes-uteis/blob/main/quarkus%20e%20spring%20web%20flux/quarkus%20e%20spring%20web%20flux.md
