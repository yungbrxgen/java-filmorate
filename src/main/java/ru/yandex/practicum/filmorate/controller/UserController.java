package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("GET /users - возвращаем всех пользователей, count={}", users.size());
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            log.warn("Валидация: email не содержит @: {}", user.getEmail());
            throw new ValidationsException("Имейл должен содержать символ '@'");
        } else if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация: пустой email");
            throw new ValidationsException("Имейл не может быть пустым");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация: дата рождения не может быть в будущем");
            throw new ValidationsException("Дата рождения не может быть в будущем");
        }
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        log.info("POST /users - создание пользователя: {}", newUser);
        validateUser(newUser);

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        long id = getNextId();
        newUser.setId(id);
        users.put(id, newUser);
        log.info("Пользователь создан с id={}", id);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("PUT /users - обновление пользователя id={}", user != null ? user.getId() : null);
        validateUser(user);

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new NotFoundException("Пользователь с ID = " + user.getId() + " не найден");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User oldUser = users.get(user.getId());
        oldUser.setName(user.getName());
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthday(user.getBirthday());

        log.info("Пользователь с id={}", user.getId());
        return oldUser;
    }
}
