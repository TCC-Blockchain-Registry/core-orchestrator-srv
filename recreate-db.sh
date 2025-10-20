#!/bin/bash

# Script para recriar o banco de dados PostgreSQL
# Útil durante o desenvolvimento quando há mudanças no schema

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}   PostgreSQL Database Reset    ${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Verificar se Docker está disponível
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado!"
        exit 1
    fi
}

# Verificar se o container está rodando
check_container() {
    if ! docker ps | grep -q core-orchestrator-postgres; then
        print_error "Container PostgreSQL não está rodando!"
        print_message "Execute: ./setup-db.sh start"
        exit 1
    fi
}

# Recriar banco de dados
recreate_database() {
    print_header
    
    print_warning "⚠️  ATENÇÃO: Todos os dados serão perdidos!"
    print_warning "Esta operação irá:"
    echo "   - Dropar o banco de dados 'core_orchestrator_db'"
    echo "   - Recriar o banco de dados vazio"
    echo "   - O Hibernate irá recriar as tabelas automaticamente"
    echo ""
    
    # Pedir confirmação
    read -p "Deseja continuar? (s/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        print_message "Operação cancelada."
        exit 0
    fi
    
    print_message "Recriando banco de dados..."
    
    # Dropar e recriar o banco
    docker exec -i core-orchestrator-postgres psql -U postgres <<-EOSQL
        -- Terminar todas as conexões ativas
        SELECT pg_terminate_backend(pg_stat_activity.pid)
        FROM pg_stat_activity
        WHERE pg_stat_activity.datname = 'core_orchestrator_db'
          AND pid <> pg_backend_pid();
        
        -- Dropar banco se existir
        DROP DATABASE IF EXISTS core_orchestrator_db;
        
        -- Recriar banco
        CREATE DATABASE core_orchestrator_db;
        
        -- Garantir permissões
        GRANT ALL PRIVILEGES ON DATABASE core_orchestrator_db TO postgres;
EOSQL
    
    if [ $? -eq 0 ]; then
        print_message "✅ Banco de dados recriado com sucesso!"
        echo ""
        print_message "📝 Próximos passos:"
        echo "   1. Reinicie a aplicação Spring Boot"
        echo "   2. O Hibernate criará as tabelas automaticamente (ddl-auto: update)"
        echo "   3. O usuário admin será criado automaticamente ao iniciar"
        echo ""
        print_message "Para verificar as tabelas criadas:"
        echo "   docker exec -it core-orchestrator-postgres psql -U postgres -d core_orchestrator_db -c '\dt'"
    else
        print_error "Falha ao recriar banco de dados!"
        exit 1
    fi
}

# Menu principal
main() {
    check_docker
    check_container
    recreate_database
}

main

