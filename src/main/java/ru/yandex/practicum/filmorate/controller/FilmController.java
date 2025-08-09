package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    static final Logger log =
            LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<Film> findAll() {
        log.info("Запрос на выгрузку всех фильмов.");
        return filmService.findAll();
    }

    @RequestMapping(path = "/popular", method = RequestMethod.GET)
    public Collection<Film> getPopularFilm(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на выгрузку популярных фильмов.");
        return filmService.getPopularFilm(count);
    }


    @RequestMapping(path = "/{filmId}", method = RequestMethod.GET)
    public Film getFilmById(@PathVariable long filmId) {
        log.info(String.format("Запрос на выгрузку фильма с id=%s.", filmId));
        return filmService.getFilmById(filmId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание нового фильма.");
        return filmService.create(film);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Film update(@RequestBody Film film) {
        log.info("Запрос на обновление фильма.");
        return filmService.update(film);
    }

    @RequestMapping(value = "/{id}/like/{userId}", method = RequestMethod.PUT)
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.addLike(id, userId);
    }

    @RequestMapping(value = "/{id}/like/{userId}", method = RequestMethod.DELETE)
    public Film removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.removeLike(id, userId);
    }

}
