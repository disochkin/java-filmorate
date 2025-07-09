package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        return user;
    }

    public User update(User user) {
        User userInStorage = userStorage.getUserById(user.getId());
        if (userInStorage == null) {
            throw new NoSuchElementException("Пользователь с ID=" + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public User addFriend(Long userId, Long friendId) {
        User currentUser = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (currentUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        if (friend == null) {
            throw new NoSuchElementException("Друг с ID=" + friendId + " не найден");
        }
        currentUser.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.update(currentUser);
        userStorage.update(friend);
        return currentUser;
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        Set<Long> userIdSet = user.getFriends();
        return userIdSet.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User removeFriend(Long userId, Long friendId) {
        User currentUser = userStorage.getUserById(userId);
        if (currentUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NoSuchElementException("Пользователь с ID=" + friendId + " не найден");
        }
        if (!currentUser.getFriends().contains(friendId)) {
            throw new FriendNotFoundException(String.format("В списке друзей пользователя ID=%s " +
                    "не найден друг с ID=%s", userId, friendId));
        }
        if (!friend.getFriends().contains(userId)) {
            throw new FriendNotFoundException(String.format("В списке друзей пользователя ID=%s " +
                    "не найден друг с ID=%s", friendId, userId));
        }
        currentUser.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.update(currentUser);
        userStorage.update(friend);
        return currentUser;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User currentUser = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        if (currentUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }
        if (otherUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + otherId + " не найден");
        }
        Set<Long> commonFriends = new HashSet<>(currentUser.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

}
