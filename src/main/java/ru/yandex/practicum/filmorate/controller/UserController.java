package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET /users - возвращаем всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        log.info("POST /users - создание пользователя: {}", newUser);
        return userService.createUser(newUser);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("PUT /users - обновление пользователя id={}", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("GET /users/{id} - запрос на получение пользователя с ID={}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        log.info("PUT /users/{id}/friends/{friendId} - запрос на добавление в друзья: {} - {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        log.info("DELETE /users/{id}/friends/{friendId} - запрос на удаление из друзей: {} <-> {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("GET /users/{id}/friends - запрос на получение списка друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        log.info("GET /users/{id}/friends/common/{otherId} - запрос на получение списка общих друзей у пользователей: {} <-> {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
