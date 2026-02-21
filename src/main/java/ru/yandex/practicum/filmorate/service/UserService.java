package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
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

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("UserService: Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        if (user == null) {
            log.warn("UserService: Попытка удалить друга у несуществующего пользователя с ID={}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        User friend = getUserById(friendId);
        if (friend == null) {
            log.warn("UserService: Попытка удалить несуществующего друга ID={}", friendId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            userStorage.update(user);
            log.info("UserService: Пользователь {} удалил из друзей пользователя {}.", userId, friendId);
        } else {
            log.warn("UserService: Попытка удалить несуществующего друга (userId={}, friendId={}", userId, friendId);
            throw new UserNotFoundException("Пользователь с ID = " + friendId + " не найден в списке друзей");
        }

        if (friend.getFriends().contains(userId)) {
            friend.getFriends().remove(userId);
            userStorage.update(friend);
            log.info("UserService: Пользователь {} удалил из друзей пользователя {}.", friendId, userId);
        } else {
            log.warn("UserService: Попытка удалить несуществующего друга(friendId={}, userId={}", friendId, userId);
            throw new UserNotFoundException("Пользователь с ID = " + userId + "не найден в списке друзей");
        }
    }

    public List<User> getFriends(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            log.warn("UserService: Попытка получить список друзей у несуществующего пользователя с ID={}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<User> friendsList = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friendsList.add(getUserById(friendId));
        }
        log.debug("UserService: Получен список друзей для пользователя {}. Количество: {}", userId, friendsList.size());
        return friendsList;
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

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(otherUserFriends);

        List<User> commonFriendsList = new ArrayList<>();
        for (Long friendId : commonFriendsIds) {
            commonFriendsList.add(getUserById(friendId));
        }
        log.debug("UserService: Получен список общих друзей для пользователей {} и {}. Количество: {}",
                userId, otherUserId, commonFriendsList.size());
        return commonFriendsList;
    }
}
