package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component("UserJdbcRepository")
public class JdbcUserRepository {
    private final NamedParameterJdbcOperations jdbc;

    static List<User> extractUserData(ResultSet rs) throws SQLException {
        Map<Integer, User> userMap = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            if (!userMap.containsKey(id)) {
                User user = new User();
                user.setId(id);
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setBirthday(rs.getDate("birthday").toLocalDate());
                user.setFriends(new HashSet<>());
                userMap.put(id, user);
            }
            User currentUser = userMap.get(id);
            if (rs.getInt("friend_id") > 0) {
                Integer friendId = rs.getInt("friend_id");
                currentUser.getFriends().add(friendId);
            }
        }
        return new ArrayList<>(userMap.values());
    }

    public User save(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());
        jdbc.update("INSERT INTO PUBLIC.\"USERS\" (EMAIL, NAME, LOGIN, BIRTHDAY) " +
                "VALUES (:email, :name, :login, :birthday)", params, keyHolder, new String[]{"id"});
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
        String query = "SELECT USERS.ID,USERS.EMAIL, USERS.LOGIN, USERS.NAME, USERS.BIRTHDAY, f.FRIEND_ID " +
                "FROM PUBLIC.\"USERS\" " +
                "LEFT JOIN PUBLIC.FRIENDSHIP f " +
                "ON USERS.ID = f.user_id;";
        return jdbc.query(query, JdbcUserRepository::extractUserData);
    }

    public Collection<User> getUserByIds(List<Integer> ids) {
        String query = "SELECT user_selected.Id,user_selected.email, user_selected.login, user_selected.name, user_selected.Birthday, f.FRIEND_ID " +
                "FROM (SELECT * FROM  PUBLIC.USERS u WHERE id IN (:ids)) AS user_selected " +
                "LEFT JOIN PUBLIC.FRIENDSHIP f " +
                "ON user_selected.ID = f.user_id;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        return jdbc.query(query, parameters, JdbcUserRepository::extractUserData);
    }

    public Optional<User> getUserById(Integer id) {
        String query = "SELECT user_selected.Id,user_selected.email, user_selected.login, user_selected.name, user_selected.Birthday, f.FRIEND_ID " +
                "FROM (SELECT * FROM  PUBLIC.USERS u WHERE id=:id) AS user_selected " +
                "LEFT JOIN PUBLIC.FRIENDSHIP f " +
                "ON user_selected.ID = f.user_id;";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
        List<User> result = jdbc.query(query, parameters, JdbcUserRepository::extractUserData);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.getFirst());
        }
    }

    public Optional<User> addFriend(Integer userId, Integer friendId) {
        String query = "INSERT INTO PUBLIC.\"FRIENDSHIP\" (USER_ID, FRIEND_ID) " +
                "VALUES (:userId, :friendId);";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("friendId", friendId);
        jdbc.update(query, parameters);
        return getUserById(userId);
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
