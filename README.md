# Finance API

> **Projeto pessoal em construção**
>
> Este repositório documenta um projeto individual de estudo e desenvolvimento. A API ainda está em construção: recursos, contratos e decisões técnicas podem mudar, e não há garantia de estabilidade ou uso em produção.

API REST para controle de finanças pessoais, desenvolvida com Java e Spring Boot.

## Funcionalidades atuais

- Cadastro e autenticação de usuários com JWT
- Gerenciamento de contas, categorias e transações
- Resumo financeiro, saldos e totais por categoria
- Persistência com PostgreSQL e migrações Flyway
- Testes automatizados, incluindo testes de integração

## Tecnologias

- Java 25 e Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security e Bean Validation
- PostgreSQL 16 e Flyway
- Maven
- Docker Compose e Testcontainers

## Executar localmente

### Pré-requisitos

- JDK 25
- Maven
- Docker com Docker Compose

### Configuração

1. Crie seu arquivo local de variáveis a partir do exemplo:

   ```bash
   cp .env.example .env
   ```

2. Edite `.env` e substitua os valores de exemplo por valores locais. O segredo JWT deve ter pelo menos 32 caracteres.

3. Exporte as variáveis para os processos iniciados neste terminal:

   ```bash
   set -a
   source .env
   set +a
   ```

4. Inicie o banco de dados:

   ```bash
   docker compose up -d db
   ```

5. Execute a aplicação:

   ```bash
   mvn spring-boot:run
   ```

A API ficará disponível em `http://localhost:8080`. As migrações do banco são aplicadas pelo Flyway.

## Testes

```bash
mvn test
```

Os testes de integração podem precisar do Docker em execução.

## Endpoints principais

| Recurso | Rotas |
| --- | --- |
| Autenticação | `POST /api/auth/register`, `POST /api/auth/login` |
| Usuário autenticado | `GET /api/me` |
| Contas | `/api/accounts` |
| Categorias | `/api/categories` |
| Transações | `/api/transactions` |
| Dashboard | `GET /api/dashboard/summary`, `GET /api/dashboard/balances`, `GET /api/dashboard/by-category` |

As rotas protegidas exigem um token no cabeçalho `Authorization: Bearer <token>`.

## Segurança

- Não versione `.env` nem compartilhe segredos.
- Use credenciais diferentes para desenvolvimento e produção.
- O arquivo `.env.example` contém apenas valores ilustrativos; substitua-os no arquivo `.env` local.

## Autor

Guilherme Vasconcelos — [@GuiVasconcelosDev](https://github.com/GuiVasconcelosDev)