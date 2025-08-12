# java-filmorate
Схема БД

![db-schema.png](db-schema.png)

Примеры запросов
Запрос фильма с жанрами и рейтингом:
SELECT ff.ID, ff.NAME, ff.DESCRIPTION, ff.RELEASE_DATE, ff.DURATION, m.ID as MPA_ID,m.NAME as MPA_NAME, g.ID AS GENRE_ID, g.NAME as GENRE_NAME FROM
(SELECT * FROM PUBLIC.FILM f WHERE id = :id) AS ff 
LEFT JOIN PUBLIC.FILM_GENRE fg  
ON ff.ID = fg.FILM_ID 
LEFT JOIN PUBLIC.MPA m 
ON ff.MPA_ID = m.ID 
LEFT JOIN PUBLIC.GENRES g 
ON fg.GENRE_ID = g.ID;

Обновление данных фильма в БД состоит из 3х этапов:
1) обновление записи фильма в БД
UPDATE PUBLIC.\"FILM\" SET NAME=:name, DESCRIPTION=:description RELEASE_DATE=:releasedate, 
DURATION=:duration, MPA_ID=:mpa_id WHERE ID=:id

2) Удаление существующих связей между фильмом и жанрами:
DELETE FROM PUBLIC."FILM_GENRE" WHERE FILM_ID =:id

3) Создание новых связей между фильмом и жанрами:
INSERT INTO PUBLIC.\"FILM_GENRE\" (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id)

Создание записи фильма в БД:
INSERT INTO PUBLIC.FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) 
    VALUES (:name, :description, :releasedate, :duration, :mpa_id);

Обновление записи фильма в БД:
UPDATE PUBLIC.FILM SET NAME=:name, DESCRIPTION=:description,
    RELEASE_DATE=:releasedate, DURATION=:duration, MPA_ID=:mpa_id WHERE ID=:id;

Получить лайки фильма:
SELECT USER_ID FROM PUBLIC.LIKES WHERE FILM_ID=:filmId;

Добавление лайка фильму:
INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) VALUES (:filmId, :userId);

Удаление лайка:
DELETE FROM PUBLIC.LIKES WHERE FILM_ID=:filmId AND USER_ID=:userId;

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Создание нового пользователя:
INSERT INTO PUBLIC.USERS (EMAIL, NAME, LOGIN, BIRTHDAY) VALUES (:email, :name, :login, :birthday);

Обновление пользователя:
UPDATE PUBLIC.USERS  SET EMAIL=:email, NAME=:name LOGIN=:login, BIRTHDAY=:birthday WHERE ID=:id;

Выборка всех пользователей:
SELECT USERS.ID,USERS.EMAIL, USERS.LOGIN, USERS.NAME, USERS.BIRTHDAY, f.FRIEND_ID 
FROM PUBLIC.USERS
LEFT JOIN PUBLIC.FRIENDSHIP
ON USERS.ID = f.user_id;

Выборка пользователя по ID:
SELECT user_selected.Id,user_selected.email, user_selected.login, user_selected.name, user_selected.Birthday, f.FRIEND_ID 
FROM (SELECT * FROM  PUBLIC.USERS u WHERE id IN (:ids)) AS user_selected 
LEFT JOIN PUBLIC.FRIENDSHIP f 
ON user_selected.ID = f.user_id;

Выборка пользователя списку ID:
SELECT user_selected.Id,user_selected.email, user_selected.login, user_selected.name, user_selected.Birthday, f.FRIEND_ID
FROM (SELECT * FROM  PUBLIC.USERS u WHERE id=:id) AS user_selected 
LEFT JOIN PUBLIC.FRIENDSHIP f 
ON user_selected.ID = f.user_id;

Добавление в друзья другого пользователя:
INSERT INTO PUBLIC.FRIENDSHIP (USER_ID, FRIEND_ID)
VALUES (:userId, :friendId);

Выборка списка id друзей пользователя:
SELECT FRIEND_ID FROM PUBLIC.FRIENDSHIP WHERE USER_ID=:userId
