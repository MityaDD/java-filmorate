package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    @Override
    public User addUser(@RequestBody User user) {
        if (users.containsKey(user.getId()) || isRepeat(user)) {
            logAndThrowNotFound("Пользователь уже занесен в базу");
        }

            user.setId(id++);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: " + user);

        return user;
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            logAndThrowException("Не введено id пользователя.");
        }
        if (!users.containsKey(user.getId())) {
            logAndThrowNotFound("Пользователя нет в базе.");
        }

            User updatedUser = users.get(user.getId());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setLogin(user.getLogin());
            updatedUser.setName(user.getName());
            updatedUser.setBirthday(user.getBirthday());
            users.put(user.getId(), updatedUser);
            log.debug("Обновлены данные пользователя " + user);

        return user;
    }

    @Override
    public void removeUser(Integer id) {

        if (!users.containsKey(id)) {
            logAndThrowNotFound("Нет в базе пользователя с id " + id);
        }
        users.remove(id);
        log.debug("Удален пользователь " + id);
    }

    public User getUser(Integer userId) {
        if (!getUsersMap().containsKey(userId)) {
            logAndThrowNotFound("В базе нет пользователя с id" + userId);
        }
        return getUsersMap().get(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return friend;
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
/*
        if (!user.getFriends().contains(friendId)) {
            logAndThrowNotFound("В дурзьях нет пользователя с id = " + friendId);
        }
 */
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    private boolean isRepeat(User user) {
        if (users.isEmpty()) {
            return false;
        }
        for (User savedUser : users.values()) {
            if (savedUser.getLogin().equals(user.getLogin())
                    && (savedUser.getEmail().equals(user.getEmail()))) {
                return true;
            }
        }
        return false;
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
