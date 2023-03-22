package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Integer, User> getUsersMap();

    User addUser(User user);

    User updateUser(User user);

    void removeUser(Integer id);
}
