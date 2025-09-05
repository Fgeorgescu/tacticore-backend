#!/bin/bash

# Script para probar filtros por usuario
BASE_URL="http://localhost:8080"

echo "=== Probando Filtros por Usuario ==="
echo

# Esperar a que la aplicación esté lista
echo "Esperando a que la aplicación esté lista..."
sleep 3

# Probar obtener todos los usuarios
echo "1. Obteniendo lista de usuarios..."
curl -s -X GET "$BASE_URL/api/analysis/users" | jq .
echo

# Probar análisis por usuario específico
echo "2. Análisis del usuario 'jcobbb'..."
curl -s -X GET "$BASE_URL/api/analysis/user/jcobbb/overview" | jq '.total_kills, .headshot_rate, .top_players[0]'
echo

# Probar kills de un usuario específico
echo "3. Kills del usuario 'jcobbb'..."
curl -s -X GET "$BASE_URL/api/analysis/user/jcobbb/kills" | jq '.user, .kills | length'
echo

# Probar kills de un usuario en una ronda específica
echo "4. Kills del usuario 'jcobbb' en la ronda 1..."
curl -s -X GET "$BASE_URL/api/analysis/user/jcobbb/round/1" | jq '.user, .round, .kills | length'
echo

# Probar matches filtrados por usuario
echo "5. Matches filtrados por usuario 'jcobbb'..."
curl -s -X GET "$BASE_URL/api/matches?user=jcobbb" | jq '.filteredBy, .matches | length'
echo

# Probar kills de un match filtrados por usuario
echo "6. Kills del match 1 filtrados por usuario 'jcobbb'..."
curl -s -X GET "$BASE_URL/api/matches/1/kills?user=jcobbb" | jq '.matchId, .filteredBy, .kills | length'
echo

# Probar con otro usuario
echo "7. Análisis del usuario 'makazze'..."
curl -s -X GET "$BASE_URL/api/analysis/user/makazze/overview" | jq '.total_kills, .headshot_rate, .weapon_stats[0]'
echo

# Probar usuario que no existe
echo "8. Probando usuario que no existe 'nonexistent'..."
curl -s -X GET "$BASE_URL/api/analysis/user/nonexistent/overview" | jq .
echo

# Probar análisis general vs análisis por usuario
echo "9. Comparando análisis general vs usuario específico..."
echo "Análisis general:"
curl -s -X GET "$BASE_URL/api/analysis/overview" | jq '.total_kills, .headshot_rate'
echo "Análisis de jcobbb:"
curl -s -X GET "$BASE_URL/api/analysis/user/jcobbb/overview" | jq '.total_kills, .headshot_rate'
echo

echo "=== Pruebas de filtros por usuario completadas ==="
