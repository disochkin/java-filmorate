package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Service
public interface FilmStorage {
    Film create(Film film);

    Collection<Film> findAll();

    Film getFilmById(Long filmId);

    Film update(Film film);
}
