# 📊 CI/CD

> Pipeline de integración continua y despliegue

## 🚀 Pipeline de CI/CD

### GitHub Actions

```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v4
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Build with Maven
      run: mvn clean compile -P dev
    
    - name: Run tests
      run: mvn test
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage reports
      uses: actions/upload-artifact@v4
      with:
        name: coverage-report
        path: target/site/jacoco/
```

## 🔧 Configuración

### Variables de Entorno

```yaml
# .github/workflows/ci.yml
env:
  MAVEN_OPTS: -Xmx1024m
  JAVA_OPTS: -Xmx1024m
```

### Secrets de GitHub

```bash
# Configurar secrets en GitHub
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_REGION=us-east-1
```

## 📊 Calidad de Código

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

## 🚀 Despliegue

### AWS Lambda

```yaml
# .github/workflows/deploy.yml
name: Deploy to AWS Lambda

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build for Lambda
      run: mvn clean package -DskipTests -P lambda
    
    - name: Deploy to AWS Lambda
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ secrets.AWS_REGION }}
    
    - name: Update Lambda function
      run: |
        aws lambda update-function-code \
          --function-name tacticore-backend \
          --zip-file fileb://target/tacticore-backend-1.0.0.jar
```

### Terraform

```yaml
# .github/workflows/terraform.yml
name: Terraform Deploy

on:
  push:
    branches: [ main ]
    paths: [ 'terraform/**' ]

jobs:
  terraform:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Setup Terraform
      uses: hashicorp/setup-terraform@v3
      with:
        terraform_version: 1.5.0
    
    - name: Terraform Init
      run: |
        cd terraform/environments/dev
        terraform init
    
    - name: Terraform Plan
      run: |
        cd terraform/environments/dev
        terraform plan
    
    - name: Terraform Apply
      run: |
        cd terraform/environments/dev
        terraform apply -auto-approve
```

## 📊 Monitoreo

### Notificaciones

```yaml
# .github/workflows/notifications.yml
name: Notifications

on:
  workflow_run:
    workflows: ["CI/CD Pipeline"]
    types: [completed]

jobs:
  notify:
    runs-on: ubuntu-latest
    if: always()
    
    steps:
    - name: Notify on Success
      if: ${{ github.event.workflow_run.conclusion == 'success' }}
      run: echo "✅ Build successful!"
    
    - name: Notify on Failure
      if: ${{ github.event.workflow_run.conclusion == 'failure' }}
      run: echo "❌ Build failed!"
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Error de Build

```bash
# Verificar logs de GitHub Actions
# Ir a Actions tab en GitHub

# Verificar configuración local
make ci
```

#### Error de Despliegue

```bash
# Verificar credenciales de AWS
aws configure list

# Verificar estado de Lambda
aws lambda get-function --function-name tacticore-backend
```

#### Error de Tests

```bash
# Ejecutar tests localmente
make test

# Verificar cobertura
make test-coverage
```

## 📚 Recursos Adicionales

### Documentación

- [GitHub Actions](https://docs.github.com/en/actions)
- [AWS Lambda](https://docs.aws.amazon.com/lambda/)
- [Terraform](https://www.terraform.io/docs/)

### Herramientas

- [GitHub Actions](https://github.com/features/actions)
- [AWS CLI](https://aws.amazon.com/cli/)
- [Terraform CLI](https://www.terraform.io/downloads.html)

### Enlaces Útiles

- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [AWS Console](https://console.aws.amazon.com/)
- [Terraform Registry](https://registry.terraform.io/)
