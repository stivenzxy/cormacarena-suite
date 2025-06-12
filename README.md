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
