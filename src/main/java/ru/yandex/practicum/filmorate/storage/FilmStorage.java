package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

@Service
public interface FilmStorage {
    Film create(Film film);
    Collection<Film> findAll();
    Film update(Film film);
}
