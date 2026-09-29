# Ticket Booking

Сервис бронирования билетов на события (концерты, кино, театр). Java 21, Spring Boot 3, PostgreSQL, Flyway, Docker.

## Запуск

Всё через Docker:

    docker compose up --build

Только БД (приложение из IDE или через Maven):

    docker compose up -d db
    mvn spring-boot:run

Проверка: http://localhost:8080/actuator/health


## Доменная модель

- **User** — пользователь (роль USER/ADMIN). Пока без пароля — аутентификация не реализована (см. "Дальнейшие шаги").
- **Venue / Seat** — площадка и её места (ряд, номер).
- **Event / EventSession** — событие и конкретный сеанс (событие + площадка + время + цена).
- **Booking / BookingSeat** — бронь на несколько мест сразу. Статусы: `PENDING → PAID / CANCELLED / EXPIRED`.

## Ключевая логика

**Защита от двойной брони места.** В таблице `booking_seats` есть частичный уникальный индекс
`(event_session_id, seat_id) WHERE active`. Если два пользователя одновременно бронируют одно
место, второй получит нарушение constraint на уровне БД — единственный надёжный способ
избежать гонки. Приложение ловит `DataIntegrityViolationException` и превращает её в HTTP 409.

**Автоматическое истечение брони.** У каждой `PENDING`-брони есть `expiresAt`
(по умолчанию +15 минут, настраивается через `BOOKING_HOLD_MINUTES`). Фоновая задача
`BookingExpirationScheduler` раз в минуту (`BOOKING_EXPIRATION_CHECK_MS`) находит просроченные
брони, переводит их в `EXPIRED` и освобождает места — они снова становятся доступны для брони.

**Оптимистичная блокировка.** У `Booking` есть `@Version`: если оплата и отмена одной и той же
брони прилетят параллельно, вторая операция получит конфликт версии вместо того, чтобы молча
затереть чужие изменения.

**Освобождение мест без удаления.** При отмене/истечении брони строки `booking_seats` не
удаляются, а помечаются `active = false`. История бронирований сохраняется, место освобождается.

## API (основное)

| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/venues` | создать площадку |
| POST | `/api/venues/{id}/seats` | добавить место |
| POST | `/api/events` | создать событие |
| POST | `/api/sessions` | создать сеанс |
| GET | `/api/events/{id}/sessions` | сеансы события |
| POST | `/api/users` | создать пользователя (временно, без пароля) |
| POST | `/api/bookings` | забронировать места |
| POST | `/api/bookings/{id}/pay?userId=` | оплатить бронь |
| POST | `/api/bookings/{id}/cancel?userId=` | отменить бронь |
| GET | `/api/bookings?userId=` | брони пользователя |

Пример сценария:

    curl -X POST localhost:8080/api/users -H "Content-Type: application/json" \
      -d '{"email":"a@test.com","fullName":"Alice"}'

    curl -X POST localhost:8080/api/venues -H "Content-Type: application/json" \
      -d '{"name":"Театр","address":"ул. Примерная, 1"}'

    curl -X POST localhost:8080/api/venues/1/seats -H "Content-Type: application/json" \
      -d '{"rowNum":1,"seatNum":1}'

    curl -X POST localhost:8080/api/events -H "Content-Type: application/json" \
      -d '{"title":"Концерт","type":"CONCERT"}'

    curl -X POST localhost:8080/api/sessions -H "Content-Type: application/json" \
      -d '{"eventId":1,"venueId":1,"startsAt":"2027-01-01T19:00:00Z","price":50.00}'

    curl -X POST localhost:8080/api/bookings -H "Content-Type: application/json" \
      -d '{"userId":1,"sessionId":1,"seatIds":[1]}'

    curl -X POST "localhost:8080/api/bookings/1/pay?userId=1"

## Дальнейшие шаги (не реализовано)

- Spring Security + JWT: полноценная регистрация/логин, `userId` из токена вместо тела запроса,
  хеширование паролей (BCrypt), проверка роли ADMIN для управления событиями/площадками.
- Swagger/OpenAPI документация.
