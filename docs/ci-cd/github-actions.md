# 🚀 GitHub Actions

> Configuración y uso de GitHub Actions para CI/CD

## 🔧 Configuración

### Workflow Principal

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
    - name: Checkout code
      uses: actions/checkout@v4
    
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

### Workflow de Despliegue

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
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build for Lambda
      run: mvn clean package -DskipTests -P lambda
    
    - name: Configure AWS credentials
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

## 📊 Calidad de Código

### Análisis con SpotBugs

```yaml
- name: Run SpotBugs
  run: mvn spotbugs:check

- name: Upload SpotBugs report
  uses: actions/upload-artifact@v4
  with:
    name: spotbugs-report
    path: target/spotbugsXml.xml
```

### Cobertura con JaCoCo

```yaml
- name: Generate JaCoCo report
  run: mvn jacoco:report

- name: Upload JaCoCo report
  uses: actions/upload-artifact@v4
  with:
    name: jacoco-report
    path: target/site/jacoco/
```

### Verificación de Seguridad

```yaml
- name: Run OWASP Dependency Check
  run: mvn org.owasp:dependency-check-maven:check

- name: Upload security report
  uses: actions/upload-artifact@v4
  with:
    name: security-report
    path: target/dependency-check-report.html
```

## 🚀 Despliegue

### AWS Lambda

```yaml
- name: Deploy to AWS Lambda
  run: |
    aws lambda update-function-code \
      --function-name tacticore-backend \
      --zip-file fileb://target/tacticore-backend-1.0.0.jar
    
    aws lambda update-function-configuration \
      --function-name tacticore-backend \
      --environment Variables='{SPRING_PROFILES_ACTIVE=lambda}'
```

### Terraform

```yaml
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

### Métricas

```yaml
- name: Collect metrics
  run: |
    echo "Build time: $(date)"
    echo "Java version: $(java -version)"
    echo "Maven version: $(mvn -version)"
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
