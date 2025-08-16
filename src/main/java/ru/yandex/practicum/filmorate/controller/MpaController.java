package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Validated
@AllArgsConstructor
public class MpaController {
    static final Logger log =
            LoggerFactory.getLogger(MpaController.class);
    private final FilmService filmService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<Mpa> findAll() {
        log.info("Запрос на выгрузку всех рейтингов.");
        return filmService.findAllMpa();
    }

    @RequestMapping(path = "/{mpaId}", method = RequestMethod.GET)
    public Mpa getMpaById(@PathVariable Integer mpaId) {
        log.info("Запрос рейтинга id={}", mpaId);
        return filmService.getMpaById(mpaId);
    }
}
