# TaskFlow AI

![CI](https://github.com/hera-JulieOliveira/taskflow-ai/actions/workflows/ci.yml/badge.svg)

Gerenciador de tarefas com assistente de IA generativa.

**Stack:** Java 21, Spring Boot, PostgreSQL, React (Vite), API da Anthropic, Docker, GitHub Actions.

## Funcionalidades
- Cadastro e login com JWT
- Projetos e tarefas por usuário
- Assistente de IA: gerar descrição, sugerir subtarefas e resumir o projeto, com feedback 👍/👎

## Como rodar
1. Banco: `docker compose up -d`
2. API (porta 8080): `cd backend && ./mvnw spring-boot:run`
3. Front (porta 5173): `cd frontend && npm install && npm run dev`

Para usar a IA, defina `ANTHROPIC_API_KEY` no terminal antes de subir a API.

docker compose --profile full up -d --build` (abre em http://localhost:8081).

## Testes
`cd backend && ./mvnw test`

Veja também [docs/AI-WORKFLOW.md](docs/AI-WORKFLOW.md).
