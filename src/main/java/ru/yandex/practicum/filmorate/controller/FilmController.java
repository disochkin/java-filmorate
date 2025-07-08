package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    static final Logger log =
            LoggerFactory.getLogger(FilmController.class);
    FilmService filmService;
    @Autowired
    private FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<Film> findAll() {
        //log.info("Запрос на выгрузку всех фильмов. Выгружено: {} записей", films.size());
        return filmService.findAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма: {}", film.toString());
        return filmService.create(film);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }
}
