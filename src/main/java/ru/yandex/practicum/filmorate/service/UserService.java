package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Map<Integer, User> getUsersMap() {
        return userStorage.getUsersMap();
    }

    public User addUser(User user) {
        if (isValid(user)) {
            userStorage.addUser(user);
        }
        return user;
    }

    public User updateUser(User user) {
        if (isValid(user)) {
            userStorage.updateUser(user);
        }
        return user;
    }

    public void removeUser(Integer id) {
        userStorage.removeUser(id);
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (userId < 0 || friendId < 0) {
            logAndThrowNotFound("Отрицательный id" + userId + friendId);
        }
        return userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId < 0 || friendId < 0) {
            logAndThrowException("Отрицательный id" + userId + friendId);
        }
        userStorage.deleteFriend(userId, friendId);
    }

    private boolean isValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            logAndThrowException("Электронная почта пустая или отсуствует символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            logAndThrowException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            logAndThrowException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    public List<User> getFriends(Integer userId) {
        return getUser(userId).getFriends().stream()
                .map(id -> getUser(id))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer oneId, Integer anotherId) {
        return getUser(oneId).getFriends().stream()
                .filter(id -> getUser(anotherId).getFriends().contains(id))
                .map(id -> getUser(id))
                .collect(Collectors.toList());
    }

    private void logAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }

    private void logAndThrowNotFound(String message) {
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }


}
