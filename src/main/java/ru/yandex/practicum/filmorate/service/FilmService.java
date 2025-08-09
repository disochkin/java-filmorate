package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.storage.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.storage.JdbcUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FilmService {
    private final JdbcFilmRepository jdbcFilmRepository;
//    private final MpaDbStorage mpaDbStorage;
    private final JdbcGenreRepository jdbcGenreRepository;
    private final JdbcUserRepository jdbcUserRepository;
    // private final UserStorage userStorage;

    //public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
//    public FilmService(@Qualifier("JdbcFilmRepository") JdbcFilmRepository jdbcFilmRepository,
//                       JdbcGenreRepository jdbcGenreRepository, ) {
//        this.jdbcFilmRepository = jdbcFilmRepository;
//        this.jdbcGenreRepository = jdbcGenreRepository;

        //     this.userStorage = userStorage;

    public Film create(Film film) {
        final List<Integer> genreIds = film.getGenres().stream().map(Genres::getId).toList();
        final Collection<Genres> genres = jdbcGenreRepository.getGenresByIds(genreIds);
        if (genreIds.size() != genres.size()){
            throw new NoSuchElementException("Жанры не найдены");
        }
        final Optional<Mpa> mpaOptional = jdbcFilmRepository.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NoSuchElementException("Рейтинг MPA с ID=" + film.getMpa().getId() + " не найден");
        }
        jdbcFilmRepository.save(film);
        return film;
    }

    public Film update(Film film) {
        Film filmInStorage = getFilmById(film.getId());
        final List<Integer> genreIds = film.getGenres().stream().map(Genres::getId).toList();
        final Collection<Genres> genres = jdbcGenreRepository.getGenresByIds(genreIds);
        if (genreIds.size() != genres.size()){
            throw new NoSuchElementException("Жанры не найдены");
        }
        final Optional<Mpa> mpaOptional = jdbcFilmRepository.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NoSuchElementException("Рейтинг MPA с ID=" + film.getMpa().getId() + " не найден");
        }
        filmInStorage.setName(film.getName());
        filmInStorage.setDescription(film.getDescription());
        filmInStorage.setReleaseDate(film.getReleaseDate());
        filmInStorage.setDuration(film.getDuration());
        filmInStorage.setMpa(film.getMpa());
        filmInStorage.setGenres(film.getGenres());
        return jdbcFilmRepository.update(film);
        }

    public Collection<Film> findAll() {
        return jdbcFilmRepository.findAll();
    }

    public Collection<Mpa> findAllMpa() {
        return jdbcFilmRepository.findAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        Optional<Mpa> mpaOptional = jdbcFilmRepository.getMpaById(mpaId);
        return mpaOptional.orElseThrow(() -> new NoSuchElementException("Рейтинг с ID=" + mpaId + " не найден"));
    }

    public Collection<Film> getPopularFilm(Integer count) {
        return  jdbcFilmRepository.findAll().stream()
                .map(film -> {                            // Для каждого фильма создаем мапу id->film
                    int likesCount = jdbcFilmRepository.getLikes(film.getId()).size();   // Количество лайков
                    return new AbstractMap.SimpleEntry<>(film.getId(), new Object[]{film, likesCount});
                })
                .sorted((entry1, entry2) -> ((Integer) entry2.getValue()[1]).compareTo((Integer) entry1.getValue()[1]))
                .limit(count)
                .map(entry -> (Film) entry.getValue()[0])
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Film getFilmById(Integer id) {
        final Optional <Film> filmOptional = jdbcFilmRepository.getFilmById(id);
        return filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + id + " не найден"));
    }

    public String addLike(Integer filmId, Integer userId) {
        Optional<Film> filmOptional = jdbcFilmRepository.getFilmById(filmId);
        filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + filmId + " не найден"));
        Optional<User> userOptional = jdbcUserRepository.getUserById(userId);
        userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        jdbcFilmRepository.addLike(filmId, userId);
        return "лайк добавлен";
    }

    public String removeLike(Integer filmId, Integer userId) {
        Optional<Film> filmOptional = jdbcFilmRepository.getFilmById(filmId);
        filmOptional.orElseThrow(() -> new NoSuchElementException("Фильм с ID=" + filmId + " не найден"));
        Optional<User> userOptional = jdbcUserRepository.getUserById(userId);
        userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        jdbcFilmRepository.removeLike(filmId, userId);
        return "лайк удален";


    }}
//
//        User user = userStorage.getUserById(userId);
//        if (user == null) {
//            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
//        }
//        film.getLikes().remove(userId);
//        filmStorage.update(film);
//        return film;
//    }

