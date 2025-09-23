# 🗄️ Base de Datos

> Configuración y gestión de bases de datos

## 🏗️ Arquitectura

### Desarrollo Local

- **Base de Datos**: H2 (en memoria)
- **URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: `password`
- **Consola**: `http://localhost:8080/h2-console`

### Producción

- **Base de Datos**: DynamoDB
- **Región**: `us-east-1`
- **Modo**: On-demand

## 🔧 Configuración

### H2 (Desarrollo)

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

### DynamoDB (Producción)

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:dynamodb://localhost:8000
    username: dummy
    password: dummy
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

## 📊 Entidades

### MatchEntity

```java
@Entity
@Table(name = "matches")
public class MatchEntity {
    @Id
    private String id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "map_name")
    private String mapName;
    
    @Column(name = "total_kills")
    private Integer totalKills;
    
    @Column(name = "tickrate")
    private Integer tickrate;
    
    @Column(name = "has_video")
    private Boolean hasVideo;
    
    @Column(name = "status")
    private String status;
    
    // Getters y setters
}
```

### KillEntity

```java
@Entity
@Table(name = "kills")
public class KillEntity {
    @Id
    @Column(name = "kill_id")
    private String killId;
    
    @Column(name = "attacker")
    private String attacker;
    
    @Column(name = "victim")
    private String victim;
    
    @Column(name = "round")
    private Integer round;
    
    @Column(name = "weapon")
    private String weapon;
    
    @Column(name = "headshot")
    private Boolean headshot;
    
    @Column(name = "distance")
    private Double distance;
    
    @Column(name = "time_in_round")
    private Double timeInRound;
    
    // Getters y setters
}
```

### ChatMessageEntity

```java
@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_id")
    private String matchId;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Getters y setters
}
```

## 🔍 Repositorios

### KillRepository

```java
@Repository
public interface KillRepository extends JpaRepository<KillEntity, String> {
    
    // Consultas personalizadas
    @Query("SELECT k.weapon, COUNT(k) FROM KillEntity k GROUP BY k.weapon ORDER BY COUNT(k) DESC")
    List<Object[]> findWeaponUsageStats();
    
    @Query("SELECT k.attacker, COUNT(k) FROM KillEntity k GROUP BY k.attacker ORDER BY COUNT(k) DESC")
    List<Object[]> findKillsByAttacker();
    
    @Query("SELECT k.victim, COUNT(k) FROM KillEntity k GROUP BY k.victim ORDER BY COUNT(k) DESC")
    List<Object[]> findDeathsByVictim();
    
    // Consultas por usuario
    List<KillEntity> findByAttacker(String attacker);
    List<KillEntity> findByVictim(String victim);
    List<KillEntity> findByAttackerOrVictim(String attacker, String victim);
    
    // Consultas por ronda
    List<KillEntity> findByRound(Integer round);
    List<KillEntity> findByAttackerAndRound(String attacker, Integer round);
    
    // Estadísticas
    Long countByAttacker(String attacker);
    Long countByVictim(String victim);
    Long countByHeadshotTrue();
    Double getAverageDistance();
    Double getAverageTimeInRound();
}
```

### MatchRepository

```java
@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    
    List<MatchEntity> findByStatus(String status);
    List<MatchEntity> findByMapName(String mapName);
    List<MatchEntity> findByHasVideoTrue();
    
    @Query("SELECT m FROM MatchEntity m WHERE m.fileName LIKE %:fileName%")
    List<MatchEntity> findByFileNameContaining(@Param("fileName") String fileName);
}
```

## 🚀 Comandos de Base de Datos

### Desarrollo Local

```bash
# Abrir consola H2
make db-console

# Limpiar base de datos
make db-clear

# Recargar datos dummy
make db-reload
```

### Verificación

```bash
# Verificar estado de la base de datos
curl http://localhost:8080/api/data/status

# Verificar datos cargados
curl http://localhost:8080/api/matches
```

## 📊 Datos de Prueba

### Carga Automática

```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private DummyDataService dummyDataService;
    
    @Autowired
    private PreloadedDataService preloadedDataService;
    
    @Override
    public void run(String... args) throws Exception {
        // Cargar datos dummy
        dummyDataService.loadDummyData();
        
        // Cargar partida de prueba
        preloadedDataService.loadTestMatch();
        
        // Cargar kills desde example.json
        dataLoaderService.loadKillData();
    }
}
```

### Datos Incluidos

- **1 partida** de ejemplo
- **Múltiples kills** con diferentes usuarios
- **Mensajes de chat** de ejemplo
- **Datos de analytics** históricos
- **Mapas y armas** predefinidos

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Conexión

```bash
# Verificar que la aplicación esté ejecutándose
curl http://localhost:8080/ping

# Verificar logs de la aplicación
make logs
```

#### Error de Datos

```bash
# Limpiar y recargar datos
make db-clear
make db-reload
```

#### Error de H2 Console

```bash
# Verificar que H2 console esté habilitada
curl http://localhost:8080/h2-console

# Verificar configuración en application.yml
grep -A 5 "h2:" src/main/resources/application.yml
```

## 📚 Recursos Adicionales

### Documentación

- [H2 Database](https://www.h2database.com/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [DynamoDB](https://docs.aws.amazon.com/dynamodb/)

### Herramientas

- [DBeaver](https://dbeaver.io/) - Cliente de base de datos
- [H2 Console](http://localhost:8080/h2-console) - Consola web de H2
- [AWS DynamoDB Console](https://console.aws.amazon.com/dynamodb/)

### Enlaces Útiles

- [H2 Documentation](https://www.h2database.com/html/main.html)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [DynamoDB Documentation](https://docs.aws.amazon.com/dynamodb/)
