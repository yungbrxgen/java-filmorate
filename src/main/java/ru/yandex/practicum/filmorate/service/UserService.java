package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.debug("UserService: Создание пользователя");
        return userStorage.save(user);
    }

    public User updateUser(User user) {
        log.debug("UserService: Обновление пользователя с ID={}", user.getId());
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        log.debug("UserService: Получение всех пользователей");
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        log.debug("UserService: Получение пользователя по ID={}", id);
        return userStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("UserService: Пользователь с ID={} не найден", id);
                    return new UserNotFoundException("Пользователь с ID = " + id + " не найден");
                });
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.warn("UserService: Попытка добавить себя в друзья(userId={}, friendId={}", userId, friendId);
            throw new ValidationsException("Пользователь не может добавить себя в друзья.");
        }

        User user = getUserById(userId);
        if (user == null) {
            log.warn("UserService: Попытка добавить друга у несуществующего пользователя с ID={}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
        User friend = getUserById(friendId);
        if (friend == null) {
            log.warn("UserService: Попытка добавить несуществующего друга ID={}", friendId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (friend.getFriendshipStatus().containsKey(userId)) {
            user.getFriendshipStatus().put(friendId, true);
            friend.getFriendshipStatus().put(userId, true);
            log.info("UserService: Дружба между {} и {} подтверждена", userId, friendId);
        } else {
            user.getFriendshipStatus().put(friendId, false);
            log.info("UserService: Пользователь {} отправил запрос в друзья {}", userId, friendId);
        }

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendshipStatus().remove(friendId);
        friend.getFriendshipStatus().remove(userId);

        log.info("UserService: Пользователь {} удалил из друзей {}.", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            log.warn("UserService: Попытка получить список друзей у несуществующего пользователя с ID={}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        log.debug("UserService: Получение списка друзей для пользователя {}.", userId);
        return user.getFriendshipStatus().keySet().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUserById(userId);
        if (user == null) {
            log.warn("UserService: Попытка получить общих друзей у пользователя ID={}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        User otherUser = getUserById(otherUserId);
        if (otherUser == null) {
            log.warn("UserService: Попытка получить общих друзей у пользователя ID={}", otherUserId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        Set<Long> userFriends = user.getFriendshipStatus().keySet();
        Set<Long> otherUserFriends = otherUser.getFriendshipStatus().keySet();

        log.debug("UserService: Получение списка общих друзей для пользователей {} и {}.", userId, otherUserId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
