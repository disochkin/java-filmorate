package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepositoryTest")
@Configuration
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository jdbcFilmRepository;
    private final HashMap<Integer, Film> filmMap = new HashMap<>();
    private final Mpa mpa = new Mpa(1, "G");

    static Film getTestFilm(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setReleaseDate(releaseDate);
        film.setMpa(mpa);
        return film;
    }

    @BeforeEach
    public void initFilms() {
        String[][] filmData = {{"1", "film1", "film description1", "2001-01-01", "10"}, {"2", "film2", "film description2", "2002-02-02", "20"}, {"3", "film3", "film description3", "2003-03-03", "30"}};
        for (int i = 0; i < filmData.length; i++) {
            filmMap.put(i + 1, getTestFilm(Integer.parseInt(filmData[i][0]), filmData[i][1], filmData[i][2],
                    LocalDate.parse(filmData[i][3]), Integer.parseInt(filmData[i][4]), mpa));
        }
    }

    @Test
    void findAll() {
        List<Film> actualFilms = jdbcFilmRepository.findAll();
        assertThat(actualFilms).containsExactlyInAnyOrderElementsOf(filmMap.values());
    }

    @Test
    void getFilmById() {
        int testFilmId = 1;
        Optional<Film> filmOptional = jdbcFilmRepository.getFilmById(testFilmId);
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(filmMap.get(testFilmId));
    }

    @Test
    void save() {
        String[] newFilmData = {"5", "film5", "film description5", "2005-05-05", "50"};
        Film newFilm = getTestFilm(Integer.parseInt(newFilmData[0]), newFilmData[1], newFilmData[2],
                LocalDate.parse(newFilmData[3]), Integer.parseInt(newFilmData[4]), mpa);
        Film savedFilm = jdbcFilmRepository.save(newFilm);
        Optional<Film> filmFromDbOpt = jdbcFilmRepository.getFilmById(savedFilm.getId());
        assertThat(filmFromDbOpt)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    void update() {
        String[] filmDataToUpdate = {"5", "film5", "film description5", "2005-05-05", "50"};
        Film filmToUpdate = filmMap.get(3);
        filmToUpdate.setName(filmDataToUpdate[1]);
        filmToUpdate.setDescription(filmDataToUpdate[2]);
        filmToUpdate.setReleaseDate(LocalDate.parse(filmDataToUpdate[3]));
        filmToUpdate.setDuration(Integer.parseInt(filmDataToUpdate[4]));
        jdbcFilmRepository.update(filmToUpdate);
        Optional<Film> updatedFilm = jdbcFilmRepository.getFilmById(filmToUpdate.getId());
        assertThat(updatedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(filmToUpdate);
    }

    @Test
    void addAndRemoveLikesTest() {
        Integer userId = 1;
        Integer filmId = 2;
        jdbcFilmRepository.addLike(filmId, userId);
        assertThat(jdbcFilmRepository.getLikes(filmId))
                .hasSize(1)
                .contains(userId);
        jdbcFilmRepository.removeLike(filmId, userId);
        assertThat(jdbcFilmRepository.getLikes(filmId)).hasSize(0);
    }
}