package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
    @RequestMapping(path = "", method = RequestMethod.GET)
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

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма: {}", film.toString());
        try {
            film.setId(getNextId());
            log.debug("ID нового фильма: {}", film.getId());
            films.put(film.getId(), film);
            log.info("Фильм сохранен: {}", film);
            return film;
        } catch (Exception e) {
            log.debug("Ошибка валидации запроса на создание нового фильма: {}", e);
            throw e;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Film update(@RequestBody Film film) {
        try {
            checkIdFilmExist(film.getId());
            films.put(film.getId(), film);
            log.info("Фильм обновлен: {}", film);
            return film;
        } catch (Exception e) {
            log.debug("Ошибка валидации запроса на обновление фильма: {}", e.toString());
            throw e;
        }
    }
}
