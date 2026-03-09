# Filmorate
## Схема базы данных Filmorate
![схема бд](/images/table-filmorate.png)
## Примеры сложных запросов:
### 1. Поиск топ-10 популярных фильмов
SELECT f.name, 
COUNT(l.user_id) AS count_likes
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY count_likes DESC
LIMIT 10;
### 2. Получение общих списков друзей для двух пользователей
SELECT u.name
FROM users u
WHERE u.id IN (
    SELECT friend_id FROM friendships WHERE user_id = 1
    INTERSECT
    SELECT friend_id FROM friendships WHERE user_id = 2
);
### 3. Получений всех жанров популярного фильма
SELECT g.name
FROM genres g
JOIN film_genres fg ON g.id = fg.genre_id
WHERE fg.film_id = 1;
