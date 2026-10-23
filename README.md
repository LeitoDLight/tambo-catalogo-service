# tambo-catalogo-service

Microservicio de **catálogo e inventario** de Tambo Market: categorías, productos y control de stock.

| Dato | Valor |
|---|---|
| Puerto | 8082 |
| Tecnología | Java 21, Spring Boot 4.1.1, Maven, PostgreSQL 17, Spring Data JPA, RabbitMQ, validación con Bean Validation |
| Responsable | José Miguel Yataco Quispe |

## API REST

| Método y ruta | Acceso |
|---|---|
| `GET /api/categorias`, `GET /api/categorias/{id}` | ADMIN, CAJERO |
| `POST /api/categorias`, `PUT /api/categorias/{id}`, `DELETE /api/categorias/{id}` | ADMIN |
| `GET /api/productos` (filtro opcional `?nombre=`), `GET /api/productos/{id}` | ADMIN, CAJERO |
| `POST /api/productos`, `PUT /api/productos/{id}`, `DELETE /api/productos/{id}` | ADMIN |

El `DELETE` de productos es una **baja lógica** (soft delete): el registro se conserva con `activo = false`.

## Mensajería (RabbitMQ)

Exchange `tambo.exchange` (tipo Direct).

| Rol | Routing key | Cola | Formato del mensaje |
|---|---|---|---|
| Consume | `venta.registrada` | `catalogo.venta.registrada` | `ventaId\|productoId:cantidad,...` |
| Publica | `venta.procesada` | `ventas.venta.procesada` | `ventaId` |
| Publica | `stock.bajo` | `alertas.stock.bajo` | `productoId\|stockActual\|nombre` |

Al recibir una venta descuenta el stock en una transacción. Publica `venta.procesada` siempre y `stock.bajo` por cada producto que quedó con stock menor o igual al mínimo. Los mensajes inválidos se rechazan sin reintentar.

## Seguridad

Valida el token JWT emitido por `auth-service` (mismo secreto compartido) y aplica los roles ADMIN y CAJERO.

## Pruebas

`ProductoRepositoryTest`: prueba del repositorio de productos.

## Proyecto completo

Este repositorio es **un microservicio de Tambo Market**. El proyecto completo (todos los servicios, `docker-compose.yml`, colección de Postman, informe y el historial de trabajo del equipo) está en el repositorio de integración, que es privado y tiene acceso concedido al docente:

https://github.com/LeitoDLight/tambo-market

Para levantar todo el sistema:

```
git clone https://github.com/LeitoDLight/tambo-market.git
cd tambo-market
docker compose up -d --build
```

Luego abrir http://localhost (usuario `admin` / `Admin123`).

## Equipo

Leonardo Fabricio Dorregaray Guevara · José Miguel Yataco Quispe · Alberto Muzaurieta Hidalgo · Joselin Carhuapoma Poma

Curso: Desarrollo de Aplicaciones Web II — Cibertec.