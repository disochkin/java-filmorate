package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    static final Logger log =
            LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    @RequestMapping(path = "",  method = RequestMethod.GET)
    public Collection<Film> findAll() {
        log.info("Запрос на выгрузку всех фильмов. Выгружено: {} записей", films.size());
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("ID нового фильма: {}", currentMaxId);
        return ++currentMaxId;
    }

    private void checkIdFilmExist(Long id) {
        if (!films.containsKey(id)) {
            throw new ValidationException(String.format("Указан id несуществующего фильма: %s", id));
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null) {
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Название не должно содержать больше 200 символов");
        }
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма: {}", film.toString());
        try {
            validateFilm(film);
            film.setId(getNextId());
            log.debug(film.toString());
            log.debug("ID нового фильма: {}", film.getId());
            films.put(film.getId(), film);
            log.info("Фильм сохранен: {}", film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Ошибка валидации запроса на создание нового фильма: {}", e);
            return new ResponseEntity<>(film, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<Film> update(@RequestBody Film film) {
        try {
            validateFilm(film);
            checkIdFilmExist(film.getId());
            films.put(film.getId(), film);
            log.info("Фильм обновлен: {}", film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Ошибка валидации запроса на обновление фильма: {}", e.toString());
            return new ResponseEntity<>(film, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }}
