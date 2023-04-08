package ru.yandex.practicum.filmorate.database.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    private final UserController userController;

    private User user;
    private User updatedUser;
    private User userNoEmail;
    private User userIncorrectEmail;
    private User userWithoutName;
    private User userEmptyLogin;
    private User userIncorrectLogin;
    private User userBornNow;
    private User userFromFuture;

    @BeforeEach
    void setUp() {
        user = new User("vasya666@mail.ru", "Nagibator", "Vasya",
                LocalDate.of(2001, 1, 21));
        updatedUser = new User(1, "vasya666@yandex.ru", "NagibatorXXX", "Vasiliy",
                LocalDate.of(2007, 7, 27));

        userNoEmail = new User("", "macho", "Steve",
                LocalDate.of(1996, 7, 16));

        userIncorrectEmail = new User( "chaplimailro.ru", "pitipon", "Pierre",
                LocalDate.of(1982, 3, 22));

        userWithoutName = new User( "salvador@t.ru", "lucky777", "",
                LocalDate.of(1989, 4, 8));

        userEmptyLogin = new User("pierrechaplin@ro.ru", "", "Olga",
                LocalDate.of(2000, 11, 19));

        userIncorrectLogin = new User( "ronin546@yandex.ru", "ro nin", "Igor",
                LocalDate.of(1984, 5, 13));

        userBornNow = new User("shadkhaniot@gmail.com", "shadkhan", "Nathan",
                LocalDate.now());

        userFromFuture = new User( "xiolin@taiwanpost.tw", "*linyu*", "linyu",
                LocalDate.of(2184, 5, 13));
    }

    @Test
    @DisplayName("Добавляем нового пользователя с валидными данными")
    void addUserTest() {
        userController.addUser(user);

        assertFalse(userController.getUsers().isEmpty());
    }

    @Test
    @DisplayName("Добавляем нового пользователя с пустой почтой")
    void addUserWithoutEmailTest() {
        assertThrows(ValidationException.class, () -> userController.addUser(userNoEmail));
        assertFalse(userController.getUsers().contains(userNoEmail), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя с адресом почты без значка @")
    void addUserWithIncorrectEmailTest() {
        assertThrows(ValidationException.class, () -> userController.addUser(userIncorrectEmail));
        assertFalse(userController.getUsers().contains(userIncorrectEmail), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя с пустым логином")
    void addUserWithEmptyLoginTest() {
        assertThrows(ValidationException.class, () -> userController.addUser(userEmptyLogin));
        assertFalse(userController.getUsers().contains(userEmptyLogin), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя с логином с пробелами")
    void addUserWithIncorrectLoginTest() {
        assertThrows(ValidationException.class, () -> userController.addUser(userIncorrectLogin));
        assertFalse(userController.getUsers().contains(userIncorrectLogin), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя с пустым именем")
    void addUserWithoutNameTest() {
        userController.addUser(userWithoutName);

        assertTrue(userController.getUsers().contains(userWithoutName), "Пользователь не добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя, родившегося прямо сейчас")
    void addUserBornNowTest() {
        userController.addUser(userBornNow);

        assertTrue(userController.getUsers().contains(userBornNow), "Пользователь не добавлен");
    }

    @Test
    @DisplayName("Добавляем нового пользователя из будущего")
    void addUserFromFutureTest() {
        assertThrows(ValidationException.class, () -> userController.addUser(userFromFuture));
        assertFalse(userController.getUsers().contains(userFromFuture), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Получаем список всех пользователей")
    void getAllUsersTest() {
        userWithoutName.setId(2);
        userBornNow.setId(3);
        userController.addUser(user);
        userController.addUser(userWithoutName);
        userController.addUser(userBornNow);

        assertEquals(3, userController.getUsers().size());
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с валидными данными")
    void updateUserTest() {
        userController.addUser(user);

        assertEquals(1, userController.getUsers().size(), "Хранилище пустое.");
        assertTrue(userController.getUsers().contains(user), "Пользователь не добавлен");

        userController.updateUser(updatedUser);
        User returnedUser = userController.getUser(1);

        assertEquals(1, userController.getUsers().size(), "Хранилище пустое.");
        assertEquals(returnedUser.getEmail(), updatedUser.getEmail(), "Эмейлы пользователей не совпадают.");
        assertEquals(returnedUser.getLogin(), updatedUser.getLogin(), "Логины пользователей не совпадают.");
        assertEquals(returnedUser.getName(), updatedUser.getName(), "Имена пользователей не совпадают.");
        assertEquals(returnedUser.getBirthday(), updatedUser.getBirthday(), "ДР пользователей не совпадают.");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с пустой почтой")
    void updateUserWithoutEmailTest() {
        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(userNoEmail));
        assertFalse(userController.getUsers().contains(userNoEmail), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с адресом почты без значка @")
    void updateUserWithIncorrectEmailTest() {
        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(userIncorrectEmail));
        assertFalse(userController.getUsers().contains(userIncorrectEmail), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с пустым логином")
    void updateUserWithEmptyLoginTest() {
        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(userEmptyLogin));
        assertFalse(userController.getUsers().contains(userEmptyLogin), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с логином с пробелами")
    void updateUserWithIncorrectLoginTest() {
        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(userIncorrectLogin));
        assertFalse(userController.getUsers().contains(userIncorrectLogin), "Пользователь ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные с пустым именем")
    void updateUserWithoutNameTest() {
        userController.addUser(user);
        userWithoutName.setId(1);
        userController.updateUser(userWithoutName);

        assertTrue(userController.getUsers().contains(userWithoutName), "Пользователь не добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные, родившегося прямо сейчас")
    void updateUserBornNowTest() {
        userController.addUser(user);
        userBornNow.setId(1);
        userController.updateUser(userBornNow);

        assertTrue(userController.getUsers().contains(userBornNow), "Пользователь не добавлен");
    }

    @Test
    @DisplayName("Обновляем данные пользователя на данные из будущего")
    void updateUserFromFutureTest() {
        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(userFromFuture));
        assertFalse(userController.getUsers().contains(userFromFuture), "Пользователь ошибочно добавлен");
    }


}