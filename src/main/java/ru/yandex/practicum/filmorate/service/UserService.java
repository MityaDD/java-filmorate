package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Map<Integer, User> allUsers() {
        return userStorage.allUsers();
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
        List<User> friends = new ArrayList<>();
        User user = getUser(userId);
        Set<Integer> friendsIdSet = user.getFriends();
        for (Integer id : friendsIdSet) {
            friends.add(userStorage.allUsers().get(id));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(Integer oneId, Integer anotherId) {
        List<User> mutualFriends = new ArrayList<>();
        User oneUser = getUser(oneId);
        User anotherUser = getUser(anotherId);
        Set<Integer> oneSet = oneUser.getFriends();
        Set<Integer> anotherSet = anotherUser.getFriends();
        for (Integer user : oneSet) {
            if (anotherSet.contains(user)) {
                mutualFriends.add(userStorage.allUsers().get(user));
            }
        }
        return mutualFriends;
    }

    public User getUser(Integer userId) {
        if (!userStorage.allUsers().containsKey(userId)) {
            logAndThrowNotFound("В базе нет пользователя с id" + userId);
        }
        return userStorage.allUsers().get(userId);
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
