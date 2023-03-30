# Приложение "Filmorate"
<picture>
    <img src="src/main/resources/logo.png">
</picture>
С каждым годом фильмов становится всё больше и больше. А значит, всё сложнее становится делать выбор,
чтобы посмотреть сегодня вечером. Теперь ни вам, ни вашим друзьям не придётся долго размышлять над этим вопросом. 
Наше приложение обрабатывает данные фильмов:clapper:, пользователей:office_worker: и дает все необходимые рекомендации.
Приложение имеет следующую функциональность:

* хранение списка фильмов
* выгрузка списков фильмов по рейтингу и популярности
* возможность ставить лайки
* вывод рекомендаций к просмотру

### Структура БД проекта :movie_camera:
<picture>
    <img src="src/main/resources/DB_diagram.png">
</picture>

## Примеры запросов

**Получение списка всех пользователей:**

```sql
SELECT *
FROM users;
```
| user\_id | login    | name      |  email           | birthday   |
|:--------:|:---------|:----------|:-----------------|:-----------|
|    1     | vaso666  | Василий   | vas-qzy@mail.ru  | 1998-11-15 |
|    2     | ALLeonov | Александр | alexxx77@ro.ru   | 2003-03-24 |
|    3     | nathan   | Валентин  | paalax@yandex.ru | 1978-01-23 |

**Другие запросы:**
```sql
-- Получение списка всех фильмов:  
SELECT * 
FROM films;  

-- Получение списка 10 популярных фильмов:  
SELECT fi.*
FROM films AS fi   
LEFT JOIN likes AS li ON fi.film_id = li.film_id  
GROUP BY fi.film_id  
ORDER BY COUNT(li.user_id) DESC  
limit 10;

-- Получение списка друзей пользователя:
SELECT us.user_id,
       us.name, 
       us.login,
FROM friends AS fr
LEFT JOIN users AS us ON fr.friend_id = us.user_id 
WHERE fr.user_id = ? 
ORDER BY us.user_id;
```
*Стек технологий:*
*Java 11, Spring Boot, Maven, REST API, JDBC, H2*