# 🌿 Cormacarena Suite

**Cormacarena Suite** es un sistema integral diseñado para gestionar los procesos y operaciones de la **Corporación para el Desarrollo Sostenible del Área de Manejo Especial La Macarena (Cormacarena)**.  
Este proyecto modular abarca desde la gestión de procesos empresariales hasta interfaces de usuario, proporcionando una solución completa para las necesidades organizativas de Cormacarena.

---

## ✨ Características Principales

- ⚙️ **Arquitectura Modular**  
  El proyecto está dividido en varios módulos, cada uno responsable de una funcionalidad específica, lo que facilita el mantenimiento y la escalabilidad.

- 🔄 **Gestión de Procesos Empresariales (BPM)**  
  Incluye un motor BPM para modelar, ejecutar y monitorear procesos de negocio.

- 👥 **Cliente y Organización**  
  Módulos dedicados a la gestión de clientes y estructuras organizativas, permitiendo una administración eficiente de usuarios y departamentos.

- 🗂️ **Modelo de Datos Centralizado**  
  Un módulo que define las entidades y relaciones utilizadas en todo el sistema, asegurando la coherencia de los datos.

- 💻 **Interfaz de Usuario (Frontend)**  
  Una interfaz web desarrollada con tecnologías modernas para una experiencia de usuario intuitiva y responsiva.

---

## 🧱 Estructura del Proyecto
cormacarena-suite/

│
├── BPM-Engine/ # Motor de procesos de negocio (BPM)

├── cormacarena-client/ # Módulo de gestión de clientes

├── cormacarena-modelo/ # Modelo de datos central del sistema

├── cormacarena-organization/# Gestión de estructura organizativa

├── transactional-mail-sender/# Software transaccional para envío de notificaciones

└── frontend/ # Interfaz web de usuario

---

## 🚀 Tecnologías Utilizadas

- **Backend**: Java
- **Frontend**: HTML, CSS, JavaScript, Tailwind
- **Gestión de dependencias**: Maven (`pom.xml`)

---

## 🐳 Dockerización del Proyecto

Este proyecto está completamente dockerizado, lo que facilita su despliegue y ejecución en cualquier entorno que soporte Docker. A continuación, se detallan los servicios que componen la aplicación y cómo ejecutarlos.

### 🛠️ Servicios Docker

El archivo `docker-compose.yml` contiene la configuración para todos los servicios necesarios para ejecutar la aplicación, incluyendo bases de datos y aplicaciones de backend.

### 🗃️ Configuración de la Base de Datos
Para el correcto funcionamiento de la aplicación, es necesario contar con una base de datos MySQL local con las siguientes características:

Nombre de la base de datos: Cormacarena

Usuario: root

Contraseña: admin

Puedes crear esta base de datos ejecutando el siguiente script en tu cliente de MySQL:

sql
Copiar
Editar
CREATE DATABASE Cormacarena;
CREATE USER 'root'@'%' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON Cormacarena.* TO 'root'@'%';
FLUSH PRIVILEGES;

Adicional realice estos cambios en el docker-compose:

version: "3.8"

services:

  mysql:
    image: mysql:8.0
    container_name: mysql-cormacarena
    environment:
      MYSQL_DATABASE: cormacarena
      MYSQL_ROOT_PASSWORD: admin
    ports:
      - "3307:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - cormanet

  bpm-engine:
    build:
      context: ./BPM-Engine
    container_name: bpm-engine
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - cormanet

  client:
    build:
      context: ./cormacarena-client
    container_name: cormacarena-client
    ports:
      - "9090:9090"
    depends_on:
      - bpm-engine
      - mysql
    networks:
      - cormanet
    environment:
      CAMUNDA_URL: http://bpm-engine:8080/engine-rest/


  organization:
    build:
      context: ./cormacarena-organization
    container_name: cormacarena-organization
    ports:
      - "9095:9095"
    depends_on:
      - bpm-engine
      - mysql
    networks:
      - cormanet
    environment:
      CAMUNDA_URL: http://bpm-engine:8080/engine-rest/

  transactional-mail-sender:
    build:
      context: ./transaccional-mail-sender  # Directorio donde está el Dockerfile de este módulo
    container_name: transactional-mail-sender
    ports:
      - "8085:8085"  # Puerto expuesto para el servicio de envío de correos
    depends_on:
      - mysql
    networks:
      - cormanet
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_SENDGRID_API_KEY: "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"
      SENDGRID_FROM_EMAIL: "cormacarenaswproject@outlook.com"

  camunda-worker:
    build:
      context: ./JavaScriptResources
    container_name: camunda-worker
    depends_on:
      - bpm-engine  # Asegura que bpm-engine esté disponible
    environment:
      CAMUNDA_URL: http://bpm-engine:8080/engine-rest/
    networks:
      - cormanet

volumes:
  mysql-data:

networks:
  cormanet:
