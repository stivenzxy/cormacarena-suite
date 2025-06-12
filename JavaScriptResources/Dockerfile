# Usa una imagen base de Node.js
FROM node:18-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo package.json y package-lock.json desde la raíz del proyecto
COPY ../package.json ../package-lock.json /app/

# Instala las dependencias de Node.js
RUN npm install

# Copia los archivos del worker (VerificacionPago.js) al contenedor
COPY ./workers /app/

# Expone el puerto si es necesario (esto depende de tu configuración)
EXPOSE 3000

# Comando para ejecutar el worker
CMD ["node", "/VerificacionPago.js"]
