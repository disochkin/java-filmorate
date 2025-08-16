package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmRepository;
import ru.yandex.practicum.filmorate.storage.GenreRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    public Film create(Film film) {
        final List<Integer> genreIds = film.getGenres().stream().map(Genres::getId).toList();
        final Collection<Genres> genres = genreRepository.getGenresByIds(genreIds);
        if (genreIds.size() != genres.size()) {
            throw new NoSuchElementException("Жанры не найдены");
        }
        final Optional<Mpa> mpaOptional = filmRepository.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NoSuchElementException("Рейтинг MPA с ID=" + film.getMpa().getId() + " не найден");
        }
        filmRepository.save(film);
        return film;
    }

    public Film update(Film film) {
        Film filmInStorage = getFilmById(film.getId());
        final List<Integer> genreIds = film.getGenres().stream().map(Genres::getId).toList();
        final Collection<Genres> genres = genreRepository.getGenresByIds(genreIds);

        if (genreIds.size() != genres.size()) {
            throw new NoSuchElementException("Жанры не найдены");
        }

        final Optional<Mpa> mpaOptional = filmRepository.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NoSuchElementException("Рейтинг MPA с ID=" + film.getMpa().getId() + " не найден");
        }

        filmInStorage.setName(film.getName());
        filmInStorage.setDescription(film.getDescription());
        filmInStorage.setReleaseDate(film.getReleaseDate());
        filmInStorage.setDuration(film.getDuration());
        filmInStorage.setMpa(film.getMpa());
        filmInStorage.setGenres(film.getGenres());

        filmRepository.update(film);
        return getFilmById(film.getId());
    }

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public Collection<Mpa> findAllMpa() {
        return filmRepository.findAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        Optional<Mpa> mpaOptional = filmRepository.getMpaById(mpaId);
        return mpaOptional.orElseThrow(() -> new NoSuchElementException("Рейтинг с ID=" + mpaId + " не найден"));
    }

    public Collection<Film> getPopularFilm(Integer count) {
        return filmRepository.getPopularFilms(count);
    }

    public Film getFilmById(Integer id) {
        final Optional<Film> filmOptional = filmRepository.getFilmById(id);
        return filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + id + " не найден"));
    }

    public Like addLike(Integer filmId, Integer userId) {
        Optional<Film> filmOptional = filmRepository.getFilmById(filmId);
        filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + filmId + " не найден"));
        Optional<User> userOptional = userRepository.getUserById(userId);
        userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        Collection<Integer> existingUserIdLike = filmRepository.getLikes(filmId);
        if (existingUserIdLike.contains(userId)) {
            throw new ValidationException(String.format("Лайк пользователя id=%s фильму id=%s уже добавлен",
                    userId, filmId));
        }
        Like like = new Like(userId,filmId);
        filmRepository.addLike(like);
        return like;
    }

    public Like removeLike(Integer filmId, Integer userId) {
        Optional<Film> filmOptional = filmRepository.getFilmById(filmId);
        filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + filmId + " не найден"));
        Optional<User> userOptional = userRepository.getUserById(userId);
        userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        Collection<Integer> existingUserIdLike = filmRepository.getLikes(filmId);
        if (!existingUserIdLike.contains(userId)) {
            throw new ValidationException(String.format("Пользователь id=%s фильму id=%s лайк не ставил",
                    userId, filmId));
        }
        Like like = new Like(userId,filmId);
        filmRepository.removeLike(like);
        return like;
    }
}

