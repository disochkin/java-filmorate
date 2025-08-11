package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
public interface FilmStorage {
    Film save(Film film);

    Collection<Film> findAll();

    Collection<Mpa> findAllMpa();

}
