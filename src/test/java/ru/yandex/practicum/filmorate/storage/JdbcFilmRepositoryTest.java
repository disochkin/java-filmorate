package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcFilmRepository.class)
@DisplayName("JdbcFilmRepositoryTest")
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
        String[][] filmData = {{"1", "film1", "film description1", "2001-01-01", "10"}, {"2", "film2", "film description2", "2002-02-02", "20"}, {"3", "film3", "film description3", "2003-03-03", "30"}, {"4", "film4", "film description4", "2004-04-04", "40"}};
        for (int i = 0; i < filmData.length; i++) {
            filmMap.put(i + 1, getTestFilm(Integer.parseInt(filmData[i][0]), filmData[i][1], filmData[i][2],
                    LocalDate.parse(filmData[i][3]), Integer.parseInt(filmData[i][4]), mpa));
        }
    }

    @Test
    void findAll() {
        Collection<Film> actualFilms = jdbcFilmRepository.findAll();
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
    void addAndRemoveLikesTest() {
        Integer userId = 1;
        Integer filmId = 2;
        Like like = new Like(userId, filmId);
        jdbcFilmRepository.addLike(like);
        assertThat(jdbcFilmRepository.getLikes(filmId))
                .hasSize(1)
                .contains(userId);
        jdbcFilmRepository.removeLike(like);
        assertThat(jdbcFilmRepository.getLikes(filmId)).hasSize(0);
    }

    @Test
    void getPopularFilmTest() {
        //Проверка
        assertThat(jdbcFilmRepository.getPopularFilms(10))
                .hasSize(0);
        // 3 лайка фильму с id=1
        jdbcFilmRepository.addLike(new Like(1,1));
        jdbcFilmRepository.addLike(new Like(2,1));
        jdbcFilmRepository.addLike(new Like(3,1));
        // 2 лайка фильму с id=3
        jdbcFilmRepository.addLike(new Like(1,3));
        jdbcFilmRepository.addLike(new Like(3,3));
        // 1 лайк фильму с id=2
        jdbcFilmRepository.addLike(new Like(1,2));
        List<Film> expectedTopList = List.of(filmMap.get(1), filmMap.get(3), filmMap.get(2));

        assertThat(jdbcFilmRepository.getPopularFilms(10))
                .hasSize(3)
                .containsExactlyElementsOf(expectedTopList);

        assertThat(jdbcFilmRepository.getPopularFilms(2))
                .hasSize(2)
                .containsExactlyElementsOf(expectedTopList.subList(0,2));
    }

    @Test
    void update() {
        String[] filmDataToUpdate = {"5", "film5", "film description5", "2005-05-05", "50"};
        Film filmToUpdate = filmMap.get(4);
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
}