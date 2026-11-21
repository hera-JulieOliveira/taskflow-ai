# Como usei IA no desenvolvimento

Usei o Claude como par de programação em todas as fases do TaskFlow AI.
Regra principal: **a IA propõe, eu valido**. Nada entra sem eu rodar, testar e entender.

## Onde a IA ajudou

| Tarefa | Como usei | Resultado |
|---|---|---|
| Estrutura do projeto | Pedi arquitetura em camadas (controller, service, repository, DTO) | Base organizada desde o início |
| Testes | Pedi testes para `TaskService`, `AiFeedbackService` e `RateLimitFilter` | 10 testes novos, sem custo e sem depender do banco |
| Revisão de código | Pedi revisão com checklist (segurança, erros, bugs, desempenho) | 7 achados, 3 corrigidos |
| Refatoração | Corrigi o handler de erros e o log da IA a partir da revisão | Menos erros 500 indevidos e falhas rastreáveis |
| Diagnóstico | Usei a IA para ler logs e propor hipóteses | Ver abaixo |

## Onde a IA errou (e o que eu fiz)

1. **Rate limit "não funcionava".** As primeiras hipóteses da IA (API desatualizada, filtro não registrado) estavam erradas. A causa real era que o arquivo do filtro nunca tinha sido criado. Resolvi **verificando os fatos** (`ls` na pasta) em vez de seguir as suposições.
2. **Erro de autenticação da API de IA.** Diagnostiquei por eliminação: um `curl` direto na API separou "problema da chave" de "problema do meu código". Descobri que a variável guardava o texto errado (não era a chave) e, depois, que faltava crédito na conta.
3. **Pacotes do Spring Boot 4.** O import do `@WebMvcTest` mudou de pacote nessa versão. Validei compilando e rodando os testes antes de aceitar o código.
4. **Falha de revisão.** O handler genérico escondia erros sem log e devolvia 500 para JSON inválido. Só apareceu na revisão com checklist, não na primeira versão.

## Cuidados que tomei

- **Segredos:** a chave de API fica em variável de ambiente, fora do repositório (`grep` antes de cada commit).
- **Privacidade (LGPD):** só título, descrição e nomes de tarefas vão ao modelo; nunca e-mail ou senha. O feedback 👍/👎 não guarda o texto gerado.
- **Custo e abuso:** limite de chamadas por usuário, `max_tokens` limitado e entrada com tamanho máximo.
- **Prompt injection:** instruções no prompt do sistema; texto do usuário tratado como dado.
- **Sempre revisar:** a interface avisa que o conteúdo é gerado por IA e o usuário decide se usa.

## Backlog técnico (achados da revisão que deixei para depois)

- Limpar contadores antigos do rate limit em memória (ou usar Redis).
- Rate limit por IP real quando houver proxy (`X-Forwarded-For` configurado com segurança).
- Trocar `ddl-auto: update` por migrações com Flyway.
- Token em cookie `HttpOnly` em vez de `localStorage`.
