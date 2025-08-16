package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    final UserRepository userRepository;

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    private User getUserById(Integer userId) {
        Optional<User> userOptional = userRepository.getUserById(userId);
        return userOptional.orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
    }

    public User update(User user) {
        User userInStorage = getUserById(user.getId());
        userInStorage.setEmail(user.getEmail());
        userInStorage.setLogin(user.getLogin());
        userInStorage.setBirthday(user.getBirthday());
        userInStorage.setName(user.getName());
        return userRepository.update(userInStorage);
    }

    public User addFriend(Integer userId, Integer friendId) {
        final User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        final User friend = userRepository.getUserById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг с ID=" + friendId + " не найден"));
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Ошибка. id пользователя и друга совпадают");
        }
        if (currentUser.getFriends().contains(friendId)) {
            throw new ValidationException(String.format("Ошибка. Друзей пользователя id=%s уже есть друг с id=%s",
                    userId, friendId));
        }
        return userRepository.addFriend(userId, friendId)
                .orElseThrow(() -> new InternalError("Ошибка при добавлении друга"));
    }

    public List<User> getFriends(Integer userId) {
        final User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));
        return userRepository.getFriends(userId).stream()
                .map(friendId -> {
                    Optional<User> userOpt = userRepository.getUserById(friendId);
                    return userOpt.orElseThrow(() -> new FriendNotFoundException("Дружба с несуществующим пользователем id=" + userId));
                })
                .collect(Collectors.toList());
    }

    public User removeFriend(Integer userId, Integer friendId) {
        final User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        final User friend = userRepository.getUserById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + friendId + " не найден"));

        if (!userRepository.getFriends(userId).contains(friendId)) {
            throw new FriendNotFoundException(String.format("В списке друзей пользователя ID=%s " +
                    "не найден друг с ID=%s", userId, friendId));
        }

        userRepository.removeFriend(userId, friendId);
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new InternalError("Ошибка при удалении дружбы"));
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        final User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        final User otherUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + otherId + " не найден"));

        Set<Integer> commonFriends = new HashSet<>(userRepository.getFriends(userId));
        commonFriends.retainAll(userRepository.getFriends(otherId));
        return commonFriends.stream()
                .map(friendId -> {
                    Optional<User> userOpt = userRepository.getUserById(friendId);
                    return userOpt.orElseThrow(() -> new FriendNotFoundException("Дружба с несуществующим пользователем id=" + friendId));
                })
                .collect(Collectors.toList());
    }
}
