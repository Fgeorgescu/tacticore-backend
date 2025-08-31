#!/bin/bash

# Script de prueba para el endpoint de Tacti-Core
# Este script prueba los diferentes endpoints de la API

set -e

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Función para obtener la URL base
get_base_url() {
    if [ -f "terraform/environments/dev/terraform.tfstate" ]; then
        # Si estamos usando Terraform
        cd terraform/environments/dev
        terraform output -raw api_url | sed 's|/hello$||'
        cd ../..
    else
        # URL por defecto para desarrollo local
        echo "http://localhost:8080"
    fi
}

BASE_URL=$(get_base_url)
print_status "Using base URL: $BASE_URL"

# Función para hacer requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local content_type=$4
    
    local url="$BASE_URL$endpoint"
    local curl_cmd="curl -s -X $method"
    
    if [ "$content_type" != "" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: $content_type'"
    fi
    
    if [ "$data" != "" ]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    curl_cmd="$curl_cmd '$url'"
    
    print_step "Making $method request to $endpoint"
    echo "Command: $curl_cmd"
    echo "Response:"
    eval $curl_cmd | jq '.' 2>/dev/null || eval $curl_cmd
    echo ""
}

# Función para crear archivos de prueba
create_test_files() {
    print_step "Creating test files..."
    
    # Crear archivo DEM de prueba (simulado)
    echo "DEM file content for testing" > test_match.dem
    
    # Crear archivo de video de prueba (simulado)
    echo "Video file content for testing" > test_video.mp4
    
    # Crear metadata de prueba
    cat > test_metadata.json << EOF
{
  "playerName": "TestPlayer",
  "notes": "This is a test match for Tacti-Core"
}
EOF
    
    print_status "Test files created: test_match.dem, test_video.mp4, test_metadata.json"
}

# Función para probar upload de match
test_match_upload() {
    print_step "Testing match upload endpoint..."
    
    # Crear archivos de prueba si no existen
    if [ ! -f "test_match.dem" ]; then
        create_test_files
    fi
    
    # Hacer request de upload
    local url="$BASE_URL/api/matches"
    print_step "Uploading match to $url"
    
    # Usar curl para multipart form data
    response=$(curl -s -X POST \
        -F "demFile=@test_match.dem" \
        -F "videoFile=@test_video.mp4" \
        -F "metadata=@test_metadata.json" \
        "$url")
    
    echo "Response:"
    echo "$response" | jq '.' 2>/dev/null || echo "$response"
    echo ""
    
    # Extraer match ID de la respuesta
    match_id=$(echo "$response" | jq -r '.id' 2>/dev/null || echo "unknown")
    print_status "Match ID: $match_id"
    
    # Guardar match ID para pruebas posteriores
    echo "$match_id" > .last_match_id
}

# Función para probar status de match
test_match_status() {
    local match_id=$1
    
    if [ "$match_id" = "" ]; then
        if [ -f ".last_match_id" ]; then
            match_id=$(cat .last_match_id)
        else
            print_warning "No match ID provided and no previous match found"
            return
        fi
    fi
    
    print_step "Testing match status endpoint for ID: $match_id"
    make_request "GET" "/api/matches/$match_id"
}

# Función para probar health check
test_health() {
    print_step "Testing health check endpoint..."
    make_request "GET" "/api/health"
}

# Función para probar endpoint hello
test_hello() {
    print_step "Testing hello endpoint..."
    make_request "GET" "/hello"
}

# Función para limpiar archivos de prueba
cleanup() {
    print_step "Cleaning up test files..."
    rm -f test_match.dem test_video.mp4 test_metadata.json .last_match_id
    print_status "Cleanup completed"
}

# Función para mostrar ayuda
show_help() {
    echo "Tacti-Core API Test Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  health          Test health check endpoint"
    echo "  hello           Test hello endpoint"
    echo "  upload          Test match upload endpoint"
    echo "  status [ID]     Test match status endpoint"
    echo "  all             Run all tests"
    echo "  cleanup         Remove test files"
    echo "  help            Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 health"
    echo "  $0 upload"
    echo "  $0 status match_12345678"
    echo "  $0 all"
}

# Verificar si jq está instalado
if ! command -v jq &> /dev/null; then
    print_warning "jq is not installed. Installing with brew..."
    brew install jq
fi

# Procesar argumentos
case "${1:-all}" in
    "health")
        test_health
        ;;
    "hello")
        test_hello
        ;;
    "upload")
        test_match_upload
        ;;
    "status")
        test_match_status "$2"
        ;;
    "all")
        print_status "Running all tests..."
        test_health
        test_hello
        test_match_upload
        test_match_status
        ;;
    "cleanup")
        cleanup
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        print_warning "Unknown command: $1"
        show_help
        exit 1
        ;;
esac

print_status "Testing completed!"
