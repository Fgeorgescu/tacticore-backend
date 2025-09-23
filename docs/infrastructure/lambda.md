# ⚡ AWS Lambda

> Configuración y despliegue en AWS Lambda

## 🚀 Configuración

### Handler Principal

```java
// LambdaHandler.java
package com.tacticore.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class LambdaHandler implements RequestHandler<Object, Object> {
    
    private static ConfigurableApplicationContext applicationContext;
    
    static {
        applicationContext = SpringApplication.run(LambdaApplication.class);
    }
    
    @Override
    public Object handleRequest(Object input, Context context) {
        // Lógica de manejo de requests
        return "Hello from Lambda!";
    }
}
```

### Configuración de Spring Boot

```java
// LambdaApplication.java
package com.tacticore.lambda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LambdaApplication {
    public static void main(String[] args) {
        SpringApplication.run(LambdaApplication.class, args);
    }
}
```

## 🔧 Configuración de Lambda

### Variables de Entorno

```yaml
# application.yml
spring:
  profiles:
    active: lambda
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

server:
  port: 8080

logging:
  level:
    com.tacticore: INFO
    org.springframework: WARN
```

### Configuración de Maven

```xml
<!-- pom.xml -->
<profiles>
    <profile>
        <id>lambda</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <mainClass>com.tacticore.lambda.LambdaHandler</mainClass>
                        <excludes>
                            <exclude>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-tomcat</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## 📦 Empaquetado

### Comando de Empaquetado

```bash
# Empaquetar para Lambda
make lambda-package

# O manualmente
mvn clean package -DskipTests -P lambda
```

### Estructura del JAR

```
target/
└── tacticore-backend-1.0.0.jar
    ├── BOOT-INF/
    │   ├── classes/
    │   └── lib/
    ├── META-INF/
    └── org/
```

## 🚀 Despliegue

### Con Terraform

```bash
# Desplegar infraestructura
make terraform-apply

# Verificar despliegue
aws lambda get-function --function-name tacticore-backend
```

### Con AWS CLI

```bash
# Crear función Lambda
aws lambda create-function \
  --function-name tacticore-backend \
  --runtime java17 \
  --role arn:aws:iam::123456789012:role/lambda-role \
  --handler com.tacticore.lambda.LambdaHandler::handleRequest \
  --zip-file fileb://target/tacticore-backend-1.0.0.jar

# Actualizar función
aws lambda update-function-code \
  --function-name tacticore-backend \
  --zip-file fileb://target/tacticore-backend-1.0.0.jar
```

## 🔧 Configuración de API Gateway

### Integración con Lambda

```yaml
# api-gateway-config.yml
api_gateway:
  name: tacticore-api
  stage: dev
  routes:
    - path: /{proxy+}
      method: ANY
      integration: lambda-proxy
      lambda_function: tacticore-backend
```

### Configuración de CORS

```java
// WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

## 📊 Monitoreo

### CloudWatch Logs

```bash
# Ver logs de Lambda
aws logs describe-log-groups --log-group-name-prefix /aws/lambda

# Ver logs específicos
aws logs tail /aws/lambda/tacticore-backend --follow
```

### Métricas

```bash
# Ver métricas de Lambda
aws cloudwatch get-metric-statistics \
  --namespace AWS/Lambda \
  --metric-name Duration \
  --dimensions Name=FunctionName,Value=tacticore-backend \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-02T00:00:00Z \
  --period 3600 \
  --statistics Average
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Timeout

```bash
# Verificar configuración de timeout
aws lambda get-function-configuration --function-name tacticore-backend

# Aumentar timeout
aws lambda update-function-configuration \
  --function-name tacticore-backend \
  --timeout 60
```

#### Error de Memoria

```bash
# Verificar uso de memoria
aws lambda get-function-configuration --function-name tacticore-backend

# Aumentar memoria
aws lambda update-function-configuration \
  --function-name tacticore-backend \
  --memory-size 1024
```

#### Error de Permisos

```bash
# Verificar rol de Lambda
aws lambda get-function --function-name tacticore-backend

# Verificar políticas del rol
aws iam list-attached-role-policies --role-name lambda-role
```

## 📚 Recursos Adicionales

### Documentación

- [AWS Lambda Documentation](https://docs.aws.amazon.com/lambda/)
- [Spring Boot on AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
- [AWS Lambda Best Practices](https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html)

### Herramientas

- [AWS CLI](https://aws.amazon.com/cli/)
- [AWS SAM](https://aws.amazon.com/serverless/sam/)
- [Serverless Framework](https://www.serverless.com/)

### Enlaces Útiles

- [AWS Lambda Console](https://console.aws.amazon.com/lambda/)
- [AWS CloudWatch](https://console.aws.amazon.com/cloudwatch/)
- [AWS IAM](https://console.aws.amazon.com/iam/)
