package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genres> {
    @Override
    public Genres mapRow(ResultSet resultSet, int rowNum) throws SQLException {
//        Genres genre = new Genres();
//        genre.setId(resultSet.getInt("id"));
//        genre.setName(resultSet.getString("name"));
        return (new Genres(resultSet.getInt("id"), resultSet.getString("name")));
    }
}
