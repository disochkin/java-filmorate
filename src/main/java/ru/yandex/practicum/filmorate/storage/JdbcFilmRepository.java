package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Component("JdbcFilmRepository")

public class JdbcFilmRepository implements FilmStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Mpa> mpaMapper;

    static List<Film> extractFilmData(ResultSet rs) throws SQLException {
        Map<Integer, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            if (!filmMap.containsKey(id)) {
                Film film = new Film();
                film.setId(id);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                film.setMpa(mpa);
                film.setGenres(new HashSet<>());
                filmMap.put(id, film);
            }
            Film currentFilm = filmMap.get(id);
            if (rs.getInt("genre_id") > 0) {
                Genres genre = new Genres();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("genre_name"));
                currentFilm.getGenres().add(genre);
            }
        }
        return new ArrayList<>(filmMap.values());
    }

    static Collection<Mpa> extractMpaData(ResultSet rs) throws SQLException {
        ArrayList<Mpa> mpaList = new ArrayList<>();
        while (rs.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            mpaList.add(mpa);
        }
        return mpaList;
    }

    public List<Film> findAll() {
        final String query = "SELECT ff.ID, ff.NAME, ff.DESCRIPTION, ff.RELEASE_DATE, ff.DURATION, m.ID as MPA_ID, " +
                "m.NAME as MPA_NAME, g.ID AS GENRE_ID , g.NAME as GENRE_NAME FROM FILM AS ff " +
                "LEFT JOIN FILM_GENRE fg " +
                "ON ff.id = fg.film_id " +
                "LEFT JOIN MPA m " +
                "ON ff.mpa_id = m.ID " +
                "LEFT JOIN GENRES g " +
                "ON fg.GENRE_ID  = g.ID;";
        return jdbc.query(query, JdbcFilmRepository::extractFilmData);
    }

    public List<Film> getPopularFilms(Integer limit) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit_count", limit);
        final String query = "SELECT ff.ID, ff.NAME, ff.DESCRIPTION, ff.RELEASE_DATE, ff.DURATION, m.ID as MPA_ID, " +
                "m.NAME as MPA_NAME, g.ID AS GENRE_ID , g.NAME as GENRE_NAME " +
                "FROM (SELECT f.ID, COUNT(DISTINCT USER_ID) AS LIKE_COUNT FROM PUBLIC.\"FILM\" f " +
                "JOIN PUBLIC.\"LIKES\" lk " +
                "ON f.ID=lk.FILM_ID " +
                "GROUP BY ID ORDER BY LIKE_COUNT DESC LIMIT :limit_count) AS ID_LIKE_COUNT " +
                "JOIN FILM AS FF ON FF.ID=ID_LIKE_COUNT.ID " +
                "LEFT JOIN PUBLIC.\"FILM_GENRE\" fg " +
                "ON ff.ID = fg.FILM_ID " +
                "LEFT JOIN PUBLIC.\"MPA\" m " +
                "ON ff.MPA_ID = m.ID " +
                "LEFT JOIN PUBLIC.\"GENRES\" g " +
                "ON fg.GENRE_ID = g.ID;";
        return jdbc.query(query, parameters, JdbcFilmRepository::extractFilmData);
    }

    public List<Film> getByIds(List<Integer> ids) {
        final String query = "SELECT ff.ID, ff.NAME, ff.DESCRIPTION, ff.RELEASE_DATE, ff.DURATION, m.ID as MPA_ID, " +
                "m.NAME as MPA_NAME, g.ID AS GENRE_ID , g.NAME as GENRE_NAME FROM PUBLIC.\"FILM\" AS ff " +
                "LEFT JOIN PUBLIC.\"FILM_GENRE\" fg " +
                "ON ff.id = fg.film_id " +
                "LEFT JOIN PUBLIC.\"MPA\" m " +
                "ON ff.mpa_id = m.ID " +
                "LEFT JOIN PUBLIC.\"GENRES\" g " +
                "ON fg.GENRE_ID  = g.ID;";
        List<Film> films = jdbc.query(query, JdbcFilmRepository::extractFilmData);
        return films;
    }

    public Collection<Mpa> findAllMpa() {
        final String query = "SELECT * FROM MPA;";
        return jdbc.query(query, JdbcFilmRepository::extractMpaData);
    }

    public Optional<Mpa> getMpaById(Integer id) {
        try {
            String query = "SELECT * FROM PUBLIC.\"MPA\" f WHERE ID=:id";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            return Optional.ofNullable(jdbc.queryForObject(query, parameters, mpaMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Film> getFilmById(Integer id) {
        String query = "SELECT ff.ID, ff.NAME, ff.DESCRIPTION, ff.RELEASE_DATE, ff.DURATION, m.ID as MPA_ID," +
                "m.NAME as MPA_NAME, g.ID AS GENRE_ID, g.NAME as GENRE_NAME FROM " +
                "(SELECT * FROM PUBLIC.\"FILM\" f WHERE id = :id) AS ff " +
                "LEFT JOIN PUBLIC.\"FILM_GENRE\" fg " +
                "ON ff.ID = fg.FILM_ID " +
                "LEFT JOIN PUBLIC.\"MPA\" m " +
                "ON ff.MPA_ID = m.ID " +
                "LEFT JOIN PUBLIC.GENRES g " +
                "ON fg.GENRE_ID = g.ID;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Film> result = jdbc.query(query, parameters, JdbcFilmRepository::extractFilmData);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.iterator().next());
        }
    }


    public Film save(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releasedate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        jdbc.update("INSERT INTO PUBLIC.\"FILM\" (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (:name, :description, :releasedate, :duration, :mpa_id)", params, keyHolder, new String[]{"id"});

        film.setId(keyHolder.getKeyAs(Integer.class));
        SqlParameterSource[] resultParams = film.getGenres()
                .stream()
                .map(genre -> {
                    MapSqlParameterSource source = new MapSqlParameterSource();
                    source.addValue("genre_id", genre.getId());
                    source.addValue("film_id", film.getId());
                    return source;
                })
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate("INSERT INTO PUBLIC.\"FILM_GENRE\" (FILM_ID, GENRE_ID) " +
                "VALUES (:film_id, :genre_id)", resultParams, keyHolder);
        return film;
    }

    public Film update(Film film) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releasedate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        jdbc.update("UPDATE PUBLIC.\"FILM\" SET NAME=:name, DESCRIPTION=:description, " +
                "RELEASE_DATE=:releasedate, DURATION=:duration, MPA_ID=:mpa_id WHERE ID=:id", params);

        SqlParameterSource paramsGenresDelete = new MapSqlParameterSource()
                .addValue("id", film.getId());
        jdbc.update("DELETE FROM PUBLIC.\"FILM_GENRE\" WHERE FILM_ID =:id", paramsGenresDelete);

        SqlParameterSource[] parameters = film.getGenres()
                .stream()
                .map(genre -> {
                    MapSqlParameterSource parametersItem = new MapSqlParameterSource();
                    parametersItem.addValue("genre_id", genre.getId());
                    parametersItem.addValue("film_id", film.getId());
                    return parametersItem;
                })
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate("INSERT INTO PUBLIC.\"FILM_GENRE\" (FILM_ID, GENRE_ID) " +
                "VALUES (:film_id, :genre_id)", parameters);
        return film;
    }

    public List<Integer> getLikes(Integer filmId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId);
        String query = "SELECT USER_ID FROM PUBLIC.\"LIKES\" WHERE FILM_ID=:filmId";
        return jdbc.queryForList(query, params, Integer.class);
    }

    public void addLike(Integer filmId, Integer userId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update("INSERT INTO PUBLIC.\"LIKES\" (FILM_ID, USER_ID) " +
                "VALUES (:filmId, :userId)", params);
    }

    public void removeLike(Integer filmId, Integer userId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update("DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID=:filmId AND GENRE_ID=:genreId; ", params);
    }

}

