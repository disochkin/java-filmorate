package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository {
    Film save(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Optional<Film> getFilmById(Integer id);

    Optional<Mpa> getMpaById(Integer id);

    Collection<Mpa> findAllMpa();

    List<Film> getPopularFilms(Integer limit);

    Collection<Integer> getLikes(Integer filmId);

    void addLike(Like like);

    void removeLike(Like like);
}
