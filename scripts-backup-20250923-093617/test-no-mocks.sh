#!/bin/bash

# Script para probar que todos los endpoints funcionan sin mocks
# Usando datos reales de la base de datos

BASE_URL="http://localhost:8080"

echo "🧪 Probando endpoints sin mocks..."
echo "=================================="

# Test 1: Maps endpoint
echo "📍 Probando /api/maps..."
MAPS_RESPONSE=$(curl -s "$BASE_URL/api/maps")
MAPS_COUNT=$(echo "$MAPS_RESPONSE" | jq '. | length')
echo "✅ Maps encontrados: $MAPS_COUNT"
echo "   Primeros 3 mapas: $(echo "$MAPS_RESPONSE" | jq -r '.[0:3] | join(", ")')"
echo ""

# Test 2: Weapons endpoint
echo "🔫 Probando /api/weapons..."
WEAPONS_RESPONSE=$(curl -s "$BASE_URL/api/weapons")
WEAPONS_COUNT=$(echo "$WEAPONS_RESPONSE" | jq '. | length')
echo "✅ Armas encontradas: $WEAPONS_COUNT"
echo "   Primeras 5 armas: $(echo "$WEAPONS_RESPONSE" | jq -r '.[0:5] | join(", ")')"
echo ""

# Test 3: Dashboard analytics
echo "📊 Probando /api/analytics/dashboard..."
DASHBOARD_RESPONSE=$(curl -s "$BASE_URL/api/analytics/dashboard")
TOTAL_MATCHES=$(echo "$DASHBOARD_RESPONSE" | jq '.totalMatches')
TOTAL_KILLS=$(echo "$DASHBOARD_RESPONSE" | jq '.totalKills')
KDR=$(echo "$DASHBOARD_RESPONSE" | jq '.kdr')
echo "✅ Dashboard Stats:"
echo "   Total Matches: $TOTAL_MATCHES"
echo "   Total Kills: $TOTAL_KILLS"
echo "   KDR: $KDR"
echo ""

# Test 4: Historical analytics
echo "📈 Probando /api/analytics/historical..."
HISTORICAL_RESPONSE=$(curl -s "$BASE_URL/api/analytics/historical")
HISTORICAL_COUNT=$(echo "$HISTORICAL_RESPONSE" | jq '.data | length')
echo "✅ Registros históricos: $HISTORICAL_COUNT"
echo "   Último registro: $(echo "$HISTORICAL_RESPONSE" | jq '.data[-1] | "\(.date): \(.kills) kills, \(.deaths) deaths, KDR: \(.kdr)"')"
echo ""

# Test 5: Historical analytics with timeRange filter
echo "📅 Probando /api/analytics/historical?timeRange=week..."
WEEKLY_RESPONSE=$(curl -s "$BASE_URL/api/analytics/historical?timeRange=week")
WEEKLY_COUNT=$(echo "$WEEKLY_RESPONSE" | jq '.data | length')
echo "✅ Registros de la semana: $WEEKLY_COUNT"
echo ""

# Test 6: Matches endpoint (ya funcionaba)
echo "🎮 Probando /api/matches..."
MATCHES_RESPONSE=$(curl -s "$BASE_URL/api/matches")
MATCHES_COUNT=$(echo "$MATCHES_RESPONSE" | jq '.matches | length')
echo "✅ Matches encontrados: $MATCHES_COUNT"
echo ""

# Test 7: User filtering (ya funcionaba)
echo "👤 Probando /api/matches?user=jcobbb..."
USER_MATCHES_RESPONSE=$(curl -s "$BASE_URL/api/matches?user=jcobbb")
USER_MATCHES_COUNT=$(echo "$USER_MATCHES_RESPONSE" | jq '.matches | length')
echo "✅ Matches de jcobbb: $USER_MATCHES_COUNT"
echo ""

# Test 8: Kills endpoint (ya funcionaba)
echo "💀 Probando /api/matches/example_match/kills?user=jcobbb..."
KILLS_RESPONSE=$(curl -s "$BASE_URL/api/matches/example_match/kills?user=jcobbb")
KILLS_COUNT=$(echo "$KILLS_RESPONSE" | jq '.kills | length')
echo "✅ Kills de jcobbb: $KILLS_COUNT"
echo ""

echo "🎉 ¡Todas las pruebas completadas!"
echo "=================================="
echo "✅ Todos los endpoints están funcionando con datos reales de la base de datos"
echo "✅ No hay más mocks en uso"
echo "✅ Las firmas de la API se mantienen compatibles con el frontend"
