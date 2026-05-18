# Clean It Frontend

Angular frontend for the Clean It! technical task.

This application implements the UI for two focused use cases in a textile cleaning scenario:

- receiving a cleaning order at the shop reception
- reviewing and updating shop orders as a local manager

The frontend is intentionally scoped as a prototype. It uses a simulated user model instead of real authentication and focuses on the requested workflows rather than a full business application.

## Tech Stack

- Angular 21
- TypeScript
- RxJS
- Angular Router
- Angular Reactive Forms
- Bootstrap 5
- ng-bootstrap
- ngx-toastr
- Angular ESLint
- Cypress
- Vitest-style test authoring through the Angular test builder

## Frontend Architecture

This frontend is built as a standalone Angular application.

Key characteristics:

- standalone components
- Angular signals for UI state
- OnPush change detection
- route-level resolvers for initial data loading
- relative API calls through `/api/v1`
- centralized global API error handling
- toast-based user feedback for success and failure states

The application shell consists of:

- a header with main navigation
- a manager dropdown that appears only for the manager role
- a role switcher for the demo user
- a footer
- a main router outlet

## Implemented Scope

### Use Case 1: Order Reception

Route:

```txt
/orders/create
```

The order reception screen supports:

- loading the assigned shop before the page opens
- loading the cleaning service catalog before the page opens
- receiving textiles for a new or existing customer
- searching existing customers while typing
- switching explicitly to new customer mode
- adding multiple textile rows
- selecting quantity, material, and cleaning services per row
- calculating live item totals and the overall order total
- calculating the expected pickup date automatically
- submitting the order to the backend
- showing toast feedback after successful submission
- redirecting managers to the manager overview after a successful order submission

Customer search behavior:

- debounced by 300 ms
- only starts when a search value has at least 2 characters
- can search by:
  - display name
  - email
  - phone number

Form behavior:

- uses reactive forms
- validates required fields and value limits on the client
- maps backend `422` validation errors back onto the form controls

### Use Case 2: Manager Order Overview

Route:

```txt
/manager/orders
```

The manager overview supports:

- route protection through a manager-only child guard
- loading the assigned shop before the page opens
- debounced filtering by search query and status
- pagination
- loading and empty states
- changing the status of an order from the overview
- showing toast feedback after status changes
- mapping backend validation errors back onto the filter form

Filtering behavior:

- debounced by 300 ms
- supports:
  - free-text search
  - status filtering

Status update behavior:

- only allowed target statuses are shown
- updates are sent through the backend API
- the updated order is patched back into the visible list

## Route Overview

| Route                | Purpose                                  |
| -------------------- | ---------------------------------------- |
| `/`                  | application shell placeholder            |
| `/customers`         | placeholder page for customer management |
| `/orders/create`     | order reception workflow                 |
| `/manager/dashboard` | placeholder page for manager dashboard   |
| `/manager/orders`    | manager order overview                   |
| `**`                 | not found page                           |

## Demo User Model

The application uses a simulated user instead of real authentication.

Available roles:

- `EMPLOYEE`
- `MANAGER`

Current behavior:

- the default user is Demo Employee
- the current role can be changed from the header
- manager routes are protected by a route guard
- non-managers attempting to access manager routes are redirected to `/orders/create`
- switching from manager back to employee while on a manager route redirects the user to `/orders/create`

## API Integration

The frontend uses a relative API base URL:

```txt
/api/v1
```

This value is provided centrally through an Angular injection token.

Current API clients:

- `ShopApi`
- `CleaningServiceApi`
- `CustomerApi`
- `OrderApi`

Used backend endpoints:

| Method  | Path                              | Purpose                       |
| ------- | --------------------------------- | ----------------------------- |
| `GET`   | `/api/v1/shops/assigned`          | load assigned demo shop       |
| `GET`   | `/api/v1/cleaning-services`       | load cleaning service catalog |
| `GET`   | `/api/v1/customers`               | search existing customers     |
| `POST`  | `/api/v1/orders`                  | create a new order            |
| `GET`   | `/api/v1/orders`                  | search shop orders            |
| `PATCH` | `/api/v1/orders/{orderId}/status` | change order status           |

## Local Development Proxy

In local development, API requests are forwarded through the Angular dev server proxy.

Proxy configuration:

```txt
src/proxy.conf.json
```

Configured target:

```txt
http://localhost:8080
```

Flow in local development:

```txt
Browser -> Angular dev server -> /api proxy -> Backend
```

## Error Handling

The frontend distinguishes between validation errors and general API failures.

Current behavior:

- non-validation API errors are handled globally through an HTTP interceptor
- backend validation errors with status `422` are not shown as generic toasts
- backend validation errors are mapped back onto the relevant form controls
- unexpected application errors are handled by a global Angular `ErrorHandler`
- navigation errors redirect back to `/`

User feedback:

- success, info, warning, and error messages are shown through ngx-toastr
- generic API error messages are formatted centrally
- a number of user-facing error messages are currently phrased in German

## Localization and UI Conventions

Application-wide settings:

- locale: `de-AT`
- default currency: `EUR`

Styling stack:

- Bootstrap 5
- ng-bootstrap components
- custom theme imported from `src/assets/themes/clean-it-theme.min.css`
- Toastr stylesheet imported globally

## Project Structure

The frontend is organized by feature and shared core concerns.

### `src/app/core`

Cross-cutting concerns such as:

- demo auth model
- route guards
- API error parsing and formatting
- global error handling
- HTTP interceptors
- toast notification wrapper
- shared server-side validation helpers
- runtime config tokens

### `src/app/features/order-management`

Business-specific functionality for the implemented use cases.

Key areas:

- data-access APIs
- resolvers
- request and response DTOs
- UI models and enums
- order reception feature
- manager order overview feature

### `src/app/layout`

Application shell components such as:

- header
- footer
- navigation configuration

### `src/app/shared`

Reusable UI and shared models such as:

- page placeholder components
- not-found page
- generic page response model

## Prerequisites

Required runtime versions:

- Node.js 24
- npm 11

The repository includes an `.nvmrc` file:

```txt
frontend/.nvmrc
```

Current value:

```txt
24
```

The package manager version declared in `package.json` is:

```txt
npm@11.6.2
```

## Local Development

Run the frontend from the `frontend` directory.

Install dependencies:

```bash
nvm use
npm ci
```

Start the Angular dev server:

```bash
npm start
```

Default local URL:

```txt
http://localhost:4200
```

For local API integration, the backend should also be running on:

```txt
http://localhost:8080
```

## Useful Commands

Install dependencies:

```bash
npm ci
```

Start development server:

```bash
npm start
```

Create a production build:

```bash
npm run build
```

Run linting:

```bash
npm run lint
```

Run unit tests once:

```bash
npm test -- --watch=false
```

Run the Angular e2e target:

```bash
npm run e2e
```

Open Cypress directly from the frontend workspace:

```bash
npm run cypress:open
```

Run Cypress headlessly from the frontend workspace:

```bash
npm run cypress:run
```

## Build Output

The Angular production build is used by the container runtime.

The container build copies the browser output from:

```txt
dist/frontend/browser
```

## Testing

Current test files inside the frontend workspace:

- `src/app/features/order-management/data-access/api/shop-api.spec.ts`
- `src/app/features/order-management/order-reception/customer-contact-required.validator.spec.ts`

Covered areas currently include:

- assigned shop API request wiring
- customer contact validator behavior

The Angular workspace also includes Cypress builders in `angular.json`.

## Additional Cypress Workspace

The repository also contains a separate Cypress workspace outside the frontend folder:

```txt
../e2e
```

Relevant files include:

- `../e2e/package.json`
- `../e2e/cypress.config.ts`
- `../e2e/cypress/e2e/spec.cy.ts`

Its default base URL is:

```txt
http://localhost:4200
```

Open the separate Cypress workspace:

```bash
cd ../e2e
npm ci
npm run cy:open
```

Run it headlessly:

```bash
cd ../e2e
npm run cy:run
```

## Containerization

The frontend includes a multi-stage Dockerfile.

Build stages:

- `node:24-alpine` for the Angular build
- `nginx:stable-alpine` for the final runtime image

The final container exposes:

```txt
80
```

Build the frontend image from the `frontend` directory:

```bash
docker build -t clean-it-frontend:local .
```

## Nginx Runtime

The runtime container uses:

```txt
nginx/default.conf
```

Current behavior:

- serves the compiled Angular application
- proxies `/api/v1/` requests to the backend service
- falls back to `index.html` for Angular client-side routing

Container runtime flow:

```txt
Browser -> Nginx frontend container -> /api/v1 proxy -> Backend
```

## Docker Compose Integration

The repository root contains a compose file for the full stack.

Start the full stack from the repository root:

```bash
docker compose up --build
```

Current port mapping:

- frontend: `http://localhost:4200`
- backend: `http://localhost:8081`

## CI

GitHub Actions workflow:

```txt
../.github/workflows/ci.yml
```

Frontend-related CI steps currently include:

- install dependencies
- lint the frontend
- run frontend tests
- build the Angular application
- build the frontend Docker image

The CI workflow uses Node 24 for frontend jobs.

## Limitations

- real authentication and authorization are not implemented
- user roles are simulated in the browser
- `/customers` and `/manager/dashboard` are placeholders
- the frontend depends on backend in-memory demo data
- current automated test coverage is still light
- the separate Cypress workspace currently contains scaffold-level test content
- environment-specific backend routing is handled outside the Angular application through proxying rather than in-app environment files

## Future Extensions

Natural next steps for this frontend would be:

- add real authentication and user context
- implement the placeholder customer and manager dashboard pages
- expand unit and integration test coverage
- add stronger end-to-end coverage for both implemented workflows
- extend manager overview behavior after status changes to refresh filtered result sets
- add environment-specific deployment documentation for Azure Container Apps
