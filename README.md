# To-do App (Spring Boot + React)

Full-stack to-do list app with a Spring Boot HATEOAS API, React + Bootstrap UI, and Docker deployment.

## Stack
- Backend: Java 17, Spring Boot, Maven, PostgreSQL
- Frontend: React, Vite, Bootstrap
- Deployment: Docker Compose
- Architecture: Clean architecture layering + HATEOAS responses

## Local development

### Backend
```
cd backend
mvn spring-boot:run
```

Environment variables (optional):
- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/todo`)
- `SPRING_DATASOURCE_USERNAME` (default: `todo`)
- `SPRING_DATASOURCE_PASSWORD` (default: `todo`)
- `APP_USERNAME` (default: `admin`)
- `APP_PASSWORD` (default: `admin`)
- `APP_CORS_ALLOWED_ORIGINS` (default: `http://localhost:5173`)

### Frontend
```
cd frontend
npm install
npm run dev
```

Optionally set `VITE_API_BASE_URL` to point at the backend.

## Docker
```
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080

## Auth
The API is protected with HTTP Basic auth. The UI prompts for username/password and uses those credentials for API calls.

## API examples (HATEOAS)
```
curl -u admin:admin http://localhost:8080/api/tasks
curl -u admin:admin http://localhost:8080/api/tasks/1
curl -u admin:admin -H "Content-Type: application/json" \
  -d '{"title":"First task","description":"Write docs"}' \
  http://localhost:8080/api/tasks
```

HATEOAS links are exposed in `_links` for each entity to guide updates, deletes, and toggles.
