# Interfaces TypeScript para Frontend - API de Análisis de Kills

## Configuración Base

```typescript
// Configuración de la API
const API_CONFIG = {
  baseUrl: 'http://localhost:8080',
  endpoints: {
    overview: '/api/analysis/overview',
    player: '/api/analysis/player',
    round: '/api/analysis/round',
    dataLoad: '/api/data/load',
    dataClear: '/api/data/clear',
    dataStatus: '/api/data/status'
  }
} as const;
```

---

## Interfaces Principales

### 1. Análisis General

```typescript
interface WeaponStat {
  weapon: string;
  count: number;
  percentage?: number;
}

interface LocationStat {
  location: string;
  count: number;
  percentage?: number;
}

interface RoundStat {
  round: number;
  kills: number;
  percentage?: number;
}

interface SideStat {
  side: string | null;
  count: number;
  percentage?: number;
}

interface TopPlayer {
  player: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
  headshots?: number;
  headshot_rate?: number;
  favorite_weapon?: string;
}

interface PredictionStat {
  label: string;
  count: number;
  percentage?: number;
  average_confidence: number;
}

interface KillAnalysisOverview {
  total_kills: number;
  total_headshots: number;
  headshot_rate: number;
  average_distance: number;
  average_time_in_round: number;
  weapon_stats: WeaponStat[];
  location_stats: LocationStat[];
  round_stats: RoundStat[];
  side_stats: SideStat[];
  top_players: TopPlayer[];
  prediction_stats: PredictionStat[];
}
```

### 2. Análisis de Jugador

```typescript
interface WeaponBreakdown {
  weapon: string;
  kills: number;
  headshots: number;
  headshot_rate: number;
}

interface RoundPerformance {
  round: number;
  kills: number;
  deaths: number;
  kd_ratio: number | null;
}

interface LocationPerformance {
  location: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
}

interface PlayerStats {
  player_name: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
  headshots: number;
  headshot_rate: number;
  average_distance: number;
  favorite_weapon: string;
  performance_score: number;
  weapon_breakdown?: WeaponBreakdown[];
  round_performance?: RoundPerformance[];
  location_performance?: LocationPerformance[];
}
```

### 3. Análisis de Ronda

```typescript
interface HotSpot {
  location: string;
  kills: number;
  percentage?: number;
}

interface WeaponDistribution {
  [weapon: string]: number;
}

interface CtTBalance {
  ct: number;
  t: number;
}

interface KillTimeline {
  time: number;
  attacker: string;
  victim: string;
  weapon: string;
  headshot: boolean;
  location: string;
}

interface PlayerPerformance {
  player: string;
  kills: number;
  deaths: number;
  kd_ratio: number;
  headshots: number;
  headshot_rate: number;
}

interface RoundAnalysis {
  round_number: number;
  total_kills: number;
  duration: number;
  most_active_player: string;
  hot_spots: HotSpot[];
  weapon_distribution: WeaponDistribution;
  ct_t_balance: CtTBalance;
  headshot_rate: number;
  average_distance: number;
  kill_timeline?: KillTimeline[];
  player_performance?: PlayerPerformance[];
}
```

### 4. Gestión de Datos

```typescript
interface DataLoadResponse {
  status: 'success' | 'error';
  message: string;
  timestamp?: string;
  data?: {
    total_kills_loaded?: number;
    total_predictions_loaded?: number;
    matches_loaded?: number;
  };
}

interface DataClearResponse {
  status: 'success' | 'error';
  message: string;
  timestamp?: string;
}

interface DataStatusResponse {
  status: 'ready' | 'error';
  message: string;
  timestamp?: string;
  data?: {
    total_kills: number;
    total_players: number;
    total_rounds: number;
    last_updated: string;
  };
}
```

### 5. Manejo de Errores

```typescript
interface ApiError {
  error: string;
  message: string;
  timestamp?: string;
}

interface ApiResponse<T> {
  data?: T;
  error?: ApiError;
  status: number;
}
```

---

## Servicios TypeScript

### Servicio Base de API

```typescript
class ApiService {
  private baseUrl: string;

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  private async request<T>(
    endpoint: string, 
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          ...options.headers
        },
        ...options
      });

      const data = await response.json();

      if (!response.ok) {
        return {
          error: data,
          status: response.status
        };
      }

      return {
        data,
        status: response.status
      };
    } catch (error) {
      return {
        error: {
          error: 'Network Error',
          message: error instanceof Error ? error.message : 'Unknown error'
        },
        status: 0
      };
    }
  }

  async get<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: 'GET' });
  }

  async post<T>(endpoint: string, body?: any): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: body ? JSON.stringify(body) : undefined
    });
  }

  async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}
```

### Servicio de Análisis

```typescript
class KillAnalysisService extends ApiService {
  async getOverview(): Promise<ApiResponse<KillAnalysisOverview>> {
    return this.get<KillAnalysisOverview>('/api/analysis/overview');
  }

  async getPlayerStats(playerName: string): Promise<ApiResponse<PlayerStats>> {
    const encodedName = encodeURIComponent(playerName);
    return this.get<PlayerStats>(`/api/analysis/player/${encodedName}`);
  }

  async getRoundAnalysis(roundNumber: number): Promise<ApiResponse<RoundAnalysis>> {
    return this.get<RoundAnalysis>(`/api/analysis/round/${roundNumber}`);
  }

  async loadData(fileName?: string): Promise<ApiResponse<DataLoadResponse>> {
    const endpoint = fileName 
      ? `/api/data/load?fileName=${encodeURIComponent(fileName)}`
      : '/api/data/load';
    return this.post<DataLoadResponse>(endpoint);
  }

  async clearData(): Promise<ApiResponse<DataClearResponse>> {
    return this.delete<DataClearResponse>('/api/data/clear');
  }

  async getDataStatus(): Promise<ApiResponse<DataStatusResponse>> {
    return this.get<DataStatusResponse>('/api/data/status');
  }
}
```

---

## Hooks de React (si usas React)

### Hook para Análisis General

```typescript
import { useState, useEffect } from 'react';

interface UseKillAnalysisReturn {
  data: KillAnalysisOverview | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useKillAnalysis(): UseKillAnalysisReturn {
  const [data, setData] = useState<KillAnalysisOverview | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const analysisService = new KillAnalysisService();
      const response = await analysisService.getOverview();
      
      if (response.error) {
        setError(response.error.message);
      } else {
        setData(response.data!);
      }
    } catch (err) {
      setError('Error fetching analysis data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return { data, loading, error, refetch: fetchData };
}
```

### Hook para Estadísticas de Jugador

```typescript
interface UsePlayerStatsReturn {
  data: PlayerStats | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export function usePlayerStats(playerName: string): UsePlayerStatsReturn {
  const [data, setData] = useState<PlayerStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    if (!playerName) return;
    
    try {
      setLoading(true);
      setError(null);
      
      const analysisService = new KillAnalysisService();
      const response = await analysisService.getPlayerStats(playerName);
      
      if (response.error) {
        setError(response.error.message);
      } else {
        setData(response.data!);
      }
    } catch (err) {
      setError('Error fetching player stats');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [playerName]);

  return { data, loading, error, refetch: fetchData };
}
```

### Hook para Análisis de Ronda

```typescript
interface UseRoundAnalysisReturn {
  data: RoundAnalysis | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useRoundAnalysis(roundNumber: number): UseRoundAnalysisReturn {
  const [data, setData] = useState<RoundAnalysis | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    if (!roundNumber || roundNumber < 1) return;
    
    try {
      setLoading(true);
      setError(null);
      
      const analysisService = new KillAnalysisService();
      const response = await analysisService.getRoundAnalysis(roundNumber);
      
      if (response.error) {
        setError(response.error.message);
      } else {
        setData(response.data!);
      }
    } catch (err) {
      setError('Error fetching round analysis');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [roundNumber]);

  return { data, loading, error, refetch: fetchData };
}
```

---

## Utilidades para Frontend

### Validadores

```typescript
export const validators = {
  playerName: (name: string): boolean => {
    const validPlayers = [
      'makazze', 'broky', 'jcobbb', 'rain', 'b1t',
      'iM', 'frozen', 'karrigan', 'w0nderful', 'Aleksib'
    ];
    return validPlayers.includes(name);
  },

  roundNumber: (round: number): boolean => {
    return Number.isInteger(round) && round >= 1 && round <= 21;
  },

  weapon: (weapon: string): boolean => {
    const validWeapons = [
      'ak47', 'm4a1_silencer', 'awp', 'glock', 'galilar',
      'deagle', 'm4a1', 'usp_silencer', 'elite', 'hegrenade',
      'tec9', 'famas', 'mac10', 'fiveseven', 'mp9'
    ];
    return validWeapons.includes(weapon);
  }
};
```

### Formateadores

```typescript
export const formatters = {
  kdRatio: (kills: number, deaths: number): string => {
    if (deaths === 0) return kills.toString();
    return (kills / deaths).toFixed(2);
  },

  percentage: (value: number, total: number): string => {
    return ((value / total) * 100).toFixed(1) + '%';
  },

  distance: (distance: number): string => {
    return distance.toFixed(2) + ' units';
  },

  time: (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  },

  performanceScore: (score: number): string => {
    return score.toFixed(1);
  }
};
```

### Constantes

```typescript
export const CONSTANTS = {
  VALID_PLAYERS: [
    'makazze', 'broky', 'jcobbb', 'rain', 'b1t',
    'iM', 'frozen', 'karrigan', 'w0nderful', 'Aleksib'
  ],
  
  VALID_ROUNDS: Array.from({length: 21}, (_, i) => i + 1),
  
  WEAPON_COLORS: {
    'ak47': '#FF6384',
    'm4a1_silencer': '#36A2EB',
    'awp': '#FFCE56',
    'glock': '#4BC0C0',
    'galilar': '#9966FF',
    'deagle': '#FF9F40',
    'm4a1': '#FF6384',
    'usp_silencer': '#C9CBCF',
    'elite': '#4BC0C0',
    'hegrenade': '#FF6384'
  },
  
  SIDE_COLORS: {
    'ct': '#4BC0C0',
    't': '#FF6384'
  },
  
  LOCATION_COLORS: {
    'BombsiteA': '#FF6384',
    'BombsiteB': '#36A2EB',
    'CTSpawn': '#4BC0C0',
    'Catwalk': '#FFCE56',
    'Underpass': '#9966FF',
    'Connector': '#FF9F40',
    'Unknown': '#C9CBCF'
  }
} as const;
```

---

## Ejemplo de Componente React

```typescript
import React from 'react';
import { useKillAnalysis } from './hooks/useKillAnalysis';

export const KillAnalysisDashboard: React.FC = () => {
  const { data, loading, error, refetch } = useKillAnalysis();

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!data) return <div>No data available</div>;

  return (
    <div className="dashboard">
      <h1>Kill Analysis Dashboard</h1>
      
      <div className="metrics">
        <div className="metric">
          <h3>Total Kills</h3>
          <p>{data.total_kills}</p>
        </div>
        
        <div className="metric">
          <h3>Headshot Rate</h3>
          <p>{data.headshot_rate.toFixed(1)}%</p>
        </div>
        
        <div className="metric">
          <h3>Average Distance</h3>
          <p>{data.average_distance.toFixed(2)}</p>
        </div>
      </div>

      <div className="charts">
        <WeaponChart data={data.weapon_stats} />
        <LocationChart data={data.location_stats} />
        <PlayerRanking data={data.top_players} />
      </div>
      
      <button onClick={refetch}>Refresh Data</button>
    </div>
  );
};
```

---

## Configuración de TypeScript

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["ES2020", "DOM"],
    "module": "ESNext",
    "moduleResolution": "node",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist"]
}
```

Esta documentación proporciona todo lo necesario para que el frontend pueda integrarse correctamente con la API de análisis de kills, incluyendo tipos TypeScript, servicios, hooks de React y utilidades.
