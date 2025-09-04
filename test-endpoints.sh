#!/bin/bash

# Script de prueba para verificar todos los endpoints del backend
# Ejecutar después de levantar el servidor Spring Boot

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo "🧪 Probando endpoints del backend Tacticore"
echo "=========================================="
echo ""

# Health check
echo "1. Health Check:"
curl -s "$BASE_URL/ping" | jq .
echo ""

# Get matches
echo "2. Obtener lista de partidas:"
curl -s "$API_BASE/matches" | jq .
echo ""

# Get specific match
echo "3. Obtener partida específica (ID: 1):"
curl -s "$API_BASE/matches/1" | jq .
echo ""

# Get match kills
echo "4. Obtener kills de partida (ID: 1):"
curl -s "$API_BASE/matches/1/kills" | jq .
echo ""

# Get match chat
echo "5. Obtener chat de partida (ID: 1):"
curl -s "$API_BASE/matches/1/chat" | jq .
echo ""

# Send chat message
echo "6. Enviar mensaje al chat:"
curl -s -X POST "$API_BASE/matches/1/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Test message from script", "user": "TestUser"}' | jq .
echo ""

# Get dashboard stats
echo "7. Obtener estadísticas del dashboard:"
curl -s "$API_BASE/analytics/dashboard" | jq .
echo ""

# Get historical analytics
echo "8. Obtener analytics históricos:"
curl -s "$API_BASE/analytics/historical?timeRange=all&metric=kdr" | jq .
echo ""

# Get maps
echo "9. Obtener lista de mapas:"
curl -s "$API_BASE/maps" | jq .
echo ""

# Get weapons
echo "10. Obtener lista de armas:"
curl -s "$API_BASE/weapons" | jq .
echo ""

# Get user profile
echo "11. Obtener perfil de usuario:"
curl -s "$API_BASE/user/profile" | jq .
echo ""

# Update user profile
echo "12. Actualizar perfil de usuario:"
curl -s -X PUT "$API_BASE/user/profile" \
  -H "Content-Type: application/json" \
  -d '{"username": "updated_user", "email": "updated@example.com"}' | jq .
echo ""

# Test file upload (DEM)
echo "13. Probar subida de archivo DEM:"
curl -s -X POST "$API_BASE/upload/dem" \
  -F "file=@test_match.dem" \
  -F "metadata={\"map\":\"Dust2\",\"gameType\":\"Ranked\"}" | jq .
echo ""

# Test file upload (Video)
echo "14. Probar subida de archivo de video:"
curl -s -X POST "$API_BASE/upload/video" \
  -F "file=@test_video.mp4" \
  -F "matchId=match_123" | jq .
echo ""

# Test process match
echo "15. Probar procesamiento de partida:"
curl -s -X POST "$API_BASE/upload/process" \
  -H "Content-Type: application/json" \
  -d '{"matchId": "match_123", "analysisType": "full"}' | jq .
echo ""

echo "✅ Pruebas completadas!"
echo ""
echo "📊 Resumen de endpoints probados:"
echo "  - Health check: /ping"
echo "  - Matches: /api/matches"
echo "  - Match details: /api/matches/{id}"
echo "  - Match kills: /api/matches/{id}/kills"
echo "  - Match chat: /api/matches/{id}/chat"
echo "  - Dashboard stats: /api/analytics/dashboard"
echo "  - Historical analytics: /api/analytics/historical"
echo "  - Maps: /api/maps"
echo "  - Weapons: /api/weapons"
echo "  - User profile: /api/user/profile"
echo "  - File uploads: /api/upload/dem, /api/upload/video"
echo "  - Process match: /api/upload/process"
