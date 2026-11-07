# Expense Tracker MCP

API REST de controle de despesas em Java e Spring Boot, desenvolvida para ser exposta a agentes de IA por meio de um servidor **MCP (Model Context Protocol)**.

> **Status: em desenvolvimento.** A API REST, o servidor MCP com ferramentas e o frontend em React estão prontos e testados. Trilha de auditoria (Kafka), controle de acesso (Spring Security) e persistência definitiva estão no roadmap abaixo.

## Por que este projeto existe

Agentes de IA só são úteis no mundo real quando conseguem chamar ferramentas. Este projeto explora esse caminho com um domínio financeiro simples:

1. Uma API de despesas com regras de negócio e persistência.
2. Um servidor MCP que expõe operações dessa API como ferramentas para agentes.
3. Governança: cada chamada de ferramenta gera um evento auditável.

## Tecnologias

- Java 17 e Spring Boot 4.1.2
- Spring AI 2.0.0 (servidor MCP)
- React, TypeScript e Vite
- Spring Web, Spring Data JPA e Bean Validation
- H2 em memória (desenvolvimento)
- JUnit 5 e Mockito
- Kafka, MySQL e Docker Compose: no roadmap

## Arquitetura

```
Frontend (React + TS) ----> Controller (REST) ---\
                                                 +--> Service (regras) --> Repository (JPA) --> Banco de dados (H2)
AI Agent (MCP Client) ----> ExpenseTools (MCP) --/
```

- DTOs (`record`) separam o contrato da API da entidade.
- Valores monetários usam `BigDecimal`, nunca `double`, para evitar erros de arredondamento.
- Tratamento global de exceções com `@RestControllerAdvice`.
- O servidor MCP (`ExpenseTools`) reutiliza a mesma camada de serviço (`ExpenseService`), garantindo validações e regras de negócio consistentes tanto para o frontend quanto para agentes de IA.
- Normalização automática de categorias: na criação e na filtragem, categorias recebem tratamento (remoção de espaços nas pontas e primeira letra maiúscula, ex.: `"  TRANSPORTE "` vira `"Transporte"`), prevenindo fragmentação no resumo.

## Endpoints

| Método | Rota                    | Descrição                          |
|--------|-------------------------|------------------------------------|
| POST   | `/expenses`             | Registra uma despesa               |
| GET    | `/expenses`             | Lista despesas (`?category=` opcional) |
| GET    | `/expenses/summary`     | Total gasto por categoria          |

Exemplo:

```bash
curl -X POST http://localhost:8080/expenses \
  -H "Content-Type: application/json" \
  -d '{"description":"Uber","amount":42.00,"category":"Transporte","date":"2026-10-24"}'

curl http://localhost:8080/expenses/summary
```

## Servidor MCP

- **Endpoint:** `http://localhost:8080/mcp`
- **Transporte:** Streamable-HTTP (`STREAMABLE`)

### Ferramentas registradas:

1. `registerExpense`: Registra uma nova despesa (parâmetros: `description`, `amount`, `category`, `date` opcional em `yyyy-MM-dd`).
2. `listExpenses`: Retorna até 50 despesas mais recentes (parâmetro: `category` opcional).
3. `summarizeByCategory`: Retorna o total consolidado gasto por categoria (sem parâmetros).

## Como executar

Pré-requisito: JDK compatível com a versão definida no `pom.xml`.

```bash
./mvnw spring-boot:run   # sobe a API em http://localhost:8080
./mvnw test              # executa os testes
```

## Frontend

Para rodar a aplicação:
- Backend: execute `./mvnw spring-boot:run` na raiz do projeto (sobe em http://localhost:8080).
- Frontend: execute `cd frontend && npm install && npm run dev` (acessar http://localhost:5173).

## Roadmap

- [x] API REST de despesas com validação e testes unitários
- [x] Frontend básico
- [x] Servidor MCP com ferramentas: registrar despesa, listar despesas e resumo por categoria
- [ ] Teste do servidor MCP com um agente de IA
- [ ] Auditoria de chamadas de ferramentas via Kafka
- [ ] Controle de acesso (Spring Security e JWT) e lista de ferramentas permitidas
- [ ] MySQL e Docker Compose
- [ ] Pipeline de CI

## Decisões e limitações

- O projeto usa H2 em memória por enquanto, então os dados são perdidos ao reiniciar.
- Partes do código foram geradas com apoio de agentes de IA e revisadas manualmente.

## Autor

Maycon Vyctor Rodrigues Costa

[GitHub](https://github.com/MayconVyctor) · [LinkedIn](https://linkedin.com/in/maycon-vyctor-dev)
