package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component("UserJdbcRepository")
public class JdbcUserRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<User> mapper;

//    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc) {
//        this.jdbc = jdbc;
//        this.mapper = (rs, rowNum) -> new Genres(rs.getInt("id"), rs.getString("name")); // Конструктор для удобства
//    }
    public User save(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());
        jdbc.update("INSERT INTO PUBLIC.\"USERS\" (EMAIL, NAME, LOGIN, BIRTHDAY) " +
                "VALUES (:email, :name, :login, :birthday)", params,  keyHolder, new String[]{"id"});
        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }
    public User update(User user) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());
        jdbc.update("UPDATE PUBLIC.\"USERS\"  SET EMAIL=:email, NAME=:name, " +
                "LOGIN=:login, BIRTHDAY=:birthday WHERE ID=:id", params);
        return user;
    }

    public Collection<User> findAll() {
        String query = "SELECT * FROM PUBLIC.\"USERS\" ORDER BY ID;";
        return jdbc.query(query, mapper);
    }

    public Collection<User> getUserByIds(List<Integer> ids) {
        String query = "SELECT * FROM PUBLIC.\"USERS\" WHERE ID IN (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        return jdbc.query(query, parameters, mapper);
    }

    public Optional<User> getUserById(Integer id) {
        try {
            String query = "SELECT * FROM PUBLIC.\"USERS\" WHERE ID=:id";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            return Optional.ofNullable(jdbc.queryForObject(query, parameters, mapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        String query = "INSERT INTO PUBLIC.\"FRIENDSHIP\" (USER_ID, FRIEND_ID) " +
                "VALUES (:userId, :friendId);";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("friendId", friendId);
        jdbc.update(query, parameters);
    }

    public Collection<Integer> getFriends(Integer userId) {
        String query = "SELECT FRIEND_ID FROM PUBLIC.\"FRIENDSHIP\" WHERE USER_ID=:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        return jdbc.queryForList(query, parameters, Integer.class);
    }

    public void removeFriendship(Integer userId, Integer friendId) {
        String query = "DELETE FROM PUBLIC.\"FRIENDSHIP\" f " +
                        "WHERE (f.USER_ID = :userId AND f.FRIEND_ID = :friendId);";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("friendId", friendId);
        jdbc.update(query, parameters);
    }

}
