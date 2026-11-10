# Finance API

API REST de controle financeiro pessoal com dashboard: saldo por conta, entradas e saídas e gastos por categoria. Feita com Java, Spring Boot, PostgreSQL, Flyway e autenticação JWT.

O projeto foi construído passo a passo com foco em boas práticas de backend: isolamento de dados por usuário, validação de entrada, migrations versionadas e tratamento padronizado de erros.

## Funcionalidades

- Cadastro e login de usuários com token JWT
- Contas (conta corrente, carteira, cartão de crédito e investimento) com saldo inicial
- Categorias de entrada e de saída
- Transações com valor, data, descrição, conta e categoria
- Dashboard com resumo do período, saldo por conta e gastos por categoria (com porcentagem)
- Isolamento total entre usuários: cada pessoa só enxerga e altera os próprios dados

## Tecnologias

- Java 25
- Spring Boot 4 (Web, Data JPA, Validation, Security, OAuth2 Resource Server)
- PostgreSQL 16 (via Docker Compose)
- Flyway para versionamento do schema
- Maven

## Pré-requisitos

- JDK 25
- Maven (ou o wrapper `mvnw` do projeto)
- Docker e Docker Compose
- OpenSSL (apenas para gerar o segredo do JWT)

## Configuração

A aplicação lê dois valores sensíveis de variáveis de ambiente. Nenhum segredo fica no código nem no repositório.

| Variável | Descrição |
| --- | --- |
| `DB_PASSWORD` | Senha do usuário `finance` no PostgreSQL |
| `JWT_SECRET` | Chave de assinatura dos tokens (HS256), com no mínimo 32 caracteres |

Outras propriedades, em `src/main/resources/application.yaml`:

| Propriedade | Padrão | Descrição |
| --- | --- | --- |
| `app.jwt.expiration-minutes` | `60` | Validade do token de acesso, em minutos |

## Como executar

1. Clone o repositório:

```bash
git clone https://github.com/GuiVasconcelosDev/finance-api.git
cd finance-api
```

2. Defina a senha do banco. O Docker Compose lê o arquivo `.env`, e a aplicação lê a variável de ambiente, então o valor precisa ser o mesmo nos dois:

```bash
echo "DB_PASSWORD=troque_por_uma_senha_forte" > .env
export DB_PASSWORD=troque_por_uma_senha_forte
```

3. Gere o segredo do JWT:

```bash
export JWT_SECRET=$(openssl rand -base64 48)
```

4. Suba o banco de dados (porta local `5433`):

```bash
docker compose up -d
```

5. Inicie a aplicação:

```bash
mvn spring-boot:run
```

A API fica disponível em `http://localhost:8080`. As tabelas são criadas automaticamente pelo Flyway na primeira execução.

Observações:

- O PostgreSQL grava a senha apenas na primeira criação do volume. Se trocar a `DB_PASSWORD` depois, é preciso recriar o volume (`docker compose down -v`, o que apaga os dados) ou alterar a senha dentro do banco.
- As variáveis de ambiente valem só para o terminal em que foram definidas. Para não repetir, guarde os `export` em um arquivo local fora do Git (por exemplo `.env.local`, já ignorado) e carregue com `source .env.local`.

## Endpoints

Todas as rotas, exceto cadastro e login, exigem o header `Authorization: Bearer <token>`.

### Autenticação

| Método | Caminho | Descrição |
| --- | --- | --- |
| POST | `/api/auth/register` | Cria um usuário |
| POST | `/api/auth/login` | Devolve o token de acesso |
| GET | `/api/me` | Dados do usuário autenticado |

### Contas

| Método | Caminho | Descrição |
| --- | --- | --- |
| GET | `/api/accounts` | Lista as contas do usuário |
| POST | `/api/accounts` | Cria uma conta |
| GET | `/api/accounts/{id}` | Busca uma conta |
| PUT | `/api/accounts/{id}` | Atualiza nome, tipo e saldo inicial |
| DELETE | `/api/accounts/{id}` | Apaga a conta (409 se houver transações) |

### Categorias

| Método | Caminho | Descrição |
| --- | --- | --- |
| GET | `/api/categories` | Lista as categorias do usuário |
| POST | `/api/categories` | Cria uma categoria (`EXPENSE` ou `INCOME`) |
| GET | `/api/categories/{id}` | Busca uma categoria |
| PUT | `/api/categories/{id}` | Renomeia a categoria (o tipo não muda) |
| DELETE | `/api/categories/{id}` | Apaga a categoria (409 se houver transações) |

### Transações

| Método | Caminho | Descrição |
| --- | --- | --- |
| GET | `/api/transactions` | Lista as transações do período |
| POST | `/api/transactions` | Cria uma transação |
| GET | `/api/transactions/{id}` | Busca uma transação |
| PUT | `/api/transactions/{id}` | Atualiza uma transação |
| DELETE | `/api/transactions/{id}` | Apaga uma transação |

A listagem aceita os parâmetros `from` e `to` (formato `yyyy-MM-dd`). Sem eles, usa o mês atual.

### Dashboard

| Método | Caminho | Descrição |
| --- | --- | --- |
| GET | `/api/dashboard/summary` | Total de entradas, de saídas e saldo do período |
| GET | `/api/dashboard/balances` | Saldo atual de cada conta e saldo total |
| GET | `/api/dashboard/by-category` | Total por categoria, em valor e porcentagem |

Parâmetros: `from` e `to` (padrão: mês atual) em `summary` e `by-category`, e `type` (`EXPENSE` ou `INCOME`, padrão `EXPENSE`) em `by-category`.

## Exemplo de uso

Cadastro e login:

```bash
curl -X POST localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Gui","email":"gui@exemplo.com","password":"senhaforte123"}'

curl -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"gui@exemplo.com","password":"senhaforte123"}'
```

O login devolve um JSON com o campo `accessToken`. Use o valor nas próximas chamadas:

```bash
TOKEN="cole_o_token_aqui"

curl -X POST localhost:8080/api/accounts \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Nubank","type":"CHECKING","initialBalance":1000}'

curl -X POST localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"accountId":"<id-da-conta>","type":"EXPENSE","amount":19.90,"description":"Lanche","transactionDate":"2026-11-10"}'

curl localhost:8080/api/dashboard/summary -H "Authorization: Bearer $TOKEN"
```

## Respostas de erro

Os erros seguem o formato Problem Details (RFC 9457).

| Status | Quando acontece |
| --- | --- |
| 400 | Dados inválidos (campos obrigatórios, formato, limites) |
| 401 | Token ausente, inválido ou expirado, ou credenciais incorretas |
| 404 | Recurso inexistente ou que pertence a outro usuário |
| 409 | Conflito (e-mail já cadastrado, nome de categoria repetido, exclusão de conta ou categoria com transações) |
| 422 | Regra de negócio violada (por exemplo, categoria de tipo diferente do da transação, ou período com data inicial depois da final) |

## Modelo de dados

O schema é criado pela migration `src/main/resources/db/migration/V1__create_initial_schema.sql`.

- `users`: usuários, com senha armazenada apenas como hash
- `accounts`: contas do usuário, com tipo e saldo inicial
- `categories`: categorias de entrada ou saída
- `transactions`: lançamentos, ligados a um usuário, uma conta e, opcionalmente, uma categoria

Decisões de modelagem:

- Valores monetários usam `NUMERIC(15,2)` no banco e `BigDecimal` no Java, nunca `double`
- O valor de uma transação é sempre positivo, e o campo `type` indica se é entrada ou saída (com `CHECK` no banco)
- Os identificadores são UUID, que não podem ser adivinhados por sequência
- Existe um índice em `transactions (user_id, transaction_date)` para as consultas do dashboard
- O Hibernate roda com `ddl-auto: validate`: quem altera o schema é o Flyway

## Segurança

- Senhas protegidas com BCrypt
- Autenticação stateless com JWT assinado (HS256), com validade configurável
- Política de negar por padrão: qualquer rota nova já exige autenticação
- O identificador do usuário vem sempre do token, nunca do corpo da requisição
- Todo acesso a dados passa por consultas filtradas pelo dono (`findByIdAndUserId`). Recurso de outra pessoa responde 404, sem confirmar que existe
- Referências no corpo da requisição (conta e categoria de uma transação) também são validadas contra o dono
- O login devolve a mesma mensagem para e-mail inexistente e senha errada, e executa uma verificação de hash nos dois casos, para evitar enumeração de usuários e ataque por tempo de resposta
- Validação de entrada com Bean Validation em todos os DTOs
- Segredos apenas em variáveis de ambiente

## Testes

```bash
mvn test
```

Os testes de integração usam o banco configurado na aplicação, então o PostgreSQL precisa estar no ar e as variáveis `DB_PASSWORD` e `JWT_SECRET` precisam estar definidas. Os testes que gravam dados são transacionais e desfazem as alterações ao final.

## Estrutura do projeto

```
src/main/java/com/guivasconcelos/finance_api/
  config/        configuração de segurança e propriedades do JWT
  controller/    endpoints REST
  dto/           objetos de entrada e saída da API
  exception/     exceções e tratamento global de erros
  model/         entidades JPA e enums
  repository/    repositórios Spring Data JPA
  service/       regras de negócio
src/main/resources/
  application.yaml
  db/migration/  migrations do Flyway
```

## Roadmap

- Testes automatizados com MockMvc e Testcontainers
- Limite de tentativas no login (rate limit)
- Refresh token e revogação
- Configuração de CORS
- Integração com o Meu Pluggy para importar dados bancários
- Frontend em React

## Autor

Guilherme Vasconcelos (GuiVasconcelosDev)

GitHub: https://github.com/GuiVasconcelosDev
