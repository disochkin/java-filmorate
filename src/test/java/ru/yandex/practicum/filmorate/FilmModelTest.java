package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

// Проверка валидации модели Film
public class FilmModelTest {

    private Validator validator;
    private Film film;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = new Film();
        film.setName("Film TestName");
        film.setDescription("a".repeat(199));
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(1990, 12, 30));
    }

    @Test
    public void filmNameEmptyTest() {
        film.setName("");
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void longFilmDescriptionTest() {
        film.setDescription("a".repeat(201));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testReleaseDateBeforeCinemaCreate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testDurationMustBePositive() {
        film.setDuration(0);
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        film.setDuration(-10);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}