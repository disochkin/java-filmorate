package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")

public class UserController {
    static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @RequestMapping(path = "/{id}/friends/{friendId}", method = RequestMethod.PUT)
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @RequestMapping(path = "/{id}/friends", method = RequestMethod.GET)
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @RequestMapping(path = "/{id}/friends/{friendId}", method = RequestMethod.DELETE)
    public User getFriends(@PathVariable long id, @PathVariable long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @RequestMapping(path = "/{id}/friends/common/{otherId}", method = RequestMethod.GET)
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
