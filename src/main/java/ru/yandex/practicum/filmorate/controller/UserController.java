package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/users"})
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<User> getUsers() {
        log.info("Всего в базе пользователей: ", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (users.containsKey(user.getId()) || isRepeat(user)) {
            logAndThrowException("Пользователь уже занесен в базу");
        }
        if (isValid(user)) {
            user.setId(id++);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: " + user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            logAndThrowException("Не введено id пользователя.");
        }
        if (!users.containsKey(user.getId())) {
            logAndThrowException("Пользователя нет в базе.");
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

}
