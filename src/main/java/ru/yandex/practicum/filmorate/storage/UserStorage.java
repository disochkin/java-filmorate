package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserStorage {
    User create(User user);

    Collection<User> findAll();

    Optional<User> getUserById(Long userId);

    User update(User user);
}
