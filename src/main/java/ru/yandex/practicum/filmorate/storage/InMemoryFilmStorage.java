package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    static final Logger log =
            LoggerFactory.getLogger(InMemoryFilmStorage.class);

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    public Film create(Film film) {
        Long filmId = getNextId();
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
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
