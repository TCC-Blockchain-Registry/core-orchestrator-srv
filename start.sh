#!/bin/bash

# Core Orchestrator Service - Script de Automação
# Autor: Fabiano
# Descrição: Script para automatizar o download e subida do PostgreSQL com Spring Boot

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}   Core Orchestrator Service    ${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Função para verificar se o Docker está instalado
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado. Por favor, instale o Docker primeiro."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose não está disponível. Por favor, instale o Docker Compose."
        exit 1
    fi
}

# Função para verificar se o Maven está instalado
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven não encontrado. Tentando usar o Maven Wrapper..."
        if [ ! -f "./mvnw" ]; then
            print_error "Maven Wrapper também não encontrado. Por favor, instale o Maven."
            exit 1
        fi
    fi
}

# Função para construir o projeto Maven
build_project() {
    print_message "Construindo o projeto Maven..."
    
    if command -v mvn &> /dev/null; then
        mvn clean package -DskipTests
    else
        ./mvnw clean package -DskipTests
    fi
    
    if [ $? -eq 0 ]; then
        print_message "Projeto construído com sucesso!"
    else
        print_error "Falha ao construir o projeto."
        exit 1
    fi
}

# Função para baixar imagens Docker
pull_images() {
    print_message "Baixando imagem do PostgreSQL..."
    docker pull postgres:15-alpine
    print_message "Imagens baixadas com sucesso!"
}

# Função para subir os serviços
# Parâmetro opcional: --build para forçar reconstrução da imagem Docker
start_services() {
    local build_flag="${1:-}"
    print_message "Subindo PostgreSQL e aplicação Spring Boot..."

    # Verificar se docker-compose ou docker compose está disponível
    if command -v docker-compose &> /dev/null; then
        docker-compose up -d $build_flag
    else
        docker compose up -d $build_flag
    fi

    print_message "Serviços iniciados com sucesso!"
    print_message "PostgreSQL disponível em: localhost:5432"
    print_message "Spring Boot disponível em: http://localhost:8080"
}

# Função para parar os serviços
stop_services() {
    print_message "Parando os serviços..."
    
    if command -v docker-compose &> /dev/null; then
        docker-compose down
    else
        docker compose down
    fi
    
    print_message "Serviços parados com sucesso!"
}

# Função para mostrar logs
show_logs() {
    print_message "Mostrando logs dos serviços..."
    
    if command -v docker-compose &> /dev/null; then
        docker-compose logs -f
    else
        docker compose logs -f
    fi
}

# Função para mostrar status dos serviços
show_status() {
    print_message "Status dos serviços:"
    
    if command -v docker-compose &> /dev/null; then
        docker-compose ps
    else
        docker compose ps
    fi
}

# Função para reiniciar os serviços
restart_services() {
    print_message "Reiniciando os serviços..."
    stop_services
    start_services
}

# Função para limpeza completa (remove volumes)
clean_all() {
    print_warning "ATENÇÃO: Esta operação irá remover todos os dados do banco!"
    read -p "Deseja continuar? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_message "Removendo containers e volumes..."
        
        if command -v docker-compose &> /dev/null; then
            docker-compose down -v --remove-orphans
        else
            docker compose down -v --remove-orphans
        fi
        
        print_message "Limpeza concluída!"
    else
        print_message "Operação cancelada."
    fi
}

# Função para mostrar ajuda
show_help() {
    echo "Core Orchestrator Service - Script de Automação"
    echo ""
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  start       - Inicia PostgreSQL e Spring Boot"
    echo "  stop        - Para todos os serviços"
    echo "  restart     - Reinicia todos os serviços"
    echo "  build       - Constrói apenas o projeto Maven"
    echo "  rebuild     - Reconstrói e reinicia todos os serviços"
    echo "  logs        - Mostra logs dos serviços"
    echo "  status      - Mostra status dos serviços"
    echo "  clean       - Remove containers e volumes (CUIDADO: apaga dados)"
    echo "  help        - Mostra esta ajuda"
    echo ""
    echo "Exemplos:"
    echo "  $0 start    # Inicia tudo"
    echo "  $0 logs     # Acompanha os logs"
    echo "  $0 stop     # Para tudo"
}

# Menu principal
main() {
    print_header
    
    # Verificar pré-requisitos
    check_docker
    check_maven
    
    case "${1:-help}" in
        "start")
            pull_images
            build_project
            start_services
            ;;
        "stop")
            stop_services
            ;;
        "restart")
            restart_services
            ;;
        "build")
            build_project
            ;;
        "rebuild")
            stop_services
            build_project
            start_services --build
            ;;
        "logs")
            show_logs
            ;;
        "status")
            show_status
            ;;
        "clean")
            clean_all
            ;;
        "help"|*)
            show_help
            ;;
    esac
}

# Executar função principal
main "$@"
