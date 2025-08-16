-- Тестовые пользователи
INSERT INTO USERS (EMAIL,LOGIN,NAME,BIRTHDAY) VALUES
('email_1@domain.ru','testUserLogin1','testUserName1','2001-01-01'),
('email_2@domain.ru','testUserLogin2','testUserName2','2002-02-02'),
('email_3@domain.ru','testUserLogin3','testUserName3','2003-03-03');

-- Тестовые фильмы
INSERT INTO MPA (ID, NAME) VALUES
(1,'G');

INSERT INTO FILM (NAME,DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID) VALUES
('film1', 'film description1', '2001-01-01', '10', 1),
('film2', 'film description2', '2002-02-02', '20', 1),
('film3', 'film description3', '2003-03-03', '30', 1),
('film4', 'film description4', '2004-04-04', '40', 1);