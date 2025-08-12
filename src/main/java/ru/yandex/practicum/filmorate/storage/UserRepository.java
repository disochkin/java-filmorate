package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User save(User user);

    User update(User user);

    Collection<User> findAll();

    Collection<User> getUserByIds(List<Integer> ids);

    Optional<User> getUserById(Integer id);

    Optional<User> addFriend(Integer userId, Integer friendId);

    Collection<Integer> getFriends(Integer userId);

    void removeFriend(Integer userId, Integer friendId);

}
