package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    @RequestMapping("/")
    public Collection<Film> findAll() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validatefilm(Film film) {
        if (film.getId() == null) {

            throw new ValidationException("Id не должно быть пустым");
        }
        if (film.getName() == null) {
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getName().length() > 200) {
            throw new ValidationException("Название не должно содержать больше 200 символов");
        }
        if (!film.getReleaseDate().isAfter(Instant.from(LocalDate.of(1895, 12, 28)))) {
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
        if (!film.getDuration().isPositive()) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }


    @PostMapping
    @RequestMapping("/create")
    public Film create(@Valid @RequestBody Film film) {
        validatefilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PostMapping
    @RequestMapping("/update")
    public Film update(@RequestBody Film film) {
        validatefilm(film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Указан id несуществующего фильма");
        }
        films.put(film.getId(), film);
        return film;
    }}