# Feature: Add Reply Functionality

## Описание
Добавление функциональности ответов (reply) в систему комментариев.

## Изменения
1. **Новая build configuration**: ReplyFeatureBuild
2. **Автоматическая сборка** для ветки feature/add_reply
3. **Артефакты сборки**: reply-artifacts.zip

## Требования
- Node.js 16+
- PostgreSQL 12+
- Redis 6+

## API Endpoints
- `POST /api/v1/reply` - Создать ответ
- `GET /api/v1/reply/{id}` - Получить ответ
- `PUT /api/v1/reply/{id}` - Обновить ответ
- `DELETE /api/v1/reply/{id}` - Удалить ответ

## Тестирование
Запуск тестов:
```bash
npm test
npm run test:e2e
Миграция базы данных
bash
npm run db:migrate
