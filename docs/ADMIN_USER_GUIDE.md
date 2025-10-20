# Guia do Usuário Admin

## 🔐 Funcionalidade de Roles

O sistema agora suporta dois tipos de usuários:

### Tipos de Usuário (UserRole)

1. **USER** - Usuário normal com permissões padrão
2. **ADMIN** - Administrador com acesso completo ao sistema

## 🔒 Segurança - Criptografia de Senhas

**Todas as senhas são criptografadas usando BCrypt** antes de serem armazenadas no banco de dados.

- **Algoritmo:** BCrypt (hash adaptativo com salt)
- **Não é possível recuperar** a senha original do hash
- **Senhas nunca são armazenadas** em texto plano
- **Cada senha tem um salt único** para máxima segurança

## 👤 Usuário Admin Padrão

Ao iniciar a aplicação pela primeira vez, um usuário admin padrão é criado automaticamente:

### Credenciais Padrão
```
Email: admin@core-orchestrator.com
Senha: admin123
```

⚠️ **IMPORTANTE:** Altere a senha do admin após o primeiro login!

## 📝 Registrando Novos Usuários

⚠️ **Importante:** Todas as senhas são automaticamente criptografadas antes de serem salvas no banco de dados.

### Registrar Usuário Normal (USER)

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

Ou com role explícito:

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123",
    "role": "USER"
  }'
```

### Registrar Usuário Admin

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Admin",
    "email": "maria@admin.com",
    "password": "senha123",
    "role": "ADMIN"
  }'
```

**Nota:** A senha `senha123` será automaticamente criptografada usando BCrypt e armazenada como um hash seguro no banco de dados.

## 📊 Estrutura do Banco de Dados

A tabela `users` agora inclui o campo `role`:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT users_role_check CHECK (role IN ('USER', 'ADMIN'))
);
```

## 🔄 Resposta da API

Ao registrar um usuário, a resposta incluirá o role:

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "role": "USER",
  "active": true,
  "createdAt": "2023-12-01T10:30:00"
}
```

## 🎯 Uso no Frontend

O frontend pode usar o campo `role` retornado pela API para:

1. **Exibir telas diferentes** para usuários normais e admins
2. **Controlar acesso** a funcionalidades específicas
3. **Customizar a interface** baseado no tipo de usuário

### Exemplo de Uso

```javascript
// Após login ou registro
const user = response.data;

if (user.role === 'ADMIN') {
    // Redirecionar para painel admin
    navigate('/admin/dashboard');
} else {
    // Redirecionar para painel do usuário
    navigate('/user/dashboard');
}
```

## 🔧 Configuração

### Alterando as Credenciais Padrão do Admin

Edite o arquivo `AdminUserInitializer.java`:

```java
private static final String DEFAULT_ADMIN_NAME = "Administrator";
private static final String DEFAULT_ADMIN_EMAIL = "admin@core-orchestrator.com";
private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
```

### Desabilitando a Criação Automática do Admin

Para desabilitar a criação automática do usuário admin, comente ou remova o bean `initAdminUser` em `AdminUserInitializer.java`.

## 📚 Arquivos Modificados

Os seguintes arquivos foram atualizados para suportar roles e segurança:

1. **Domain Layer:**
   - `UserRole.java` - Enum com os tipos de usuário
   - `UserModel.java` - Adicionado campo `role`

2. **Persistence Layer:**
   - `UserEntity.java` - Adicionado campo `role`
   - `UserPersistenceMapper.java` - Atualizado para mapear `role`

3. **Input Layer (API):**
   - `UserRegistrationRequest.java` - Adicionado campo `role` (opcional)
   - `UserRegistrationResponse.java` - Adicionado campo `role`
   - `UserController.java` - Atualizado para processar `role`

4. **Service Layer:**
   - `UserUseCase.java` - Adicionado parâmetro `role`
   - `UserService.java` - Lógica para definir role (default: USER) + criptografia de senha

5. **Configuration:**
   - `PasswordEncoderConfig.java` - Configuração do BCrypt para criptografia
   - `AdminUserInitializer.java` - Cria usuário admin automaticamente com senha criptografada

6. **Dependencies (pom.xml):**
   - Adicionado `spring-security-crypto` para criptografia de senhas

## 🔐 Rota de Login

### Endpoint: POST /api/users/login

**Login com Email e Senha:**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@core-orchestrator.com","password":"admin123"}'
```

**Resposta de Sucesso (200):**
```json
{
  "id": 1,
  "name": "Administrator",
  "email": "admin@core-orchestrator.com",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2023-12-01T10:30:00",
  "message": "Login successful"
}
```

**Resposta de Erro (401 Unauthorized):**
- Email não encontrado
- Senha incorreta
- Conta inativa

### Como Funciona a Verificação de Senha?

O sistema usa `passwordEncoder.matches()` para verificar:
1. Usuário digita: `"admin123"`
2. Sistema busca hash do banco: `$2a$10$N9qo8uLO...`
3. BCrypt extrai o salt e aplica na senha digitada
4. Compara os hashes resultantes
5. Retorna sucesso ou erro

⚠️ **Importante:** A senha nunca é armazenada em texto plano nem comparada diretamente!

## 🚀 Testando

### Via Swagger UI

Acesse: http://localhost:8080/swagger-ui/index.html

1. Vá até o endpoint `POST /api/users/register`
2. Clique em "Try it out"
3. Insira os dados com o `role` desejado
4. Execute e verifique a resposta

### Verificando no Banco de Dados

```bash
# Conectar ao PostgreSQL
docker exec -it core-orchestrator-postgres psql -U postgres -d core_orchestrator_db

# Listar usuários e seus roles
SELECT id, name, email, role, active FROM users;

# Verificar que as senhas estão criptografadas
SELECT id, email, LEFT(password, 20) as password_hash_preview FROM users;
# Você verá algo como: $2a$10$abcdefghijklm... (hash BCrypt)
```

**Exemplo de senha criptografada:**
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```
- `$2a$` - Algoritmo BCrypt
- `10` - Custo/força do hash
- Resto - Salt + Hash da senha

## ⚡ Próximos Passos

Para implementar autorização completa, considere:

1. Adicionar Spring Security
2. Implementar JWT para autenticação
3. Criar anotações `@PreAuthorize` para proteger endpoints
4. Adicionar mais roles se necessário (ex: MODERATOR)

