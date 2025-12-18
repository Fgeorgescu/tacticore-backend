# ============================================
# VARIABLES DE ENTORNO
# ============================================
# Configurar antes de iniciar la aplicación

# URL del servicio de ML (opcional - default: http://3.91.78.196:8000)
export ML_SERVICE_URL=http://3.91.78.196:8000

# Nombre del parámetro de archivo para el servicio ML (opcional - default: demo_file)
# export ML_SERVICE_FILE_PARAM_NAME=demo_file

# Puerto del servidor (opcional - default: 8080)
# export SERVER_PORT=8080

# Modo simulación - usar JSONs locales en lugar del servicio ML (opcional - default: false)
# export SIMULATION_ENABLED=false

# Directorio de JSONs para simulación (opcional - default: demos-jsons)
# export SIMULATION_JSON_DIRECTORY=demos-jsons

# Región de AWS para S3 (opcional - default: us-east-1)
# export AWS_REGION=us-east-1

# ============================================
# INSTRUCCIONES DE REDEPLOY
# ============================================

cd ~/tacticore-backend

# Bajar los cambios
git pull

# Matar el proceso actual (si hay alguno)
pkill -f java

# Recompilar
mvn clean package -DskipTests

# Iniciar
nohup java -jar target/tacticore-backend-1.0.0.jar > app.log 2>&1 &

# Verificar que arrancó (esperar ~30 seg)
tail -f app.log