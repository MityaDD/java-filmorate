package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Map<Integer, User> getUsersMap() {
        return userStorage.getUsersMap();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void removeUser(Integer id) {
        userStorage.removeUser(id);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (userId < 0 || friendId < 0) {
            logAndThrowNotFound("Отрицательный id" + userId + friendId);
        }
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        if (userId < 0 || friendId < 0) {
            logAndThrowException("Отрицательный id" + userId + friendId);
        }
        User user = getUser(userId);
        User friend = getUser(friendId);

        if (!user.getFriends().contains(friendId)) {
            logAndThrowNotFound("В дурзьях нет пользователя с id = " + friendId);
        }
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());

        return user;
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

    public User getUser(Integer userId) {
        if (!getUsersMap().containsKey(userId)) {
            logAndThrowNotFound("В базе нет пользователя с id" + userId);
        }
        return getUsersMap().get(userId);
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
