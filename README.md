# 📝 NotesApp

Una aplicación completa de gestión de notas desarrollada con Spring Boot y React.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4.10-purple.svg)](https://vitejs.dev/)

## 📋 Descripción

NotesApp es una aplicación moderna de gestión de notas que permite a los usuarios crear, editar, archivar y organizar sus notas con etiquetas. La aplicación cuenta con autenticación JWT, diseño responsive y una interfaz intuitiva.

### ✨ Características Principales

- 🔐 **Autenticación segura** con JWT (JSON Web Tokens)
- 📝 **CRUD completo** de notas (Crear, Leer, Actualizar, Eliminar)
- 🏷️ **Sistema de etiquetas** para organizar notas
- 📦 **Archivar/Desarchivar** notas
- 🎨 **Interfaz moderna** con React y diseño responsive
- 💾 **Base de datos H2** en memoria (desarrollo)
- 🔄 **API RESTful** bien documentada
- ✅ **Tests de integración** incluidos

## 🏗️ Arquitectura

### Backend
- **Framework:** Spring Boot 3.2.0
- **Lenguaje:** Java 17
- **Base de datos:** H2 (desarrollo), configurable para PostgreSQL/MySQL
- **Seguridad:** Spring Security + JWT
- **Build Tool:** Maven
- **ORM:** Spring Data JPA

### Frontend
- **Framework:** React 18.3.1
- **Build Tool:** Vite 5.4.10
- **Routing:** React Router DOM 7.0.1
- **HTTP Client:** Axios 1.7.8
- **Estilos:** CSS personalizado

## 🚀 Instalación y Configuración

### Prerrequisitos

- Java 17 o superior
- Node.js 18+ y npm
- Maven 3.6+ (o usar el wrapper incluido)
- Git

### Clonar el Repositorio

```bash
git clone https://github.com/MartinCarrizo09/notesApp.git
cd notesApp
```

### Opción 1: Inicio Rápido (Recomendado)

Usa el script de inicio automático:

```bash
# En Linux/Mac
chmod +x start.sh
./start.sh

# En Windows PowerShell
bash start.sh
```

El script iniciará automáticamente el backend en el puerto 8080 y el frontend en el puerto 5173.

### Opción 2: Inicio Manual

#### Backend

```bash
cd backend

# Usando Maven Wrapper (recomendado)
./mvnw clean install
./mvnw spring-boot:run

# O usando Maven instalado
mvn clean install
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

#### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev
```

El frontend estará disponible en: `http://localhost:5173`

## 🔑 Usuarios de Prueba

La aplicación incluye usuarios precargados para testing:

```
Usuario: admin
Contraseña: admin123

Usuario: user
Contraseña: user123
```

## 📡 API Endpoints

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/register` | Registrar nuevo usuario |

### Notas

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| GET | `/api/notes` | Obtener todas las notas activas | ✅ |
| GET | `/api/notes/archived` | Obtener notas archivadas | ✅ |
| GET | `/api/notes/{id}` | Obtener nota por ID | ✅ |
| POST | `/api/notes` | Crear nueva nota | ✅ |
| PUT | `/api/notes/{id}` | Actualizar nota | ✅ |
| DELETE | `/api/notes/{id}` | Eliminar nota | ✅ |
| PATCH | `/api/notes/{id}/archive` | Archivar nota | ✅ |
| PATCH | `/api/notes/{id}/unarchive` | Desarchivar nota | ✅ |

### Etiquetas

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| GET | `/api/tags` | Obtener todas las etiquetas | ✅ |
| GET | `/api/tags/{id}` | Obtener etiqueta por ID | ✅ |
| POST | `/api/tags` | Crear nueva etiqueta | ✅ |
| PUT | `/api/tags/{id}` | Actualizar etiqueta | ✅ |
| DELETE | `/api/tags/{id}` | Eliminar etiqueta | ✅ |

## 🧪 Tests

### Ejecutar Tests del Backend

```bash
cd backend
./mvnw test
```

Los tests incluyen:
- Tests unitarios
- Tests de integración de controladores
- Tests de seguridad y autenticación

## 📁 Estructura del Proyecto

```
notesApp/
├── backend/                    # Aplicación Spring Boot
│   ├── src/main/java/
│   │   └── com/ensolvers/notes/
│   │       ├── bootstrap/     # Carga inicial de datos
│   │       ├── config/        # Configuración (CORS, Security, JWT)
│   │       ├── controller/    # Controladores REST
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── exception/     # Manejo de excepciones
│   │       ├── model/         # Entidades JPA
│   │       ├── repository/    # Repositorios Spring Data
│   │       └── service/       # Lógica de negocio
│   └── src/test/             # Tests
├── frontend/                  # Aplicación React
│   ├── public/               # Archivos estáticos
│   └── src/
│       ├── components/       # Componentes reutilizables
│       ├── pages/           # Páginas de la aplicación
│       └── services/        # Servicios API
└── docs/                     # Documentación adicional
    ├── architecture.md       # Arquitectura del sistema
    ├── auth-flow.md         # Flujo de autenticación
    ├── data-model.md        # Modelo de datos
    └── logging.md           # Sistema de logging
```

## 🔧 Configuración

### Backend - application.properties

```properties
# Base de datos H2
spring.datasource.url=jdbc:h2:mem:notesdb
spring.datasource.driverClassName=org.h2.Driver

# JWT
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# Puerto del servidor
server.port=8080
```

### Frontend - Configuración de API

Edita `frontend/src/services/api.js` para cambiar la URL del backend:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## 🌟 Características Técnicas

### Seguridad
- Autenticación basada en JWT
- Contraseñas hasheadas con BCrypt
- Protección CORS configurada
- Filtros de seguridad personalizados

### Base de Datos
- Relaciones Many-to-Many entre Notas y Etiquetas
- Relaciones One-to-Many entre Usuario y Notas
- Carga automática de datos de prueba

### Frontend
- Gestión de estado con React Hooks
- Rutas protegidas con autenticación
- Interceptores Axios para tokens JWT
- Diseño responsive y moderno

## 📚 Documentación Adicional

Consulta la carpeta `/docs` para documentación detallada sobre:
- Arquitectura del sistema
- Flujo de autenticación
- Modelo de datos
- Sistema de logging

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto fue desarrollado como parte de un desafío técnico.

## 👤 Autor

**Martin Carrizo**

- GitHub: [@MartinCarrizo09](https://github.com/MartinCarrizo09)
- Repository: [notesApp](https://github.com/MartinCarrizo09/notesApp)

## 🐛 Reporte de Bugs

Si encuentras algún bug, por favor abre un issue en el repositorio con:
- Descripción del problema
- Pasos para reproducirlo
- Comportamiento esperado
- Screenshots (si aplica)

## 📞 Soporte

Para preguntas o soporte, abre un issue en el repositorio de GitHub.

---

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub!
