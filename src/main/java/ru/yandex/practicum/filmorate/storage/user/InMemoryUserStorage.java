package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private Long getNextId() {
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
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация: пустой email");
            throw new ValidationsException("Имейл не может быть пустым");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация: дата рождения не может быть в будущем");
            throw new ValidationsException("Дата рождения не может быть в будущем");
        }
    }

    public User validateUserIfNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя для отображения не указано. Будет использован login={}", user.getLogin());
            user.setName(user.getLogin());
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        log.debug("InMemoryUserStorage: Получение всех пользователей. Всего: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        validateUser(user);
        User userToSave = validateUserIfNameIsEmpty(user);
        userToSave.setId(getNextId());
        users.put(userToSave.getId(), userToSave);
        log.info("InMemoryUserStorage: Добавлен пользователь с ID={}. Логин: {}", userToSave.getId(), userToSave.getLogin());
        return userToSave;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("InMemoryUserStorage: Попытка обновления несуществующего пользователя с ID={}", user.getId());
            throw new UserNotFoundException("Пользователь с ID = " + user.getId() + " не найден");
        }
        validateUser(user);
        User userToUpdate = validateUserIfNameIsEmpty(user);
        users.put(userToUpdate.getId(), userToUpdate);
        log.info("InMemoryUserStorage: Обновлен пользователь с ID={}. Логин: {}", userToUpdate.getId(), userToUpdate.getLogin());
        return userToUpdate;
    }

    @Override
    public Optional<User> getById(Long id) {
        log.debug("InMemoryUserStorage: Получение пользователя по ID={}", id);
        return Optional.ofNullable(users.get(id));
    }
}
