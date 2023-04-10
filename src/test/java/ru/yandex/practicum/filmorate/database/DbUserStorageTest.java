package ru.yandex.practicum.filmorate.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DbUserStorageTest {
    private final DbUserStorage userStorage;
    private User user;
    private User updatedUser;
    private User user2;

    @BeforeEach
    public void createUserForTests() {

        user = new User("vasya666@mail.ru", "Nagibator", "Vasya",
                LocalDate.of(2001, 1, 21));

        updatedUser = new User(1, "vasya777@yandex.ru", "NagibatorXXX", "Vasiliy",
                LocalDate.of(2007, 7, 27));

        user2 = new User("salvador@t.ru", "lucky777", "Max",
                LocalDate.of(1989, 4, 8));

        userStorage.addUser(user);
    }

    @Test
    @DisplayName("Возвращает пользователя, если БД не пуста")
    void returnUserWhenDbNotEmptyTest() {
        Map<Integer, User> users = userStorage.getUsersMap();
        User returnedUser = users.get(1);

        assertThat(users).hasSize(1);
        assertThat(returnedUser.getEmail()).isEqualTo("vasya666@mail.ru");
        assertThat(returnedUser.getLogin()).isEqualTo("Nagibator");
        assertThat(returnedUser.getName()).isEqualTo("Vasya");
        assertThat(returnedUser.getBirthday()).isEqualTo("2001-01-21");
    }

    @Test
    @DisplayName("Добавляем пользователя")
    void addUserTest() {
        userStorage.addUser(user2);
        Map<Integer, User> users = userStorage.getUsersMap();
        User returnedUser = users.get(2);

        assertThat(returnedUser.getEmail()).isEqualTo(user2.getEmail());
        assertThat(returnedUser.getLogin()).isEqualTo(user2.getLogin());
        assertThat(returnedUser.getName()).isEqualTo(user2.getName());
        assertThat(returnedUser.getBirthday()).isEqualTo(user2.getBirthday());
    }

    @Test
    @DisplayName("Обновляем пользователя")
    void updateUserTest() {
        userStorage.updateUser(updatedUser);
        User returnedUser = userStorage.getUsersMap().get(1);

        assertThat(returnedUser).isEqualTo(updatedUser);
    }

    @Test
    @DisplayName("Удаляем пользователя")
    void removeUserTest() {
        userStorage.addUser(user2);

        assertThat(userStorage.getUsersMap()).hasSize(2);

        userStorage.removeUser(1);

        assertThat(userStorage.getUsersMap()).hasSize(1);
        assertThat(userStorage.getUsersMap().get(2)).isEqualTo(user2);

        userStorage.removeUser(2);

        assertThat(userStorage.getUsersMap()).isEmpty();
    }

    @Test
    @DisplayName("Получаем пользователя по id")
    void getUserByIdTest() {
        userStorage.addUser(user2);
        User returnedUser = userStorage.getUser(2);

        assertThat(returnedUser).isEqualTo(user2);
    }
}