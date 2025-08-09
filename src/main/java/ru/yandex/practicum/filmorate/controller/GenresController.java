package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Validated
public class GenresController {
    static final Logger log =
            LoggerFactory.getLogger(GenresController.class);
    private final GenreService genreService;

    @Autowired
    public GenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<Genres> findAll() {
        log.info("Запрос на выгрузку всех жанров.");
        return genreService.findAll();
    }

    @RequestMapping(path = "/{genreId}", method = RequestMethod.GET)
    public Genres getGenreById(@PathVariable Integer genreId) {
        log.info("Запрос жанра id={}", genreId);
        return genreService.getGenreById(genreId);
    }

}
