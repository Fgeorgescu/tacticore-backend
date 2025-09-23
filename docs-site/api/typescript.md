# 🔷 Interfaces TypeScript

> Definiciones TypeScript para el frontend

## 🎯 Tipos Principales

### KillAnalysisDto

```typescript
interface KillAnalysisDto {
  total_kills: number;
  total_headshots: number;
  headshot_rate: number;
  average_distance: number;
  average_time_in_round: number;
  weapon_stats: WeaponStat[];
  location_stats: LocationStat[];
  round_stats: RoundStat[];
  side_stats: SideStat[];
  top_players: PlayerStat[];
  prediction_stats: PredictionStat[];
}

interface WeaponStat {
  weapon: string;
  count: number;
}

interface LocationStat {
  location: string;
  count: number;
}

interface RoundStat {
  round: number;
  kills: number;
}

interface SideStat {
  side: 'ct' | 't';
  count: number;
}

interface PlayerStat {
  player: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
}

interface PredictionStat {
  label: string;
  count: number;
  average_confidence: number;
}
```

### PlayerStatsDto

```typescript
interface PlayerStatsDto {
  player_name: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
  headshots: number;
  headshot_rate: number;
  average_distance: number;
  favorite_weapon: string;
  performance_score: number;
}
```

### RoundAnalysisDto

```typescript
interface RoundAnalysisDto {
  round_number: number;
  total_kills: number;
  duration: number;
  most_active_player: string;
  hot_spots: HotSpot[];
  weapon_distribution: Record<string, number>;
  ct_t_balance: {
    ct: number;
    t: number;
  };
  headshot_rate: number;
  average_distance: number;
}

interface HotSpot {
  location: string;
  kills: number;
}
```

## 🎮 Tipos de Partidas

### MatchDto

```typescript
interface MatchDto {
  id: string;
  fileName: string;
  mapName: string;
  totalKills: number;
  tickrate: number;
  hasVideo: boolean;
  status: 'processing' | 'completed' | 'failed';
  score: number;
}
```

### KillDto

```typescript
interface KillDto {
  id: number;
  killer: string;
  victim: string;
  weapon: string;
  isGoodPlay: boolean;
  round: number;
  time: string;
  teamAlive: {
    ct: number;
    t: number;
  };
  position: string;
}
```

### ChatMessageDto

```typescript
interface ChatMessageDto {
  id: number;
  userName: string;
  message: string;
  createdAt: string; // ISO 8601 format
}
```

## 📊 Tipos de Analytics

### DashboardStats

```typescript
interface DashboardStats {
  totalMatches: number;
  totalKills: number;
  totalDeaths: number;
  totalGoodPlays: number;
  totalBadPlays: number;
  averageScore: number;
  kdr: number;
}
```

### AnalyticsData

```typescript
interface AnalyticsData {
  date: string; // YYYY-MM-DD format
  kills: number;
  deaths: number;
  kdr: number;
  score: number;
  goodPlays: number;
  badPlays: number;
  matches: number;
}
```

## 🗺️ Tipos de Configuración

### MapResponse

```typescript
interface MapResponse {
  maps: string[];
}
```

### WeaponResponse

```typescript
interface WeaponResponse {
  weapons: string[];
}
```

## 🚨 Tipos de Error

### ErrorResponse

```typescript
interface ErrorResponse {
  error: string;
  message: string;
  timestamp?: string;
}
```

### ApiResponse<T>

```typescript
interface ApiResponse<T> {
  data?: T;
  error?: ErrorResponse;
  status: number;
}
```

## 🔧 Tipos de Utilidad

### PaginationParams

```typescript
interface PaginationParams {
  page?: number;
  limit?: number;
  sort?: string;
  order?: 'asc' | 'desc';
}
```

### FilterParams

```typescript
interface FilterParams {
  user?: string;
  round?: number;
  weapon?: string;
  map?: string;
  dateFrom?: string;
  dateTo?: string;
}
```

## 📝 Ejemplos de Uso

### Función para Obtener Análisis

```typescript
async function getKillAnalysis(): Promise<KillAnalysisDto> {
  const response = await fetch('/api/analysis/overview');
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  return await response.json();
}
```

### Función para Obtener Estadísticas de Jugador

```typescript
async function getPlayerStats(playerName: string): Promise<PlayerStatsDto> {
  const response = await fetch(`/api/analysis/player/${encodeURIComponent(playerName)}`);
  
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error(`Player ${playerName} not found`);
    }
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  return await response.json();
}
```

### Función para Obtener Partidas con Filtros

```typescript
async function getMatches(filters?: FilterParams): Promise<MatchDto[]> {
  const params = new URLSearchParams();
  
  if (filters?.user) params.append('user', filters.user);
  if (filters?.map) params.append('map', filters.map);
  
  const response = await fetch(`/api/matches?${params.toString()}`);
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  const data = await response.json();
  return data.matches;
}
```

### Hook de React para Analytics

```typescript
import { useState, useEffect } from 'react';

function useDashboardStats() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchStats() {
      try {
        setLoading(true);
        const response = await fetch('/api/analytics/dashboard');
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        setStats(data);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unknown error');
      } finally {
        setLoading(false);
      }
    }

    fetchStats();
  }, []);

  return { stats, loading, error };
}
```

## 🎨 Tipos para Componentes

### Component Props

```typescript
interface PlayerCardProps {
  player: PlayerStatsDto;
  onClick?: (player: PlayerStatsDto) => void;
}

interface KillListProps {
  kills: KillDto[];
  onKillClick?: (kill: KillDto) => void;
  filters?: FilterParams;
}

interface MatchCardProps {
  match: MatchDto;
  onMatchClick?: (match: MatchDto) => void;
}
```

### State Types

```typescript
interface AppState {
  matches: MatchDto[];
  currentMatch: MatchDto | null;
  kills: KillDto[];
  analytics: DashboardStats | null;
  loading: boolean;
  error: string | null;
}

interface FilterState {
  user: string;
  round: number | null;
  weapon: string;
  map: string;
}
```

## 🔍 Tipos de Validación

### Validation Rules

```typescript
interface ValidationRule<T> {
  field: keyof T;
  required?: boolean;
  min?: number;
  max?: number;
  pattern?: RegExp;
  custom?: (value: any) => boolean;
}

interface ValidationResult {
  isValid: boolean;
  errors: Record<string, string[]>;
}
```

## 📚 Enums

### Match Status

```typescript
enum MatchStatus {
  PROCESSING = 'processing',
  COMPLETED = 'completed',
  FAILED = 'failed'
}
```

### Weapon Categories

```typescript
enum WeaponCategory {
  RIFLE = 'rifle',
  PISTOL = 'pistol',
  SNIPER = 'sniper',
  SMG = 'smg',
  SHOTGUN = 'shotgun',
  MACHINE_GUN = 'machine_gun'
}
```

### Team Sides

```typescript
enum TeamSide {
  CT = 'ct',
  T = 't'
}
```
