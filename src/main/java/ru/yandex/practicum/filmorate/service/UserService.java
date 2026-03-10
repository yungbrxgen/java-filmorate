package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пустое. Для отображение будет использован логин");
            user.setName(user.getLogin());
        }
    }

    public User createUser(User user) {
        log.info("Создание нового пользователя: {}", user.getLogin());
        validateUserName(user);
        return userStorage.save(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя с ID={}", user.getId());
        validateUserName(user);
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        log.info("Запрос на получение списка пользователей");
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        log.info("Запрос на получение пользователя с ID={}", id);
        return userStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID={} не найден", id);
                    return new UserNotFoundException("Пользователь с ID " + id + " не найден");
                });
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь {} добавляет в друзья {}", userId, friendId);
        if (userId.equals(friendId)) {
            log.warn("Попытка отправить запрос дружбы себе");
            throw new ValidationsException("Пользователь не может добавить себя в друзья");
        }
        //проверка на существование пользователей
        getUserById(userId);
        getUserById(friendId);

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.warn("Попытка удалить себя из друзей");
            throw new ValidationsException("Пользователь не может находится в списке своих друзей");
        }

        log.info("Пользователь {} удаляет из друзей {}", userId, friendId);
        getUserById(userId);
        getUserById(friendId);

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public List<User> getFriends(Long id) {
        log.info("Получение списка друзей пользователя {}", id);
        getUserById(id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Запрос на получение списка общих друзей у пользователей {}, {}", userId, otherUserId);
        getUserById(userId);
        getUserById(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
