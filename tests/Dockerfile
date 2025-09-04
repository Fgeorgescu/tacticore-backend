# Dockerfile para Backend (Tests)
FROM node:18-alpine

WORKDIR /app

# Instalar dependencias del sistema y curl para health checks
RUN apk add --no-cache python3 make g++ curl

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar código fuente
COPY . .

# Exponer puerto
EXPOSE 8000

# Comando para desarrollo (ajustar según tu framework)
CMD ["npm", "run", "dev"]
