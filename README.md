Markdown
# DilanMotos - Sistema de Gestión e Inventario Inteligente

**DilanMotos** es un ecosistema full-stack (Web, Móvil y Backend) diseñado para la gestión operativa, control de inventarios, agendamiento de servicios mecánicos y ventas en talleres de motocicletas. 

Cuenta con una arquitectura modular desacoplada e integración con modelos de Inteligencia Artificial (Groq / Gemini) para la asistencia técnica automatizada.

---

## Tecnologías Utilizadas

* **Backend:** Java 17 + Spring Boot 3.x (Spring Security, JWT, JPA/Hibernate, Java Mail).
* **Base de Datos:** MySQL / MariaDB (Puerto default: `3308`).
* **Frontend Web:** React + Tailwind CSS + Axios.
* **App Móvil:** Kotlin / Android Studio + Retrofit.
* **Inteligencia Artificial:** Groq API / Google Gemini API.

---

## Arquitectura del Backend

El backend está desarrollado bajo **Arquitectura Hexagonal (Ports & Adapters)** para desacoplar las reglas del negocio de los frameworks y servicios externos:

```text
com.dilanmotos
├── domain/          # Entidades y reglas de negocio puras
├── application/     # Casos de uso (Use Cases)
├── ports/           # Interfaces de entrada y salida (Inbound/Outbound)
└── infrastructure/  # Adaptadores (Controladores REST, Repositorios JPA, Clientes IA)
Requisitos Previos
Asegúrate de contar con lo siguiente instalado en tu entorno local:

Java JDK 17 o superior

Node.js (versión LTS)

XAMPP / MySQL Server

Android Studio (para la app móvil)

Configuración e Instalación
1. Clonar el repositorio
Bash
git clone [https://github.com/tu-usuario/dilanmotos.git](https://github.com/tu-usuario/dilanmotos.git)
cd dilanmotos
2. Configurar el Backend
Crea un archivo .env en la raíz del proyecto backend (al mismo nivel de pom.xml) utilizando de plantilla las siguientes variables:

Fragmento de código
# BASE DE DATOS
DB_HOST=localhost
DB_PORT=3308
DB_NAME=dilanmotos
DB_USER=root
DB_PASSWORD=

# SEGURIDAD Y SERVICIOS
JWT_SECRET=TuClaveSecretaJWT
GOOGLE_API_KEY=TuApiKeyGoogle
GROQ_API_KEY=TuApiKeyGroq

# SMTP MAIL
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-correo@gmail.com
MAIL_PASSWORD=tu-password-app
Ejecuta el servidor de desarrollo:

Bash
./mvnw spring-boot:run
3. Configurar el Frontend (Web)
Bash
cd frontend
npm install
npm run dev
Documentación de la API
Con el backend en ejecución, puedes acceder a la documentación interactiva de Swagger en:
http://localhost:8080/swagger-ui.html

Licencia
Este proyecto está desarrollado con fines académicos dentro del centro de formación CEET / SENA.
