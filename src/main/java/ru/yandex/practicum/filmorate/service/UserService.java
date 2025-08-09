package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.JdbcUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    final JdbcUserRepository jdbcUserRepository;

    @Autowired
    UserService(@Qualifier("UserJdbcRepository") JdbcUserRepository jdbcUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
    }


    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return jdbcUserRepository.save(user);
    }

    public Collection<User> findAll() {
        return jdbcUserRepository.findAll();
    }

    private User getUserById(Integer userId) {
        Optional<User> userOptional = jdbcUserRepository.getUserById(userId);
        return userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
    }

    public User update(User user) {
        User userInStorage = getUserById(user.getId());
        userInStorage.setEmail(user.getEmail());
        userInStorage.setLogin(user.getLogin());
        userInStorage.setBirthday(user.getBirthday());
        userInStorage.setName(user.getName());
        return jdbcUserRepository.update(userInStorage);
    }

    public User addFriend(Integer userId, Integer friendId) {
        final User currentUser = jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        final User friend = jdbcUserRepository.getUserById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг с ID=" + friendId + " не найден"));

        jdbcUserRepository.addFriend(userId, friendId);
        return currentUser;
    }

    public List<User> getFriends(Integer userId) {
        final User currentUser = jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        return jdbcUserRepository.getFriends(userId).stream()
                .map(friendId -> {
                    Optional<User> userOpt = jdbcUserRepository.getUserById(friendId);
                    return userOpt.orElseThrow(() -> new FriendNotFoundException("Дружба с несуществующим пользователем id=" + userId));
                })
                .collect(Collectors.toList());
    }

    public User removeFriend(Integer userId, Integer friendId) {
        final User currentUser = jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        final User friend = jdbcUserRepository.getUserById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг с ID=" + friendId + " не найден"));

        if (!jdbcUserRepository.getFriends(userId).contains(friendId)) {
            throw new FriendNotFoundException(String.format("В списке друзей пользователя ID=%s " +
                    "не найден друг с ID=%s", userId, friendId));
        }
        if (!jdbcUserRepository.getFriends(friendId).contains(userId)) {
            throw new FriendNotFoundException(String.format("В списке друзей пользователя ID=%s " +
                    "не найден друг с ID=%s", friendId, userId));
        }
        jdbcUserRepository.removeFriendship(userId, friendId);
        return currentUser;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        final User currentUser = jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        final User otherUser = jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + otherId + " не найден"));

        Set<Integer> commonFriends = new HashSet<>(jdbcUserRepository.getFriends(userId));
        commonFriends.retainAll(jdbcUserRepository.getFriends(otherId));
        return commonFriends.stream()
                .map(friendId -> {
                    Optional<User> userOpt = jdbcUserRepository.getUserById(friendId);
                    return userOpt.orElseThrow(() -> new FriendNotFoundException("Дружба с несуществующим пользователем id=" + friendId));
                })
                .collect(Collectors.toList());
    }

}
