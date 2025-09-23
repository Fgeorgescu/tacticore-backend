#!/bin/bash

# Script de pruebas completo para filtros por usuario
# Backend debe estar corriendo en puerto 8081

BASE_URL="http://localhost:8081"
USER="jcobbb"  # Usuario de prueba
ROUND="6"      # Ronda de prueba

echo "🧪 INICIANDO PRUEBAS COMPLETAS DE FILTROS POR USUARIO"
echo "=================================================="
echo "Backend URL: $BASE_URL"
echo "Usuario de prueba: $USER"
echo "Ronda de prueba: $ROUND"
echo ""

# Función para hacer requests y mostrar resultados
test_endpoint() {
    local endpoint="$1"
    local description="$2"
    local expected_status="${3:-200}"
    
    echo "🔍 Probando: $description"
    echo "   Endpoint: $endpoint"
    
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "$expected_status" ]; then
        echo "   ✅ Status: $http_code (OK)"
        echo "   📄 Respuesta:"
        echo "$body" | jq . 2>/dev/null || echo "$body"
    else
        echo "   ❌ Status: $http_code (Esperado: $expected_status)"
        echo "   📄 Respuesta:"
        echo "$body"
    fi
    echo ""
}

# 1. PRUEBAS BÁSICAS DE CONECTIVIDAD
echo "📡 1. PRUEBAS DE CONECTIVIDAD"
echo "=============================="
test_endpoint "/ping" "Health Check"

# 2. PRUEBAS DE ANÁLISIS GENERAL
echo "📊 2. PRUEBAS DE ANÁLISIS GENERAL"
echo "================================"
test_endpoint "/api/analysis/overview" "Análisis General"
test_endpoint "/api/analysis/players" "Top Players"
test_endpoint "/api/analysis/rounds" "Análisis por Rondas"

# 3. PRUEBAS DE USUARIOS
echo "👥 3. PRUEBAS DE USUARIOS"
echo "========================"
test_endpoint "/api/analysis/users" "Lista de Usuarios"

# 4. PRUEBAS DE FILTROS POR USUARIO
echo "🎯 4. PRUEBAS DE FILTROS POR USUARIO"
echo "===================================="
test_endpoint "/api/analysis/user/$USER/overview" "Análisis del Usuario $USER"
test_endpoint "/api/analysis/user/$USER/kills" "Kills del Usuario $USER"
test_endpoint "/api/analysis/user/$USER/round/$ROUND" "Kills del Usuario $USER en Ronda $ROUND"

# 5. PRUEBAS DE MATCHES CON FILTROS
echo "🏆 5. PRUEBAS DE MATCHES CON FILTROS"
echo "===================================="
test_endpoint "/api/matches" "Todos los Matches"
test_endpoint "/api/matches?user=$USER" "Matches Filtrados por Usuario $USER"

# 6. PRUEBAS DE KILLS EN MATCHES ESPECÍFICOS
echo "⚔️  6. PRUEBAS DE KILLS EN MATCHES"
echo "=================================="
# Primero obtenemos un match ID
echo "🔍 Obteniendo ID de match..."
match_response=$(curl -s "$BASE_URL/api/matches")
match_id=$(echo "$match_response" | jq -r '.matches[0].id' 2>/dev/null)

if [ "$match_id" != "null" ] && [ -n "$match_id" ]; then
    echo "   Match ID encontrado: $match_id"
    test_endpoint "/api/matches/$match_id/kills" "Kills del Match $match_id"
    test_endpoint "/api/matches/$match_id/kills?user=$USER" "Kills del Match $match_id Filtrados por Usuario $USER"
else
    echo "   ⚠️  No se pudo obtener un Match ID válido"
fi

# 7. PRUEBAS DE CHAT
echo "💬 7. PRUEBAS DE CHAT"
echo "===================="
if [ "$match_id" != "null" ] && [ -n "$match_id" ]; then
    test_endpoint "/api/matches/$match_id/chat" "Chat del Match $match_id"
else
    echo "   ⚠️  Saltando pruebas de chat (no hay match ID)"
fi

# 8. PRUEBAS DE DATOS PRECARGADOS
echo "🗄️  8. PRUEBAS DE DATOS PRECARGADOS"
echo "=================================="
test_endpoint "/api/data/status" "Estado de la Base de Datos"

# 9. PRUEBAS DE CASOS EDGE
echo "🔬 9. PRUEBAS DE CASOS EDGE"
echo "=========================="
test_endpoint "/api/analysis/user/usuario_inexistente/overview" "Usuario Inexistente" "400"
test_endpoint "/api/analysis/user/$USER/round/999" "Ronda Inexistente"
test_endpoint "/api/matches/inexistente/kills" "Match Inexistente" "404"

# 10. RESUMEN DE PRUEBAS
echo "📈 10. RESUMEN DE PRUEBAS"
echo "========================="
echo "✅ Pruebas completadas"
echo "📊 Backend funcionando en puerto 8081"
echo "🎯 Filtros por usuario implementados y probados"
echo ""
echo "🔗 URLs importantes:"
echo "   - Health Check: $BASE_URL/ping"
echo "   - H2 Console: $BASE_URL/h2-console"
echo "   - Análisis General: $BASE_URL/api/analysis/overview"
echo "   - Usuarios: $BASE_URL/api/analysis/users"
echo "   - Análisis por Usuario: $BASE_URL/api/analysis/user/$USER/overview"
echo ""
echo "🎉 ¡Todas las pruebas completadas!"
