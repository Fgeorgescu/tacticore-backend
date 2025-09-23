# 🔍 Calidad de Código

> Herramientas y procesos para asegurar la calidad del código

## 🎯 Herramientas de Calidad

### SpotBugs

```xml
<!-- pom.xml -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### JaCoCo

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### OWASP Dependency Check

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 🚀 Comandos de Calidad

### Análisis Local

```bash
# Ejecutar análisis de calidad
make quality

# Análisis específico
mvn spotbugs:check
mvn jacoco:report
mvn org.owasp:dependency-check-maven:check
```

### Reportes

```bash
# Generar reportes
make test-coverage

# Ver reportes
open target/site/jacoco/index.html
open target/spotbugsXml.xml
open target/dependency-check-report.html
```

## 📊 Métricas de Calidad

### Cobertura de Código

- **Objetivo**: > 80%
- **Actual**: ~75%
- **Métricas**:
  - Líneas cubiertas
  - Ramas cubiertas
  - Métodos cubiertos
  - Clases cubiertas

### Análisis Estático

- **SpotBugs**: Detección de bugs potenciales
- **Checkstyle**: Estilo de código
- **PMD**: Reglas de código
- **SonarQube**: Análisis completo

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de SpotBugs

```bash
# Verificar configuración
mvn spotbugs:help

# Ejecutar con más detalle
mvn spotbugs:check -X
```

#### Error de JaCoCo

```bash
# Verificar configuración
mvn jacoco:help

# Generar reporte manualmente
mvn jacoco:report
```

#### Error de Dependency Check

```bash
# Verificar configuración
mvn org.owasp:dependency-check-maven:help

# Ejecutar con más detalle
mvn org.owasp:dependency-check-maven:check -X
```

## 📚 Recursos Adicionales

### Documentación

- [SpotBugs](https://spotbugs.github.io/)
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)

### Herramientas

- [SpotBugs](https://spotbugs.github.io/)
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)

### Enlaces Útiles

- [SpotBugs Documentation](https://spotbugs.github.io/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/)
- [OWASP Dependency Check Documentation](https://owasp.org/www-project-dependency-check/)
