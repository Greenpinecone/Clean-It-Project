# Clean It! Frontend

Angular 21 frontend for the Clean It! technical task.

The application supports selected business use cases for a textile cleaning company:

- receiving cleaning orders
- viewing and managing cleaning orders for a local shop

The project is part of a client-server application intended to run containerized on Azure Container Apps.

## Tech Stack

- Angular 21
- Angular Material
- TypeScript
- npm
- Angular testing utilities with Vitest for unit tests
- Cypress for end-to-end tests
- Angular development proxy for local API calls
- Nginx reverse proxy planned for production API calls

## Architecture

The frontend uses relative API paths:

```text
/api/...
```

Local development:

```text
Browser -> Angular dev server -> Angular proxy -> Backend
```

Planned production setup:

```text
Browser -> Nginx frontend container -> /api reverse proxy -> Backend container
```

This keeps frontend-backend communication same-origin from the browser's point of view and avoids unnecessary CORS configuration.

## Testing Strategy

- Vitest runs Angular unit tests through the Angular CLI.
- Angular TestBed is used for component, service, and dependency-injection based tests.
- HttpTestingController is used for API service tests without real network requests.
- Cypress is used for browser-based end-to-end tests.

## Prerequisites

Use the Node.js version defined in `.nvmrc`.

```bash
nvm use
npm ci
```

## Development

Start the Angular development server:

```bash
npm start
```

Open:

```text
http://localhost:4200
```

API requests to `/api/...` are forwarded by the Angular development proxy.

## Build

Create a production build:

```bash
npm run build
```

The build output is written to the `dist/` directory.

## Code Quality

Run linting:

```bash
npm run lint
```

## Unit Tests

Run unit tests:

```bash
npm test
```

The project uses Angular's default unit testing setup with Vitest.

## End-to-End Tests

This project uses Cypress for end-to-end tests.

Run E2E tests through the Angular CLI:

```bash
npm run e2e
```

Open the Cypress test runner:

```bash
npm run cypress:open
```

Run cypress headlessly:

```bash
npm run cypress:run
```

Cypress component testing is not configured. Component and service-level tests are handled through Angular's unit testing setup.

## Containerization and Deployment

Docker, Azure Container Apps deployment, and GitHub Actions CI/CD will be added in later steps.

The planned production setup is:

```text
Browser -> Nginx frontend container -> /api reverse proxy -> Backend container
```

## Configuration

The frontend intentionally calls the backend through relative `/api/...` paths.

Environment-specific backend routing is handled outside the Angular application:

- locally through the Angular development proxy
- in production through the Nginx reverse proxy

No backend host URL is stored in the Angular application. The frontend only depends on the relative `/api/...` path.
