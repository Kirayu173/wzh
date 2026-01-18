# Web Admin Dependencies

## Runtime (required)
- Java 8+ and the backend service running (serves the UI and APIs).
- A modern browser (Chrome, Edge, Firefox, or Safari).

## Backend APIs used
- POST /auth/login
- GET /auth/me
- GET /admin/products
- POST /admin/products
- PUT /admin/products/{id}
- DELETE /admin/products/{id}
- GET /products/{id}
- GET /admin/orders
- POST /admin/orders/{id}/ship
- GET /admin/trace
- POST /admin/trace
- PUT /admin/trace/{id}
- DELETE /admin/trace/{id}
- POST /admin/trace/{traceCode}/logistics
- GET /admin/trace/{traceCode}/qrcode

## Optional tooling
- curl or a DB client to promote a user to role=admin.
- A static file server if hosting outside the backend (requires CORS or a reverse proxy).
