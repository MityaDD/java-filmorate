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
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friends.DbFriendsStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DbFriendsStorageTest {
    private final DbFriendsStorage friendStorage;
    private final UserService userService;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void createUserForTests() {
        user1 = new User("vasya666@mail.ru", "Nagibator", "Vasya",
                LocalDate.of(2001, 1, 21));

        user2 = new User("salvador@t.ru", "lucky777", "Max",
                LocalDate.of(1989, 4, 8));

        user3 = new User("pierrechaplin@ro.ru", "mutnaya", "Olga",
                LocalDate.of(2000, 11, 19));
    }

    @Test
    @DisplayName("Добавляем друга")
    void addToFriendTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        friendStorage.addFriend(1, 2);

        assertThat(userService.getFriends(1)).contains(user2);
    }

    @Test
    @DisplayName("Удаляем друга")
    void removeFriendTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        friendStorage.addFriend(1, 2);

        assertThat(userService.getFriends(1)).contains(user2);

        friendStorage.removeFriend(1, 2);

        assertThat(userService.getFriends(1)).isEmpty();
    }

    @Test
    @DisplayName("Получаем список друзей для пользовотеля")
    void getUserFriendsTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        friendStorage.addFriend(1, 2);
        friendStorage.addFriend(1, 3);

        assertThat(userService.getFriends(1)).isEqualTo(List.of(user2, user3));
    }

    @Test
    @DisplayName("Получаем список взаимных друзей для для двух пользователей")
    void getMutualFriendsTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        friendStorage.addFriend(1, 3);
        friendStorage.addFriend(2, 3);

        assertThat(userService.getMutualFriends(1, 2)).isEqualTo(List.of(user3));
    }
}