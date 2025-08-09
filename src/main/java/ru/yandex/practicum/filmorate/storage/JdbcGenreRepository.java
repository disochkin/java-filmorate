package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component("GenreDbStorage")
public class JdbcGenreRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Genres> mapper;

//    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc) {
//        this.jdbc = jdbc;
//        this.mapper = (rs, rowNum) -> new Genres(rs.getInt("id"), rs.getString("name")); // Конструктор для удобства
//    }

    public Collection<Genres> findAll() {
        String query = "SELECT * FROM PUBLIC.\"GENRES\" ORDER BY ID;";
        return jdbc.query(query, mapper);
    }

    public Collection<Genres> getGenresByIds(List<Integer> ids) {
        String query = "SELECT * FROM PUBLIC.\"GENRES\" WHERE ID IN (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        return jdbc.query(query, parameters, mapper);
    }

    public Optional<Genres> getGenreById(Integer id) {
        try {
            String query = "SELECT * FROM PUBLIC.\"GENRES\" WHERE ID=:id";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            return Optional.ofNullable(jdbc.queryForObject(query, parameters, mapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
