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