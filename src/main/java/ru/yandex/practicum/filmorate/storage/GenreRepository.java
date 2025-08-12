package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository {
    Collection<Genres> findAll();

    Collection<Genres> getGenresByIds(List<Integer> ids);

    Optional<Genres> getGenreById(Integer id);
}