# Интеграция фронтенда с бэкендом

## Выполненные изменения

1. **Отключение моков:**
   - В файле `pcmaker_front/src/api/apiClient.ts` изменен флаг `USE_MOCK_API` на `false`
   - Добавлены комментарии в файлы `mockApi.ts` и `mockData.ts` о том, что они больше не используются

2. **Настройка CORS на бэкенде:**
   - Обновлен `WebConfig.java` для разрешения запросов с домена фронтенда `http://localhost:5173`

3. **Настройка прокси для разработки:**
   - Добавлен прокси в `vite.config.ts` для перенаправления запросов к `/api` на бэкенд
   - Обновлен baseURL в `apiClient.ts` для использования относительного пути

4. **Обновление API-эндпоинтов:**
   - Обновлены API-эндпоинты в хранилищах состояния для соответствия бэкенду:
     - `/components` → `/products/pc-components`
     - `/components?type=XXX` → `/products/component-type/XXX`
     - `/components/{id}` → `/products/{id}`
     - `/auth/register` → `/auth/signup`
     - `/profile/me` → `/users/profile`
     - `/profile/configurations` → `/configurations`

5. **Улучшение обработки ответов API:**
   - Обновлена логика обработки ответов от API в хранилищах состояния
   - Настроена корректная обработка JWT-ответов от сервера аутентификации

6. **Добавление новых хранилищ состояния:**
   - Созданы новые хранилища для работы с дополнительными API:
     - `review.ts` - работа с отзывами о товарах
     - `order.ts` - работа с заказами
     - `category.ts` - работа с категориями товаров
     - `compatibility.ts` - работа с правилами совместимости компонентов

## Запуск проекта

1. **Запуск бэкенда:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Запуск фронтенда:**
   ```bash
   cd pcmaker_front
   npm install
   npm run dev
   ```

## Структура API

Бэкенд предоставляет следующие API эндпоинты:

- `/api/auth` - аутентификация и регистрация
- `/api/products` - управление товарами
- `/api/cart` - корзина пользователя
- `/api/orders` - заказы
- `/api/configurations` - конфигуратор ПК
- `/api/compatibility` - проверка совместимости компонентов
- `/api/categories` - категории товаров
- `/api/reviews` - отзывы о товарах
- `/api/users` - управление пользователями

## Маппинг фронтенд-компонентов на API-эндпоинты

| Компонент/Хранилище | API-эндпоинты |
|---------------------|---------------|
| `catalog.ts` | `/products/pc-components`, `/products/component-type/{type}`, `/products/{id}` |
| `auth.ts` | `/auth/login`, `/auth/signup`, `/users/profile` |
| `cart.ts` | `/cart`, `/cart/{id}` |
| `configurator.ts` | `/compatibility/check`, `/configurations` |
| `review.ts` | `/reviews/product/{id}`, `/reviews/pending`, `/reviews/reported` |
| `order.ts` | `/orders`, `/orders/{id}`, `/orders/from-cart`, `/orders/statistics` |
| `category.ts` | `/categories`, `/categories/{id}`, `/categories/pc-components` |
| `compatibility.ts` | `/compatibility/check`, `/compatibility/rules`, `/compatibility/compatible/{id}` |

## Возможные проблемы и их решение

1. **Ошибки CORS:**
   - Убедитесь, что в `WebConfig.java` добавлен правильный URL фронтенда
   - В режиме разработки используйте прокси Vite

2. **Несоответствие данных:**
   - Если API возвращает данные в формате, отличном от ожидаемого фронтендом, используйте адаптеры для преобразования данных
   - Пример преобразования в `auth.ts` для ответа JWT

3. **Проблемы с аутентификацией:**
   - Проверьте правильность передачи JWT токена в заголовке Authorization
   - В этой реализации не используется refresh token, при истечении срока действия токена пользователь перенаправляется на страницу входа

4. **Генерация API клиентов:**
   - Для автоматической генерации API клиентов можно использовать команду:
     ```bash
     npm run generate-api
     ```

## Дальнейшие шаги

1. **Создание компонентов представлений:**
   - Необходимо создать компоненты для просмотра и управления отзывами
   - Необходимо создать компоненты для просмотра и управления заказами
   - Необходимо создать админ-интерфейс для управления категориями

2. **Интеграция категорий и поиска:**
   - Добавить функциональность поиска и фильтрации по категориям

3. **Обработка ошибок:**
   - Создать единую систему обработки и отображения ошибок от API
   - Реализовать перехват 401 ошибок с перенаправлением на страницу входа 