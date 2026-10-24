# Expense Tracker MCP

API REST de controle de despesas em Java e Spring Boot, desenvolvida para ser exposta a agentes de IA por meio de um servidor **MCP (Model Context Protocol)**.

> **Status: em desenvolvimento.** A API REST está pronta e testada. O servidor MCP e a trilha de auditoria estão no roadmap abaixo.

## Por que este projeto existe

Agentes de IA só são úteis no mundo real quando conseguem chamar ferramentas. Este projeto explora esse caminho com um domínio financeiro simples:

1. Uma API de despesas com regras de negócio e persistência.
2. Um servidor MCP que expõe operações dessa API como ferramentas para agentes.
3. Governança: cada chamada de ferramenta gera um evento auditável.

## Tecnologias

- Java 17 e Spring Boot 4.1.2
- Spring AI 2.0.0 (servidor MCP)
- Spring Web, Spring Data JPA e Bean Validation
- H2 em memória (desenvolvimento)
- JUnit 5 e Mockito
- Kafka, MySQL e Docker Compose: no roadmap

## Arquitetura

```
controller  ->  service  ->  repository  ->  banco de dados
   (REST)     (regras)       (JPA)
```

- DTOs (`record`) separam o contrato da API da entidade.
- Valores monetários usam `BigDecimal`, nunca `double`, para evitar erros de arredondamento.
- Tratamento global de exceções com `@RestControllerAdvice`.

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

## Como executar

Pré-requisito: JDK compatível com a versão definida no `pom.xml`.

```bash
./mvnw spring-boot:run   # sobe a API em http://localhost:8080
./mvnw test              # executa os testes
```

## Roadmap

- [x] API REST de despesas com validação e testes unitários
- [ ] Servidor MCP com ferramentas: registrar despesa, listar despesas e resumo por categoria
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
