# Приложение "Filmorate"
Данное приложение обрабатывает данные фильмов:clapper: и пользователей :office_worker:,
ставящих оценки этих фильмов. Имеет следующую функциональность:
* хранение списка фильмов
* выгрузка списков фильмов по рейтингу и популярности
* возможность ставить лайки
* вывод рекомендаций к просмотру
* 
### ## Структура БД проекта :movie_camera:
<picture>
    <img src="src/main/resources/DB_diagram.png">
</picture>
**Примеры запросов:**

```
-- Получение списка всех фильмов:  
SELECT * 
FROM films;  

-- Получение спика всех пользователей: 
SELECT * 
FROM users;

-- Получение списка 10 популярных фильмов:  
SELECT fi.*
FROM films AS fi   
LEFT JOIN likes AS li ON fi.film_id = li.film_id  
GROUP BY fi.film_id  
ORDER BY COUNT(li.user_id) desc  
limit 10;

-- Получение списка друзей пользователя:
SELECT us.user_id,
       us.name, 
       us.login,
FROM friends AS fr
LEFT JOIN users AS us ON fr.friend_id = us.user_id 
WHERE fr.user_id = (искомый пользователь)  
ORDER BY us.user_id;
```
Стек технологий:
Java 11, PostgreSQL, Spring Boot, Maven, REST API