package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Validated
@AllArgsConstructor
public class UserController {
    static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;


    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<User> findAll() {
        return userService.findAll();
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
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @RequestMapping(path = "/{id}/friends", method = RequestMethod.GET)
    public Collection<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @RequestMapping(path = "/{id}/friends/{friendId}", method = RequestMethod.DELETE)
    public User getFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.removeFriend(id, friendId);
    }

    @RequestMapping(path = "/{id}/friends/common/{otherId}", method = RequestMethod.GET)
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
