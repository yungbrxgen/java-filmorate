\# Filmorate

\## Схема базы данных Filmorate

!\[схема бд](src/main/resources/images/table-filmorate.png)

\## Примеры сложных запросов:

\### 1. Поиск топ-10 популярных фильмов

SELECT f.name, 

COUNT(l.user\_id) AS count\_likes

FROM films f

LEFT JOIN likes l ON f.id = l.film\_id

GROUP BY f.id

ORDER BY count\_likes DESC

LIMIT 10;

\### 2. Получение общих списков друзей для двух пользователей

SELECT u.name

FROM users u

WHERE u.id IN (

&nbsp;   SELECT friend\_id FROM friendships WHERE user\_id = 1

&nbsp;   INTERSECT

&nbsp;   SELECT friend\_id FROM friendships WHERE user\_id = 2

);

\### 3. Получений всех жанров популярного фильма

SELECT g.name

FROM genres g

JOIN film\_genres fg ON g.id = fg.genre\_id

WHERE fg.film\_id = 1;

