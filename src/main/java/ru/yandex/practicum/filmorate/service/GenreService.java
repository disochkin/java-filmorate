package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.JdbcGenreRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GenreService {
    private final JdbcGenreRepository genreStorage;

    @Autowired
    public GenreService(@Qualifier("GenreDbStorage") JdbcGenreRepository genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genres> findAll() {
        return genreStorage.findAll();
    }

    public Genres getGenreById(Integer id) {
        Optional<Genres> genreOptional = genreStorage.getGenreById(id);
        return genreOptional.orElseThrow(() -> new NoSuchElementException("Жанр с ID=" + id + " не найден"));
    }

}
