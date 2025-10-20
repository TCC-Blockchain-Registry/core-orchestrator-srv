# Core Orchestrator Service

Projeto Spring Boot com PostgreSQL containerizado usando Docker.

## 🚀 Início Rápido

### Opção 1: Tudo com Docker (Recomendado)
```bash
./start.sh start
```

### Opção 2: Desenvolvimento Local
```bash
# 1. Iniciar apenas PostgreSQL
./setup-db.sh start

# 2. Executar aplicação Spring Boot
mvn spring-boot:run
```

## 📋 Pré-requisitos

- **Docker e Docker Compose** (obrigatório)
- **Java 21** (para desenvolvimento local)
- **Maven** (opcional, o projeto usa Maven Wrapper)

## 🛠️ Comandos Disponíveis

### Script Principal (`start.sh`)
- `./start.sh start` - Inicia PostgreSQL e Spring Boot
- `./start.sh stop` - Para todos os serviços
- `./start.sh restart` - Reinicia todos os serviços
- `./start.sh build` - Constrói apenas o projeto Maven
- `./start.sh rebuild` - Reconstrói e reinicia todos os serviços
- `./start.sh logs` - Mostra logs dos serviços
- `./start.sh status` - Mostra status dos serviços
- `./start.sh clean` - Remove containers e volumes (⚠️ apaga dados)

### Script do Banco (`setup-db.sh`)
- `./setup-db.sh start` - Inicia PostgreSQL
- `./setup-db.sh stop` - Para PostgreSQL
- `./setup-db.sh restart` - Reinicia PostgreSQL
- `./setup-db.sh logs` - Mostra logs do PostgreSQL

### Script de Recriação do Banco (`recreate-db.sh`)
- `./recreate-db.sh` - Recria o banco de dados (⚠️ apaga todos os dados)
  - Útil durante desenvolvimento quando há mudanças no schema
  - O Hibernate recriará as tabelas automaticamente

## 🐘 Configuração do PostgreSQL

- **Host:** localhost
- **Porta:** 5432
- **Database:** core_orchestrator_db
- **Usuário:** postgres
- **Senha:** postgres123

## 🧪 Testes

Os testes usam **H2 em memória**, então não precisam do PostgreSQL:

```bash
# Executar testes (não precisa de PostgreSQL)
mvn test

# Build completo (não precisa de PostgreSQL)
mvn clean install
```

## 🌐 Acesso à Aplicação

Após iniciar os serviços:

- **API Spring Boot:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **PostgreSQL:** localhost:5432

## 🔐 Sistema de Usuários

### Usuário Admin Padrão
Ao iniciar a aplicação pela primeira vez, um usuário admin é criado automaticamente:
- **Email:** admin@core-orchestrator.com
- **Senha:** admin123
- ⚠️ **Altere a senha após o primeiro login!**

### Tipos de Usuário
- **USER** - Usuário normal
- **ADMIN** - Administrador

### Segurança
- ✅ Todas as senhas são **criptografadas com BCrypt**
- ✅ Senhas nunca são armazenadas em texto plano
- ✅ Cada senha tem um salt único

📚 Para mais detalhes, veja [docs/ADMIN_USER_GUIDE.md](docs/ADMIN_USER_GUIDE.md)

## 🔧 Opções de Instalação PostgreSQL

### Opção 1: Docker (Recomendado)
```bash
# Usar o script fornecido
./setup-db.sh start
```

### Opção 2: PostgreSQL Local (Ubuntu/Debian)
```bash
# Instalar PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Configurar usuário e banco
sudo -u postgres psql
CREATE DATABASE core_orchestrator_db;
CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres123';
GRANT ALL PRIVILEGES ON DATABASE core_orchestrator_db TO postgres;
\q
```

### Opção 3: PostgreSQL Local (CentOS/RHEL)
```bash
# Instalar PostgreSQL
sudo yum install postgresql-server postgresql-contrib
sudo postgresql-setup initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Configurar usuário e banco
sudo -u postgres psql
CREATE DATABASE core_orchestrator_db;
CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres123';
GRANT ALL PRIVILEGES ON DATABASE core_orchestrator_db TO postgres;
\q
```

### Opção 4: PostgreSQL (macOS com Homebrew)
```bash
# Instalar PostgreSQL
brew install postgresql
brew services start postgresql

# Configurar usuário e banco
createdb core_orchestrator_db
psql core_orchestrator_db
CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres123';
GRANT ALL PRIVILEGES ON DATABASE core_orchestrator_db TO postgres;
\q
```

## 📁 Estrutura do Projeto

```
core-orchestrator-srv/
├── src/                    # Código fonte Spring Boot
│   ├── main/              # Código principal
│   └── test/              # Testes (usa H2 em memória)
├── docker-compose.yml      # Configuração Docker Compose
├── Dockerfile             # Imagem Docker da aplicação
├── start.sh              # Script completo de automação
├── setup-db.sh           # Script apenas para PostgreSQL
├── pom.xml               # Configuração Maven
└── README.md             # Este arquivo
```

## 🔧 Desenvolvimento Local

### Para desenvolvimento com PostgreSQL local:
1. **Instale PostgreSQL** (qualquer opção acima)
2. **Configure o banco:**
   ```sql
   CREATE DATABASE core_orchestrator_db;
   CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres123';
   GRANT ALL PRIVILEGES ON DATABASE core_orchestrator_db TO postgres;
   ```
3. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

### Para desenvolvimento com Docker:
```bash
# Apenas PostgreSQL via Docker
./setup-db.sh start

# Executar aplicação localmente
mvn spring-boot:run
```

## 📝 Logs

```bash
# Logs completos
./start.sh logs

# Logs apenas do PostgreSQL
./setup-db.sh logs
```

## ⚠️ Troubleshooting

### Problema: `mvn clean install` falha por falta do banco
**Solução:** Os testes usam H2 em memória, não deveria falhar. Se falhar:
```bash
mvn clean install -DskipTests
```

### Problema: Conexão recusada ao PostgreSQL
**Soluções:**
```bash
# Verificar se PostgreSQL está rodando
./setup-db.sh start

# Ver logs do PostgreSQL
./setup-db.sh logs

# Testar conexão manualmente
docker exec -it core-orchestrator-postgres pg_isready -U postgres
```

### Problema: Permissão negada nos scripts
```bash
chmod +x start.sh setup-db.sh
```

### Problema: PostgreSQL não aceita conexões
```bash
# Reinicializar completamente
./start.sh clean
./start.sh start
```

## 🧪 Testando a API

### Opção 1: Swagger UI
Acesse: http://localhost:8080/swagger-ui/index.html

### Opção 2: Postman
Importe a collection: `docs/collection/Core-Orchestrator-API.postman_collection.json`

### Opção 3: cURL
```bash
# Registrar usuário normal
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João","email":"joao@email.com","password":"senha123"}'

# Registrar usuário admin
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria","email":"maria@admin.com","password":"senha456","role":"ADMIN"}'

# Fazer login (usar admin padrão ou usuário registrado)
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@core-orchestrator.com","password":"admin123"}'
```

**Nota:** Todas as senhas são automaticamente criptografadas com BCrypt.

📚 **Mais informações:** Veja [docs/ADMIN_USER_GUIDE.md](docs/ADMIN_USER_GUIDE.md)
