package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.GenreRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {
    private final GenreRepository genreRepository;

    public Collection<Genres> findAll() {
        return genreRepository.findAll();
    }

    public Genres getGenreById(Integer id) {
        Optional<Genres> genreOptional = genreRepository.getGenreById(id);
        return genreOptional.orElseThrow(() -> new NoSuchElementException("Жанр с ID=" + id + " не найден"));
    }
}
