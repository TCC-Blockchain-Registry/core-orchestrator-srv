# 🚀 Como Iniciar o Core Orchestrator

## Pré-requisitos

### 1. PostgreSQL Rodando

O orchestrator precisa do PostgreSQL rodando. Verifique:

```bash
# Verificar se PostgreSQL está rodando
psql -U postgres -h localhost -c "SELECT version();"

# Se não estiver, inicie:
# macOS (via Homebrew):
brew services start postgresql@16

# Docker:
docker run --name postgres-orchestrator \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_DB=core_orchestrator_db \
  -p 5432:5432 \
  -d postgres:16
```

**Credenciais esperadas:**
- Host: `localhost`
- Port: `5432`
- Database: `core_orchestrator_db`
- User: `postgres`
- Password: `postgres123`

### 2. RabbitMQ Rodando (para enviar jobs)

```bash
cd /Users/fabiano/college/message-queue
docker compose up -d

# Verificar
docker compose ps
# Deve mostrar: rabbitmq-property (Up/healthy)
```

## 🏃 Iniciar o Orchestrator

### Opção 1: Via Maven (Recomendado para Desenvolvimento)

```bash
cd /Users/fabiano/college/core-orchestrator-srv

# Iniciar com Maven
mvn spring-boot:run
```

### Opção 2: Via JAR Compilado

```bash
cd /Users/fabiano/college/core-orchestrator-srv

# Compilar
mvn clean package -DskipTests

# Executar
java -jar target/core-orchestrator-srv-1.0-SNAPSHOT.jar
```

## ✅ Verificar se Iniciou Corretamente

### 1. Aguarde a Mensagem de Sucesso

No console, você deve ver:

```log
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.3.5)

...
Started Application in 5.234 seconds (process running for 5.789)
```

### 2. Testar Health Check

```bash
# Via curl
curl http://localhost:8080/actuator/health

# Resposta esperada:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 994662584320,
        "free": 123456789,
        "threshold": 10485760,
        "path": "/Users/fabiano/college/core-orchestrator-srv",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    },
    "rabbit": {
      "status": "UP",
      "details": {
        "version": "3.12.0"
      }
    }
  }
}
```

### 3. Testar Registro de Propriedade

```bash
curl -X POST http://localhost:8080/api/properties/register \
  -H "Content-Type: application/json" \
  -d '{
    "matriculaId": 999888,
    "folha": 100,
    "comarca": "São Paulo - SP",
    "endereco": "Rua Teste, 100",
    "metragem": 250,
    "proprietario": "0x565524f400856766f11562832eB809d889491a01",
    "matriculaOrigem": 0,
    "tipo": "URBANO",
    "isRegular": true
  }'
```

## 🐛 Troubleshooting

### ❌ Erro: "Connection to localhost:5432 refused"

**Problema:** PostgreSQL não está rodando

**Solução:**
```bash
# Verificar se PostgreSQL está rodando
brew services list | grep postgresql

# Iniciar PostgreSQL
brew services start postgresql@16

# Ou criar banco via Docker
docker run --name postgres-orchestrator \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_DB=core_orchestrator_db \
  -p 5432:5432 \
  -d postgres:16

# Aguardar 5 segundos e tentar novamente
```

### ❌ Erro: "Database 'core_orchestrator_db' does not exist"

**Problema:** Banco não foi criado

**Solução:**
```bash
# Criar o banco
psql -U postgres -h localhost -c "CREATE DATABASE core_orchestrator_db;"

# Ou conectar e criar manualmente
psql -U postgres -h localhost
CREATE DATABASE core_orchestrator_db;
\q
```

### ❌ Erro: "Port 8080 already in use"

**Problema:** Outra aplicação está usando a porta 8080

**Solução 1:** Parar o processo que está usando a porta
```bash
# Encontrar o processo
lsof -ti:8080

# Matar o processo
kill -9 $(lsof -ti:8080)
```

**Solução 2:** Mudar a porta do orchestrator
```yaml
# application.yml
server:
  port: 8081  # Mudar para 8081
```

### ❌ Erro: "Cannot connect to RabbitMQ"

**Problema:** RabbitMQ não está rodando

**Solução:**
```bash
cd /Users/fabiano/college/message-queue
docker compose up -d

# Verificar
docker compose ps
```

**Nota:** O orchestrator vai funcionar mesmo sem RabbitMQ, mas não vai enviar jobs para a fila.

### ❌ Health Check retorna 404

**Problema:** Actuator não está configurado

**Solução:** Já corrigido! As alterações foram aplicadas:
- ✅ Dependência Actuator adicionada
- ✅ Configuração no application.yml

Basta recompilar e reiniciar:
```bash
mvn clean compile
mvn spring-boot:run
```

## 📊 Endpoints Disponíveis

### API Principal
- **Base URL**: `http://localhost:8080`
- **Swagger**: `http://localhost:8080/swagger-ui.html`

### Actuator (Monitoring)
- **Health**: `http://localhost:8080/actuator/health`
- **Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`

### Properties
- `POST /api/properties/register` - Registrar propriedade
- `GET /api/properties` - Listar propriedades
- `GET /api/properties/{id}` - Buscar por ID
- `GET /api/properties/by-proprietario/{address}` - Buscar por dono

### Transfers
- `POST /api/property-transfers/initiate` - Iniciar transferência
- `GET /api/property-transfers/{id}` - Buscar transferência
- `POST /api/property-transfers/{id}/approve` - Aprovar transferência

## 🔍 Logs Úteis

### Ver Conexão com PostgreSQL
```log
HikariPool-1 - Starting...
HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...
HikariPool-1 - Start completed.
```

### Ver Conexão com RabbitMQ
```log
Created new connection: SimpleConnection@... [delegate=amqp://admin@127.0.0.1:5672/, localPort=...]
```

### Ver Job Sendo Publicado
```log
📤 Publishing blockchain job to queue: type=REGISTER_PROPERTY, id=abc-123
✅ Job published successfully: jobId=abc-123
```

## ✅ Checklist de Inicialização

- [ ] PostgreSQL rodando (porta 5432)
- [ ] Database `core_orchestrator_db` criado
- [ ] RabbitMQ rodando (porta 5672) [opcional]
- [ ] Porta 8080 disponível
- [ ] `mvn spring-boot:run` executado
- [ ] Logs mostram "Started Application in X seconds"
- [ ] Health check retorna `{"status": "UP"}`
- [ ] Swagger UI acessível em http://localhost:8080/swagger-ui.html

## 🎯 Próximos Passos

Após iniciar o orchestrator com sucesso:

1. **Importar Collection no Postman**
   - Arquivo: `postman/Core-Orchestrator-RabbitMQ-Integration.postman_collection.json`

2. **Executar Testes**
   - Pasta "1. Health Checks" no Postman
   - Pasta "2. Property Registration"

3. **Verificar Logs**
   - Orchestrator: jobs sendo publicados
   - Queue Worker: jobs sendo processados
   - RabbitMQ UI: estatísticas da fila

---

**Dica:** Mantenha 3 terminais abertos:
- Terminal 1: Orchestrator (`mvn spring-boot:run`)
- Terminal 2: Offchain API (`npm run dev`)
- Terminal 3: Queue Worker (`npm run dev`)

