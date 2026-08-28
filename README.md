# Personal Finance API

API REST para controle financeiro desenvolvida com **Java 17 e Spring Boot**, com autenticação JWT, gerenciamento de categorias, registro e cancelamento de transações, relatórios financeiros e envio de notificações por email.

O projeto utiliza um serviço principal para as operações financeiras e um serviço separado para notificações, com ambiente local reproduzível através do Docker Compose.

## Funcionalidades

- Cadastro e autenticação de usuários com JWT
- Renovação de autenticação com Refresh Token
- CRUD de categorias
- Registro de receitas, despesas e saldo inicial
- Cancelamento lógico de transações
- Status de transações (`COMPLETED` e `CANCELED`)
- Filtros por categoria, tipo, período e vencimento
- Paginação de transações
- Relatórios financeiros por intervalo de datas
- Relatórios considerando apenas transações concluídas
- Envio de relatórios por email
- Envio mensal automático de relatórios
- Rate Limiting com Bucket4j
- Estado compartilhado do Rate Limiting com Hazelcast
- Comunicação entre serviços com OpenFeign
- Versionamento do banco de dados com Flyway
- Índices para otimização das principais consultas
- Documentação com Swagger/OpenAPI
- Collection do Postman para demonstração da API
- Testes automatizados
- Ambiente reproduzível com Docker Compose
- Integração contínua com GitHub Actions

## Tecnologias

### Back-end

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Spring Cache
- Spring Cloud OpenFeign

### Banco de dados

- PostgreSQL
- Flyway

### Segurança e infraestrutura

- JWT
- Refresh Token
- Bucket4j
- Hazelcast
- Docker
- Docker Compose
- Mailpit

### Desenvolvimento e testes

- Gradle
- MapStruct
- Lombok
- Swagger / OpenAPI
- Postman
- JUnit 5
- Mockito
- MockMvc
- GitHub Actions

## Arquitetura

```text
                    ┌──────────────────┐
                    │      Client      │
                    └────────┬─────────┘
                             │ HTTP
                             ▼
                  ┌─────────────────────┐
                  │ Transaction Service │
                  └───────┬──────┬──────┘
                          │      │
                          │      │ OpenFeign
                          │      ▼
                          │  ┌──────────────────────┐
                          │  │ Notification Service │
                          │  └──────────┬───────────┘
                          │             │ SMTP
                          ▼             ▼
                   ┌────────────┐   ┌─────────┐
                   │ PostgreSQL │   │ Mailpit │
                   └────────────┘   └─────────┘
```

O **Transaction Service** concentra as regras relacionadas a usuários, autenticação, categorias, transações e relatórios.

O envio de emails é delegado ao **Transaction Notification Service** através de requisições HTTP utilizando OpenFeign.

Durante o desenvolvimento local, os emails são enviados para o **Mailpit**, permitindo testar o fluxo sem utilizar um provedor SMTP externo.

## Pré-requisitos

Para executar o projeto com Docker Compose:

- Git
- Docker
- Docker Compose

Não é necessário instalar PostgreSQL ou Mailpit manualmente.

## Como executar

Clone o repositório:

```bash
git clone https://github.com/guuhf/transaction-service.git
cd transaction-service
```

Crie o arquivo `.env` a partir do exemplo.

### Windows

```bash
copy .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

Suba o ambiente:

```bash
docker compose up -d --build
```

Verifique os containers:

```bash
docker compose ps
```

API:

```text
http://localhost:8084
```

Mailpit:

```text
http://localhost:8025
```

## Variáveis de ambiente

Exemplo:

```env
DB_NAME=db_transactions
DB_USER=postgres
DB_PASSWORD=postgres

JWT_SECRET_KEY=change-this-secret-key-before-production

EMAIL_FROM=no-reply@transaction-service.local
EMAIL_PERSONAL_NAME=Transaction Service
```

> Os valores acima são destinados ao ambiente local. Credenciais e chaves reais não devem ser armazenadas no repositório.

## Autenticação

A API utiliza JWT para autenticação.

Fluxo:

```text
Cadastro
   ↓
Login
   ↓
Access Token + Refresh Token
   ↓
Access Token nas requisições protegidas
   ↓
Access Token expira
   ↓
Refresh Token
   ↓
Novo Access Token
```

Endpoints protegidos devem receber:

```http
Authorization: Bearer <access_token>
```

Para renovar o Access Token:

```http
POST /auth/refresh-token
```

## Transações

A API suporta:

- `INCOME`
- `EXPENSE`
- `OPENINGBALANCE`

Toda nova transação é criada com status:

```text
COMPLETED
```

Uma transação pode ser cancelada:

```text
COMPLETED
    │
    │ PATCH /transactions/{id}/cancel
    ▼
CANCELED
```

O cancelamento não remove o registro do banco. A transação permanece disponível para histórico, mas deixa de participar dos cálculos dos relatórios.

## Paginação

A listagem de transações retorna uma resposta simplificada:

```json
{
  "content": [],
  "page": 0,
  "size": 15,
  "totalElements": 0,
  "totalPages": 0
}
```

Isso evita expor diretamente toda a estrutura interna de paginação do Spring Data.

## Filtros

A listagem de transações aceita filtros como:

- `categoryId`
- `transactionType`
- `initialDate`
- `finalDate`
- `initialDueDate`
- `finalDueDate`

Exemplo:

```http
GET /transactions?page=0&categoryId=1&transactionType=EXPENSE
```

Todos os filtros são aplicados considerando o usuário autenticado.

## Relatórios financeiros

O endpoint de relatório recebe um intervalo de datas:

```http
GET /transactions/report?initialDate=2026-08-01T00:00:00&finalDate=2026-08-27T23:59:59
```

O relatório calcula:

- total de receitas;
- total de despesas;
- saldo inicial;
- saldo final;
- quantidade de transações;
- valores agrupados por categoria.

Somente transações com status `COMPLETED` entram nos cálculos.

O intervalo máximo permitido é de **90 dias**.

Após a geração do relatório, o Transaction Service solicita ao Notification Service o envio do resultado por email.

## Categorias

Cada categoria pertence ao usuário que a criou.

O banco impede categorias duplicadas para o mesmo usuário de forma case-insensitive. Assim, nomes como:

```text
Alimentação
alimentação
ALIMENTAÇÃO
```

são considerados equivalentes para o mesmo usuário.

## Rate Limiting

A API utiliza **Bucket4j** para controlar a quantidade de requisições realizadas nos endpoints.

O **Hazelcast** é utilizado como armazenamento compartilhável do estado do Rate Limiting, permitindo que diferentes instâncias da aplicação possam compartilhar os mesmos contadores em um cenário de escalabilidade horizontal.

## Banco de dados e Flyway

O PostgreSQL é utilizado como banco principal e o schema é versionado através do Flyway.

Novas mudanças estruturais são adicionadas através de novas migrations, evitando alterações manuais no banco.

Além das constraints de integridade, a tabela de transações possui índices voltados aos principais padrões de consulta, como:

```text
(USER_ID, DATE)
(USER_ID, CATEGORY_ID)
(USER_ID, DUE_DATE)
(USER_ID, TRANSACTION_STATUS, DATE)
```

## Swagger / OpenAPI

Com a aplicação em execução:

```text
http://localhost:8084/swagger-ui.html
```

A interface permite visualizar endpoints, contratos de request/response e testar as requisições.

## Postman Collection

Uma collection pronta para testar e demonstrar a API está disponível em:

[postman/transaction-service.postman_collection.json](postman/transaction-service.postman_collection.json)

### Importando no Postman

1. Abra o Postman.
2. Clique em **Import**.
3. Selecione `postman/transaction-service.postman_collection.json`.
4. Confirme que a variável `baseUrl` está definida como `http://localhost:8084`.
5. Execute as requisições na ordem sugerida abaixo.

### Fluxo sugerido

```text
Register
   ↓
Login
   ↓
Create Category
   ↓
Create Transaction
   ↓
List Transactions
   ↓
Cancel Transaction
   ↓
Generate Report
```

A collection utiliza variáveis para evitar copiar valores manualmente:

| Variável | Função |
|---|---|
| `baseUrl` | URL base da API |
| `accessToken` | JWT utilizado nos endpoints protegidos |
| `refreshToken` | Token utilizado para renovar a autenticação |
| `categoryId` | ID da categoria utilizada nos testes |
| `transactionId` | ID utilizado no cancelamento |
| `page` | Página utilizada na listagem |

Ao executar **Login**, a collection salva automaticamente `accessToken` e `refreshToken`.

## Endpoints principais

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth` | Registra um novo usuário |
| POST | `/auth/login` | Autentica o usuário |
| POST | `/auth/refresh-token` | Gera um novo Access Token |

### Usuário

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/user` | Retorna os dados do usuário autenticado |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/transactions/category` | Cria uma categoria |
| GET | `/transactions/category` | Lista as categorias do usuário |
| PUT | `/transactions/category/{id}` | Atualiza uma categoria |
| DELETE | `/transactions/category/{id}` | Remove uma categoria |

### Transações

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/transactions` | Registra uma nova transação |
| GET | `/transactions?page={page}` | Lista transações com filtros e paginação |
| PATCH | `/transactions/{id}/cancel` | Cancela uma transação |

### Relatórios

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/transactions/report` | Gera um relatório financeiro e solicita seu envio por email |

## Testes

Para executar os testes:

```bash
./gradlew test
```

Para executar o build completo:

```bash
./gradlew clean build
```

O projeto possui testes para regras de autenticação, usuários, categorias, transações, relatórios, controllers e mappers.

## Integração contínua

O projeto utiliza GitHub Actions para validar alterações automaticamente.

Fluxo:

```text
Push / Pull Request
        ↓
Checkout
        ↓
Java 17
        ↓
Gradle
        ↓
Build + Tests
        ↓
Success / Failure
```

## Decisões técnicas

### JWT e Refresh Token

JWT permite autenticação stateless. O Refresh Token permite utilizar Access Tokens de menor duração sem exigir um novo login sempre que o token expirar.

### PostgreSQL

Foi escolhido por se adequar à natureza relacional dos dados financeiros e oferecer transações, constraints e índices.

### Flyway

Mantém as alterações do schema versionadas junto ao código e reproduzíveis entre ambientes.

### MapStruct

Reduz código repetitivo no mapeamento entre entidades e DTOs utilizando código gerado em tempo de compilação.

### OpenFeign

Abstrai a comunicação HTTP entre o Transaction Service e o Notification Service.

### Cancelamento lógico

Transações canceladas permanecem armazenadas para preservar o histórico, mas deixam de participar dos relatórios.

### Paginação

Evita carregar todas as transações de um usuário em uma única requisição e fornece apenas os metadados necessários para navegação.

## Objetivo do projeto

O objetivo é aplicar conceitos utilizados no desenvolvimento de aplicações back-end com Java e Spring Boot, incluindo:

- APIs REST;
- autenticação e autorização;
- regras de negócio;
- modelagem relacional;
- Spring Data JPA;
- PostgreSQL;
- Flyway;
- índices;
- DTOs;
- MapStruct;
- Bean Validation;
- tratamento de exceções;
- paginação;
- testes automatizados;
- comunicação entre serviços;
- tarefas agendadas;
- relatórios;
- notificações;
- Rate Limiting;
- Docker;
- documentação de APIs;
- integração contínua.
