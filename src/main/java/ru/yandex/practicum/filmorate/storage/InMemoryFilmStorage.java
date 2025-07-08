package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        Long filmId = getNextId();
        film.setId(filmId);
        films.put(filmId,film);
        return film;
    }

    public Film update(Film newFilm) {
        checkIdFilmExist(newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private void checkIdFilmExist(Long id) {
        if (!films.containsKey(id)) {
            throw new ValidationException(String.format("Указан id несуществующего фильма: %s", id));
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
