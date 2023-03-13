package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public Map<Integer, User> allUsers() {
        return users;
    }

    @Override
    public User addUser(@RequestBody User user) {
        if (users.containsKey(user.getId()) || isRepeat(user)) {
            logAndThrowNotFound("Пользователь уже занесен в базу");
        }
        if (isValid(user)) {
            user.setId(id++);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: " + user);
        }
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
        if (isValid(user)) {
            User updatedUser = users.get(user.getId());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setLogin(user.getLogin());
            updatedUser.setName(user.getName());
            updatedUser.setBirthday(user.getBirthday());
            users.put(user.getId(), updatedUser);
            log.debug("Обновлены данные пользователя " + user);
        }
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
