#!/bin/bash

# Script para iniciar apenas o PostgreSQL
# Útil para desenvolvimento local

set -e

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}   PostgreSQL Development Setup  ${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Verificar se Docker está disponível
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo "Docker não está instalado!"
        exit 1
    fi
}

# Iniciar apenas PostgreSQL
start_postgres() {
    print_message "Iniciando PostgreSQL..."
    
    if command -v docker-compose &> /dev/null; then
        docker-compose up -d postgres
    else
        docker compose up -d postgres
    fi
    
    print_message "PostgreSQL iniciado com sucesso!"
    print_message "Conexão: jdbc:postgresql://localhost:5432/core_orchestrator_db"
    print_message "Usuário: postgres"
    print_message "Senha: postgres123"
    
    # Aguardar PostgreSQL ficar pronto
    print_message "Aguardando PostgreSQL ficar pronto..."
    sleep 5
    
    # Testar conexão
    if docker exec -i core-orchestrator-postgres pg_isready -U postgres > /dev/null 2>&1; then
        print_message "✅ PostgreSQL está pronto para conexões!"
    else
        print_message "⏳ PostgreSQL ainda inicializando... (normal na primeira execução)"
    fi
}

# Parar PostgreSQL
stop_postgres() {
    print_message "Parando PostgreSQL..."
    
    if command -v docker-compose &> /dev/null; then
        docker-compose stop postgres
    else
        docker compose stop postgres
    fi
    
    print_message "PostgreSQL parado!"
}

# Mostrar logs do PostgreSQL
show_logs() {
    if command -v docker-compose &> /dev/null; then
        docker-compose logs -f postgres
    else
        docker compose logs -f postgres
    fi
}

# Menu principal
main() {
    print_header
    check_docker
    
    case "${1:-start}" in
        "start")
            start_postgres
            ;;
        "stop")
            stop_postgres
            ;;
        "logs")
            show_logs
            ;;
        "restart")
            stop_postgres
            start_postgres
            ;;
        *)
            echo "Uso: $0 {start|stop|restart|logs}"
            echo ""
            echo "Comandos:"
            echo "  start   - Inicia PostgreSQL"
            echo "  stop    - Para PostgreSQL"
            echo "  restart - Reinicia PostgreSQL"
            echo "  logs    - Mostra logs do PostgreSQL"
            ;;
    esac
}

main "$@"
