package ru.yandex.practicum.filmorate.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.like.DbLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DbLikesStorageTest {
    private final DbUserStorage userStorage;
    private final DbLikesStorage likesStorage;
    private final DbFilmStorage filmStorage;
    private Film movie;
    private Film movie2;
    private Film movie3;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        movie = new Film("Memento", "A man with short-term memory loss attempts ",
                LocalDate.of(2004, 2, 23), 121, filmStorage.setMpa(3));

        movie2 = new Film("The Shawshank Redemption", "Over the course of several years",
                LocalDate.of(1993, 5, 6), 141, filmStorage.setMpa(2));

        movie3 = new Film("Forrest Gump", "Alabama man with an IQ of 75, whose only desire",
                LocalDate.of(1994, 12, 28), 152, filmStorage.setMpa(4));


        user1 = new User("vasya666@mail.ru", "Nagibator", "Vasya",
                LocalDate.of(2001, 1, 21));

        user2 = new User("salvador@t.ru", "lucky777", "Max",
                LocalDate.of(1989, 4, 8));

        user3 = new User("pierrechaplin@ro.ru", "mutnaya", "Olga",
                LocalDate.of(2000, 11, 19));

        userStorage.addUser(user1);

    }

    @Test
    @DisplayName("Добавляем лайк фильму")
    void addLikeTest() {
        filmStorage.addFilm(movie);

        assertThat(filmStorage.getFilmsMap().get(1).getLikes()).isEmpty();

        likesStorage.addLike(1, 1);

        assertThat(filmStorage.getFilmsMap().get(1).getLikes()).contains(1);
    }

    @Test
    @DisplayName("Удаляем лайк фильму")
    void removeLikeTest() {
        filmStorage.addFilm(movie);
        likesStorage.addLike(1, 1);

        assertThat(filmStorage.getFilmsMap().get(1).getLikes()).contains(1);

        likesStorage.removeLike(1, 1);

        assertThat(filmStorage.getFilmsMap().get(1).getLikes()).isEmpty();
    }

    @Test
    @DisplayName("Запрашиваем 2 самых популярных фильма")
    void getPopularFilmsTest() {
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        filmStorage.addFilm(movie);
        filmStorage.addFilm(movie2);
        filmStorage.addFilm(movie3);
        movie.setMpa(null);
        movie2.setMpa(null);
        List<Film> expectedResult = List.of(movie, movie2);

        likesStorage.addLike(1, 1);
        likesStorage.addLike(2, 1);
        likesStorage.addLike(3, 1);
        likesStorage.addLike(1, 2);
        likesStorage.addLike(2, 2);
        likesStorage.addLike(1, 3);

        assertThat(likesStorage.getPopularFilms(2)).isEqualTo(expectedResult);
    }

}