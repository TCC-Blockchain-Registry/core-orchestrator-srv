# 🧪 Guia de Teste Manual - Integração RabbitMQ

## 📋 Pré-requisitos

### 1️⃣ Serviços que Devem Estar Rodando

```bash
# ✅ RabbitMQ
cd message-queue
docker compose ps
# Deve mostrar: rabbitmq-property (Up/healthy)

# ✅ Besu Blockchain
cd besu-property-ledger/docker/besu
docker compose ps
# Deve mostrar: 4 validators (Up/healthy)

# ✅ PostgreSQL (para orchestrator)
# Deve estar rodando em localhost:5432
# Database: core_orchestrator_db
# User: postgres / Password: postgres123

# ✅ Offchain Consumer API
cd offchain-consumer-srv
npm run dev
# Deve responder em http://localhost:3000

# ✅ Queue Worker
cd queue-worker
npm run dev
# Deve estar consumindo da fila blockchain-jobs
```

### 2️⃣ Importar Collection no Postman

1. Abra o Postman
2. Click em **Import**
3. Selecione o arquivo: `core-orchestrator-srv/postman/Core-Orchestrator-RabbitMQ-Integration.postman_collection.json`
4. A collection será importada com todas as variáveis configuradas

## 🚀 Passo a Passo do Teste

### Passo 1: Verificar Health Checks

Execute os requests na pasta **"1. Health Checks"**:

1. **Orchestrator Health**
   - Deve retornar 200 OK
   
2. **RabbitMQ Management API**
   - Deve retornar overview do RabbitMQ
   
3. **Offchain API Health**
   - Deve retornar `{"status": "OK"}`
   
4. **Check RabbitMQ Queue**
   - Deve mostrar status da fila `blockchain-jobs`

✅ **Se todos retornaram sucesso, prossiga!**

### Passo 2: Registrar Propriedade (REGISTER_PROPERTY)

Execute: **"2. Property Registration" → "Register Property - Test 1"**

**Request:**
```json
POST http://localhost:8080/api/properties/register
{
  "matriculaId": 111222,
  "folha": 100,
  "comarca": "São Paulo - SP",
  "endereco": "Rua RabbitMQ Test, 100",
  "metragem": 250,
  "proprietario": "0x565524f400856766f11562832eB809d889491a01",
  "matriculaOrigem": 0,
  "tipo": "URBANO",
  "isRegular": true
}
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "matriculaId": 111222,
  "folha": 100,
  "comarca": "São Paulo - SP",
  "endereco": "Rua RabbitMQ Test, 100",
  "metragem": 250,
  "proprietario": "0x565524f400856766f11562832eB809d889491a01",
  "matriculaOrigem": 0,
  "tipo": "URBANO",
  "isRegular": true,
  "blockchainTxHash": null,  // ⚠️ Ainda null (será atualizado depois)
  "createdAt": "2025-11-17T15:00:00",
  "updatedAt": "2025-11-17T15:00:00"
}
```

### Passo 3: Verificar Logs do Orchestrator

No terminal onde o orchestrator está rodando, você deve ver:

```log
📝 Property registered in database: matriculaId=111222, id=1
📤 Publishing blockchain job to queue: type=REGISTER_PROPERTY, id=abc-123-uuid
✅ Job published successfully: jobId=abc-123-uuid
🚀 Blockchain job published: jobId=abc-123-uuid, matriculaId=111222
```

### Passo 4: Verificar Queue Worker

No terminal do queue-worker, você deve ver:

```log
[Job abc-123-uuid] [REGISTER_PROPERTY] STARTED - Attempt 1
[Offchain API] POST /api/properties/register
[Offchain API] Response: 200
[Job abc-123-uuid] [REGISTER_PROPERTY] COMPLETED - TX: 0xabc123def456...
```

### Passo 5: Verificar RabbitMQ Management UI

Acesse: **http://localhost:15672** (admin / admin123)

1. Vá em **Queues**
2. Click em **blockchain-jobs**
3. Você deve ver:
   - **Total**: 1 mensagem processada
   - **Ready**: 0 (nenhuma esperando)
   - **Unacked**: 0 (nenhuma sendo processada)
   - **Deliver/Get**: 1 (job foi entregue ao worker)

### Passo 6: Verificar na Blockchain

Execute: **"5. Verify on Blockchain" → "Check Property on Blockchain"**

⏳ **Aguarde 5-10 segundos** após o registro para garantir que o job foi processado.

**Request:**
```bash
GET http://localhost:3000/api/properties/111222
```

**Resposta Esperada:**
```json
{
  "success": true,
  "data": {
    "matriculaId": 111222,
    "folha": 100,
    "comarca": "São Paulo - SP",
    "endereco": "Rua RabbitMQ Test, 100",
    "metragem": 250,
    "proprietario": "0x565524f400856766f11562832eB809d889491a01",
    "currentOwner": "0x565524f400856766f11562832eB809d889491a01",
    "exists": true,
    "frozen": false,
    "typeName": "URBANO"
  }
}
```

✅ **SUCESSO!** A propriedade foi registrada na blockchain!

### Passo 7: Testar Transferência (CONFIGURE_TRANSFER)

Execute: **"4. Property Transfers" → "Initiate Transfer"**

**Request:**
```json
POST http://localhost:8080/api/property-transfers/initiate
{
  "propertyId": 1,
  "fromProprietario": "0x565524f400856766f11562832eB809d889491a01",
  "toProprietario": "0x1234567890123456789012345678901234567890"
}
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "propertyId": 1,
  "fromProprietario": "0x565524f400856766f11562832eB809d889491a01",
  "toProprietario": "0x1234567890123456789012345678901234567890",
  "status": "PENDING",
  "createdAt": "2025-11-17T15:05:00"
}
```

**Logs Esperados (Orchestrator):**
```log
📝 Transfer initiated in database: transferId=1, propertyId=1
📤 Publishing blockchain job to queue: type=CONFIGURE_TRANSFER
✅ Job published successfully
🚀 Blockchain transfer configuration job published: jobId=xyz-789-uuid
```

**Logs Esperados (Queue Worker):**
```log
[Job xyz-789-uuid] [CONFIGURE_TRANSFER] STARTED
[Offchain API] POST /api/transfers/configure
[Offchain API] Response: 200
[Job xyz-789-uuid] [CONFIGURE_TRANSFER] COMPLETED
```

## 📊 Verificações de RabbitMQ

### Ver Estatísticas da Fila

Execute: **"6. RabbitMQ Monitoring" → "Get blockchain-jobs Queue Stats"**

**Resposta:**
```json
{
  "name": "blockchain-jobs",
  "messages": 0,           // Total de mensagens na fila
  "messages_ready": 0,     // Aguardando processamento
  "messages_unacknowledged": 0,  // Sendo processadas
  "messages_details": {
    "rate": 0.0
  },
  "message_stats": {
    "publish": 2,          // ✅ 2 jobs publicados
    "publish_details": {
      "rate": 0.0
    },
    "deliver_get": 2,      // ✅ 2 jobs entregues ao worker
    "deliver_get_details": {
      "rate": 0.0
    }
  }
}
```

### Ver Conexões Ativas

Execute: **"6. RabbitMQ Monitoring" → "List Connections"**

Você deve ver 2 conexões:
1. **Core Orchestrator** (publisher)
2. **Queue Worker** (consumer)

## 🔍 Troubleshooting

### ❌ Erro: Connection Refused (RabbitMQ)

**Problema:** Orchestrator não consegue conectar ao RabbitMQ

**Solução:**
```bash
# Verificar se RabbitMQ está rodando
cd message-queue
docker compose ps

# Se não estiver, iniciar
docker compose up -d
```

### ❌ Jobs Ficam na Fila (não são processados)

**Problema:** Queue Worker não está consumindo

**Solução:**
```bash
# Verificar se Queue Worker está rodando
cd queue-worker
npm run dev

# Verificar logs do worker
tail -f /tmp/queue-worker.log
```

### ❌ Job Falha (vai para DLQ)

**Problema:** Job falhou após 3 tentativas

**Investigar:**
1. Verificar logs do Queue Worker
2. Verificar se Offchain API está respondendo
3. Verificar Dead Letter Queue:

```bash
# Via Postman
GET http://localhost:15672/api/queues/%2F/blockchain-jobs-dlq

# Ou via Management UI
http://localhost:15672/#/queues/%2F/blockchain-jobs-dlq
```

### ❌ Propriedade no Banco mas Não na Blockchain

**Problema:** Job não foi processado ou falhou

**Verificar:**
1. Logs do Queue Worker (erros?)
2. RabbitMQ Management UI (job na DLQ?)
3. Offchain API logs

**Reprocessar:**
- Se o job falhou, você pode republicá-lo manualmente
- Ou esperar implementação de retry manual

## 📈 Testes Avançados

### Teste de Carga (Múltiplas Propriedades)

Execute os 3 requests em **"2. Property Registration"**:
1. Register Property - Test 1
2. Register Property - Test 2
3. Register Property - Rural

Aguarde 15-20 segundos e verifique se **todas** foram processadas:

```bash
# Via RabbitMQ
GET http://localhost:15672/api/queues/%2F/blockchain-jobs
# message_stats.deliver_get deve ser 3

# Via Offchain API
GET http://localhost:3000/api/properties/111222
GET http://localhost:3000/api/properties/222333
GET http://localhost:3000/api/properties/333444
```

### Teste de Falha e Retry

1. **Parar** Offchain API:
   ```bash
   # Ctrl+C no terminal da API
   ```

2. Registrar propriedade via orchestrator
   - Job ficará na fila

3. Verificar tentativas de retry no worker:
   ```log
   [Job xxx] STARTED - Attempt 1
   [Offchain API] No response received
   [Job xxx] RETRY - Will retry in 10000ms
   [Job xxx] STARTED - Attempt 2
   [Offchain API] No response received
   [Job xxx] RETRY - Will retry in 20000ms
   [Job xxx] STARTED - Attempt 3
   [Offchain API] No response received
   [Job xxx] FAILED - Max retries exceeded
   ```

4. **Reiniciar** Offchain API
   ```bash
   cd offchain-consumer-srv
   npm run dev
   ```

5. Job foi para DLQ (pode ser reprocessado manualmente depois)

## ✅ Checklist Final

Após executar todos os testes, você deve ter:

- [ ] ✅ Propriedade registrada no PostgreSQL (orchestrator)
- [ ] ✅ Job REGISTER_PROPERTY publicado no RabbitMQ
- [ ] ✅ Job processado pelo Queue Worker
- [ ] ✅ Propriedade registrada na blockchain (Besu)
- [ ] ✅ Transferência iniciada no PostgreSQL
- [ ] ✅ Job CONFIGURE_TRANSFER publicado no RabbitMQ
- [ ] ✅ Job processado pelo Queue Worker
- [ ] ✅ Transferência configurada na blockchain
- [ ] ✅ Estatísticas corretas no RabbitMQ Management UI
- [ ] ✅ Nenhum job na Dead Letter Queue (DLQ)

## 🎉 Conclusão

Se todos os checkboxes acima estão marcados, a integração está **100% funcional**!

O sistema completo está funcionando:
```
Cliente → Orchestrator → PostgreSQL ✅
              ↓
         RabbitMQ ✅
              ↓
       Queue Worker ✅
              ↓
      Offchain API ✅
              ↓
    Besu Blockchain ✅
```

---

**Próximos Passos:**
- Implementar callback de TxHash
- Adicionar monitoramento de jobs
- Criar dashboard de estatísticas
- Implementar retry manual da DLQ

