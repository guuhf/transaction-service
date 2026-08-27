# Transaction Service

API REST para controle financeiro pessoal, com autenticação JWT, gerenciamento de categorias, registro de transações, geração de relatórios financeiros e envio de notificações por email.

O projeto utiliza um serviço principal responsável pelas operações financeiras e um serviço separado para notificações, com ambiente local executável através do Docker Compose.

## Funcionalidades

* Cadastro e autenticação de usuários com JWT
* Renovação de autenticação com Refresh Token
* CRUD de categorias
* Registro de receitas, despesas e saldo inicial
* Filtros de transações por categoria, tipo e período
* Geração de relatórios financeiros por intervalo de datas
* Envio de relatórios por email
* Rate Limiting com Bucket4j
* Suporte a Rate Limiting distribuído através do Hazelcast
* Comunicação entre serviços com OpenFeign
* Versionamento e execução de migrações do banco de dados com Flyway
* Ambiente local com PostgreSQL e Mailpit
* Documentação da API com Swagger/OpenAPI
* Ambiente reproduzível com Docker Compose
* Integração contínua com GitHub Actions

## Tecnologias

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* JWT
* OpenFeign
* MapStruct
* Bucket4j
* Hazelcast
* Docker
* Docker Compose
* Mailpit
* Gradle
* GitHub Actions
* Swagger / OpenAPI

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
                          │      │ OpenFeign
                          │      ▼
                          │  ┌──────────────────────────┐
                          │  │ Notification Service     │
                          │  └────────────┬─────────────┘
                          │               │ SMTP
                          ▼               ▼
                   ┌────────────┐    ┌─────────┐
                   │ PostgreSQL │    │ Mailpit │
                   └────────────┘    └─────────┘
```

O **Transaction Service** concentra as regras relacionadas a usuários, categorias, transações e relatórios financeiros.

O envio de emails é delegado ao **Transaction Notification Service**, com comunicação HTTP realizada através do **OpenFeign**.

Durante o desenvolvimento local, o serviço de notificações envia os emails para o **Mailpit**, permitindo testar o fluxo sem utilizar um provedor de email real.

A estrutura do banco de dados é gerenciada pelo **Flyway**, responsável por aplicar e versionar as migrations do PostgreSQL.

## Rate Limiting

A API possui controle de limite de requisições utilizando **Bucket4j**.

O Bucket4j controla a quantidade de requisições permitidas dentro de determinado intervalo de tempo, ajudando a proteger os endpoints contra abuso e excesso de chamadas.

O projeto também utiliza **Hazelcast** como armazenamento compartilhado para o estado do Rate Limiting.

Atualmente, o projeto não depende de múltiplas instâncias do Transaction Service e, portanto, o Rate Limiting poderia funcionar apenas com armazenamento em memória local.

A integração com Hazelcast foi adicionada para tornar a implementação compatível com um cenário de **escalabilidade horizontal**.

Caso o Transaction Service seja executado em múltiplas instâncias, todas podem compartilhar o mesmo estado do Rate Limiting:

```text
                    ┌───────────────┐
                    │    Client     │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │ Load Balancer │
                    └───┬───────┬───┘
                        │       │
              ┌─────────▼─┐   ┌─▼─────────┐
              │ Instance 1│   │ Instance 2│
              │ Bucket4j  │   │ Bucket4j  │
              └─────┬─────┘   └─────┬─────┘
                    │               │
                    └───────┬───────┘
                            ▼
                      ┌───────────┐
                      │ Hazelcast │
                      └───────────┘
```

Sem um armazenamento compartilhado, cada instância manteria seu próprio contador de requisições.

Com Hazelcast, o estado pode ser compartilhado entre as instâncias, mantendo o limite consistente mesmo após uma eventual escalabilidade horizontal da aplicação.

## Pré-requisitos

Para executar o projeto utilizando Docker Compose, é necessário ter instalado:

* Git
* Docker
* Docker Compose

Não é necessário instalar PostgreSQL ou Mailpit manualmente.

## Como Rodar

Clone o repositório:

```bash
git clone <https://github.com/guuhf/transaction-service.git>
```

Entre na pasta do projeto:

```bash
cd transaction-service
```

Crie o arquivo `.env` utilizando o arquivo de exemplo.

### Windows

```bash
copy .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

Suba os containers:

```bash
docker compose up -d --build
```

Ao iniciar a aplicação, o **Flyway** verifica o histórico de migrations e executa automaticamente aquelas que ainda não foram aplicadas, preparando a estrutura necessária no PostgreSQL.

Para verificar os containers em execução:

```bash
docker compose ps
```

A API ficará disponível em:

```text
http://localhost:8084
```

O Mailpit ficará disponível em:

```text
http://localhost:8025
```

## Variáveis de Ambiente

Exemplo de configuração do arquivo `.env`:

```env
DB_NAME=db_transactions
DB_USER=postgres
DB_PASSWORD=postgres

JWT_SECRET_KEY=change-this-secret-key-before-production

EMAIL_FROM=no-reply@transaction-service.local
EMAIL_PERSONAL_NAME=Transaction Service
```

> Os valores apresentados são destinados ao ambiente local de desenvolvimento. Credenciais e chaves utilizadas em produção não devem ser armazenadas diretamente no repositório.

## Migrações do Banco de Dados

As alterações estruturais do banco de dados são gerenciadas através do **Flyway**.

As migrations ficam versionadas junto ao código-fonte e são executadas em ordem durante a inicialização da aplicação.

Exemplo de estrutura:

```text
src/main/resources/db/migration
├── V1__create_users.sql
├── V2__create_categories.sql
└── V3__create_transactions.sql
```

Cada nova alteração no schema deve ser adicionada através de uma nova migration, evitando alterações manuais na estrutura do banco.

## Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação.

O fluxo básico consiste em:

1. Registrar um usuário.
2. Realizar login.
3. Receber um Access Token e um Refresh Token.
4. Utilizar o Access Token nos endpoints protegidos.
5. Utilizar o Refresh Token para obter um novo Access Token quando necessário.

Exemplo de autenticação:

```http
Authorization: Bearer <access_token>
```

Quando o Access Token expirar, o endpoint:

```http
POST /auth/refresh-token
```

pode ser utilizado para gerar um novo token sem exigir um novo login.

## Swagger

Com a aplicação em execução, a documentação interativa da API pode ser acessada em:

```text
http://localhost:8084/swagger-ui.html
```

Através do Swagger é possível visualizar os endpoints disponíveis, contratos da API e realizar requisições diretamente pela interface.

## Endpoints Principais

| Método | Endpoint                 | Descrição                                         |
| ------ |--------------------------|---------------------------------------------------|
| POST   | `/auth`                  | Registra um novo usuário                          |
| POST   | `/auth/login`            | Autentica o usuário                               |
| POST   | `/auth/refresh-token`    | Gera um novo Access Token                         |
| POST   | `/transactions/category` | Cria uma categoria                                |
| GET    | `/transactions/category` | Lista as categorias do usuário                    |
| POST   | `/transactions`          | Registra uma nova transação                       |
| GET    | `/transactions`          | Lista transações utilizando filtros               |
| GET    | `/transactions/report`   | Gera um relatório financeiro<br/>e envia no email |
A documentação completa dos endpoints, parâmetros, DTOs e respostas está disponível através do Swagger.

## Fluxo de Demonstração

1. Subir o ambiente com Docker Compose.
2. Aguardar a inicialização da aplicação e execução das migrations pelo Flyway.
3. Registrar um usuário através de `/auth`.
4. Realizar login através de `/auth/login`.
5. Copiar o Access Token retornado.
6. Autorizar as requisições utilizando o JWT.
7. Criar categorias de receitas e despesas.
8. Registrar transações.
9. Consultar as transações utilizando filtros.
10. Gerar um relatório financeiro.
11. Solicitar o envio do relatório por email.
12. Abrir o Mailpit.
13. Conferir o email enviado pelo serviço de notificação.

## Decisões Técnicas

### JWT

JWT foi utilizado para implementar autenticação stateless, evitando a necessidade de manter uma sessão tradicional no servidor para cada usuário autenticado.

### Refresh Token

O Refresh Token permite gerar um novo Access Token sem exigir que o usuário realize novamente o processo de login após a expiração do token de acesso.

### PostgreSQL

PostgreSQL foi escolhido como banco de dados principal por ser um banco relacional adequado para representar usuários, categorias e transações financeiras, além de oferecer mecanismos de integridade e consistência dos dados.

### Flyway

Flyway foi utilizado para versionar e automatizar as migrações do banco de dados.

As alterações na estrutura do PostgreSQL são mantidas em arquivos de migration versionados junto ao código-fonte, permitindo que diferentes ambientes sejam inicializados e atualizados de forma consistente.

Ao iniciar a aplicação, o Flyway identifica quais migrations ainda não foram executadas e aplica apenas as versões pendentes, mantendo um histórico das alterações realizadas no schema.

Essa abordagem evita depender de alterações manuais no banco e mantém a evolução da estrutura de dados rastreável junto ao histórico do projeto.

### OpenFeign

OpenFeign foi utilizado para abstrair a comunicação HTTP entre o **Transaction Service** e o **Transaction Notification Service**.

Dessa forma, o serviço financeiro pode solicitar o envio de notificações sem assumir diretamente a responsabilidade pelo envio de emails.

### Bucket4j

Bucket4j foi utilizado para implementar **Rate Limiting** nos endpoints da API.

A solução permite controlar a quantidade de requisições realizadas dentro de determinado intervalo de tempo, ajudando a proteger a aplicação contra abuso e excesso de chamadas.

### Hazelcast

O Hazelcast não é uma dependência obrigatória para o cenário atual do projeto, já que o Transaction Service pode ser executado em uma única instância e manter o estado do Rate Limiting em memória.

Mesmo assim, ele foi integrado ao Bucket4j para aplicar um modelo mais preparado para **escalabilidade horizontal**.

Caso múltiplas instâncias do Transaction Service sejam executadas, o Hazelcast permite compartilhar o estado dos limites de requisição entre elas, evitando que cada instância mantenha um contador independente.

A implementação foi utilizada também como forma de aplicar conceitos relacionados a estado compartilhado e sistemas distribuídos.

### MapStruct

MapStruct foi utilizado para realizar o mapeamento entre entidades e DTOs através de código gerado em tempo de compilação, reduzindo código repetitivo e mantendo a separação entre persistência e contratos da API.

### Mailpit

Mailpit foi utilizado no ambiente de desenvolvimento para capturar emails enviados pela aplicação.

Isso permite testar todo o fluxo de notificações localmente sem utilizar credenciais reais ou enviar emails para endereços externos.

### Docker Compose

Docker Compose foi utilizado para criar um ambiente local reproduzível, permitindo subir os serviços e dependências necessários através de um único comando.

### GitHub Actions

GitHub Actions é utilizado para automatizar verificações do projeto através de um pipeline de integração contínua.

## Estrutura Geral

```text
Client
  │
  ▼
Transaction Service
  │
  ├── Authentication
  ├── Categories
  ├── Transactions
  ├── Reports
  ├── Rate Limiting
  │     ├── Bucket4j
  │     └── Hazelcast
  │
  ├── Flyway
  │     └── PostgreSQL
  │
  └── OpenFeign
        │
        ▼
Transaction Notification Service
        │
        ▼
      Mailpit
```

## Ambiente Local

| Serviço / Componente             | Função                                            |
| -------------------------------- | ------------------------------------------------- |
| Transaction Service              | API principal                                     |
| Transaction Notification Service | Processamento e envio de notificações             |
| PostgreSQL                       | Persistência dos dados                            |
| Flyway                           | Versionamento e execução das migrations           |
| Mailpit                          | Captura de emails em desenvolvimento              |
| Hazelcast                        | Estado compartilhado utilizado pelo Rate Limiting |

## Segurança

O projeto aplica algumas medidas voltadas à segurança da API:

* Autenticação com JWT
* Refresh Token
* Proteção de endpoints com Spring Security
* Rate Limiting com Bucket4j
* Suporte a Rate Limiting distribuído com Hazelcast
* Configurações sensíveis através de variáveis de ambiente
* Separação entre o serviço financeiro e o serviço responsável por notificações

## Objetivo do Projeto

O objetivo deste projeto é aplicar conceitos utilizados no desenvolvimento de APIs back-end com Spring Boot, incluindo:

* autenticação e autorização;
* modelagem e persistência de dados;
* versionamento e migração de banco de dados com Flyway;
* comunicação entre serviços;
* segurança de APIs;
* Rate Limiting;
* conceitos de estado compartilhado e escalabilidade horizontal;
* utilização de DTOs e mapeamento;
* geração de relatórios;
* envio de notificações;
* containerização;
* documentação de APIs;
* integração contínua.
