# TaskManager

A production-quality personal task management system built with Spring Boot, Java 17, PostgreSQL, and Thymeleaf.

## 🚀 Overview

TaskManager is a modern web application designed to help individuals manage their daily tasks efficiently. It provides a clean, responsive user interface and a robust REST API for external integrations.

### Key Features

- **Task Management**: Create, view, edit, and delete tasks.
- **Status Tracking**: Tasks can be marked as PENDING, IN_PROGRESS, or COMPLETED.
- **Dashboard**: High-level overview of task statistics.
- **Filtering**: Quickly filter tasks by their current status.
- **Modern UI**: Built with Bootstrap 5 for a responsive and visually appealing experience.
- **REST API**: Full CRUD capabilities exposed via JSON endpoints.

## 🏗️ Architecture

The project follows Clean Architecture and SOLID principles, organized into layers:

- **Web Layer**: Spring MVC Controllers for Thymeleaf templates and REST Controllers for the API.
- **Service Layer**: Business logic implementation.
- **Repository Layer**: Data access using Spring Data JPA.
- **Domain Model**: Core entities and enums.
- **DTOs**: Data transfer objects for request/response handling.

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5
- **Persistence**: Spring Data JPA, PostgreSQL
- **Frontend**: Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Utility**: Lombok, Bean Validation

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **PostgreSQL 14+**
- **IDE** (IntelliJ IDEA recommended)

## 🔧 Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ecarrasco-hn/TaskManager.git
   cd TaskManager
   ```

2. **Configure Database**:
   - Ensure PostgreSQL is running.
   - Create a database named `TaskManager`.
   - Update `src/main/resources/application.yml` if your PostgreSQL credentials differ from the default (`postgres`/`postgres`).

3. **Environment Variables (Optional)**:
   You can override database settings using environment variables:
   - `DB_URL`: JDBC URL
   - `DB_USERNAME`: Username
   - `DB_PASSWORD`: Password

## 🚀 Running the Application

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Application**:
   - Web UI: [http://localhost:8080/tasks](http://localhost:8080/tasks)
   - REST API: [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks)

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks (filter with `?status=...`) |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update existing task |
| DELETE | `/api/tasks/{id}` | Delete a task |
| PATCH | `/api/tasks/{id}/status`| Update task status |
| GET | `/api/tasks/stats` | Get task statistics |

## 🧪 Testing Strategy

El proyecto utiliza un enfoque de pruebas piramidal, priorizando las pruebas unitarias y de integración para asegurar la robustez del sistema.

### Entorno de Pruebas
- **Framework Principal**: JUnit 5 (Jupiter)
- **Biblioteca de Mocks**: Mockito para el aislamiento de componentes.
- **Spring Boot Test**: Utilizado para pruebas de integración con el contexto de Spring.
- **Base de Datos de Prueba**: Se recomienda el uso de una base de datos en memoria (H2) o contenedores (Testcontainers) para pruebas de integración de repositorios.

### Estructura de Pruebas
Las pruebas se organizan siguiendo la misma estructura de paquetes que el código fuente en `src/test/java`:
- `com.taskmanager.controller`: Pruebas para controladores API y Web (utilizando MockMvc).
- `com.taskmanager.service`: Pruebas unitarias para la lógica de negocio.
- `com.taskmanager.repository`: Pruebas de integración para la capa de persistencia.

### Ejecución de Pruebas
Para ejecutar todas las pruebas del proyecto, utilice el siguiente comando:
```bash
mvn test
```

## 📄 License

This project is licensed under the MIT License.
