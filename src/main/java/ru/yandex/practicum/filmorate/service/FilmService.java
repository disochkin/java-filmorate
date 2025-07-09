package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Collection<Film> getPopularFilm(Integer count) {
        List<Film> sortedProducts = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size())).limit(count)
                .toList();
        return sortedProducts;
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм с ID=" + filmId + " не найден");
        }
        return film;
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм с ID=" + id + " не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
        return film;
    }

    public Film removeLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм с ID=" + id + " не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
        return film;
    }
}
