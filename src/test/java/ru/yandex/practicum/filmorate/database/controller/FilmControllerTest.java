package ru.yandex.practicum.filmorate.database.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    private final FilmController filmController;
    private final DbFilmStorage filmStorage;

    private Film movie;
    private Film updatedMovie;
    private Film movieWithoutName;
    private Film movieDescription200;
    private Film movieDescription201;
    private Film movieDurationNegative;
    private Film movieDurationZero;
    private Film movie28dec1895;
    private Film movie27dec1895;

    @BeforeEach
    void setUp() {
        movie = new Film("The Showshank Redemption", "Over the course of several years, two convicts",
                LocalDate.of(1993, 5, 6), 141, filmStorage.setMpa(1));

        updatedMovie = new Film(1, "The Shawshank Redemption", "Over the course of several years, two convicts form a friendship",
                LocalDate.of(1994, 6, 7), 142, filmStorage.setMpa(3));

        movieWithoutName = new Film("", "The jury of 12 in a New York City murder trial",
                LocalDate.of(1957, 2, 9), 142, filmStorage.setMpa(2));

        movieDescription200 = new Film("The Godfather", "The Godfather Vito Corleone is the head of the Corleone mafia family in New York. He is at the event of his daughter's wedding. His youngest son Michael and a decorated WW II Marine is also present...",
                LocalDate.of(1972, 5, 17), 175, filmStorage.setMpa(4));

        movieDescription201 = new Film("The Godfather 2", "The Godfather Vito Corleone is the head of the Corleone mafia family in New York. He is at the event of his daughter's wedding. His youngest son Michael and a decorated WW II Marine is also present...+",
                LocalDate.of(1972, 11, 12), 163, filmStorage.setMpa(1));

        movieDurationNegative = new Film("Hotaru no haka", "A young boy and his little sister struggle to survive",
                LocalDate.of(1988, 3, 12), -107, filmStorage.setMpa(2));

        movieDurationZero = new Film("Memento", "A man with short-term memory loss attempts ",
                LocalDate.of(2004, 2, 23), 0, filmStorage.setMpa(3));

        movie28dec1895 = new Film("Interstellar", "A team of explorers travel through a wormhole in space",
                LocalDate.of(1895, 12, 28), 142, filmStorage.setMpa(5));

        movie27dec1895 = new Film("Inception", "A thief who steals corporate secrets through the use of dream-sharing technology",
                LocalDate.of(1895, 12, 27), 142, filmStorage.setMpa(1));

    }


    @Test
    @DisplayName("Добавляем новый фильм с валидными данными")
    void addNewFilmTest() {
        filmController.addFilm(movie);

        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    @DisplayName("Добавляем новый фильм с пустым названием")
    void addFilmWithoutNameTest() {
        assertThrows(ValidationException.class, () -> filmController.addFilm(movieWithoutName));
        assertFalse(filmController.getFilms().contains(movieWithoutName), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем фильм с описанием в 200 знаков")
    void addFilmWith200SymbolsDescriptionTest() {
        filmController.addFilm(movieDescription200);

        assertTrue(filmController.getFilms().contains(movieDescription200), "Фильм не добавлен");
    }

    @Test
    @DisplayName("Добавляем фильм с описанием в 201 знак")
    void addFilmWith201SymbolsDescriptionTest() {
        assertThrows(ValidationException.class, () -> filmController.addFilm(movieDescription201), "Описание меньше 200.");
        assertFalse(filmController.getFilms().contains(movieDescription201), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем фильм с нулевой продолжительностью")
    void addFilmWithDurationZeroTest() {
        assertThrows(ValidationException.class, () -> filmController.addFilm(movieDurationZero));
        assertFalse(filmController.getFilms().contains(movieDurationZero), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем фильм с отрицательной продолжительностью")
    void addFilmWithNegativeDurationTest() {
        assertThrows(ValidationException.class, () -> filmController.addFilm(movieDurationNegative));
        assertFalse(filmController.getFilms().contains(movieDurationNegative), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Добавляем фильм c датой релиза за день до начала киноэпохи")
    void addFilmWithDateBefore28Dec1895Test() {
        assertThrows(ValidationException.class, () -> filmController.addFilm(movie27dec1895));
        assertFalse(filmController.getFilms().contains(movie27dec1895), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Получаем список со всеми фильмами")
    void getAllFilmsTest() {
        movieDescription200.setId(2);
        movie28dec1895.setId(3);
        filmController.addFilm(movie);
        filmController.addFilm(movieDescription200);
        filmController.addFilm(movie28dec1895);

        assertEquals(3, filmController.getFilms().size());
    }

    @Test
    @DisplayName("Обновляем фильм с валидными данными")
    void updateFilmWithValidDataTest() {
        filmController.addFilm(movie);

        assertEquals(1, filmController.getFilms().size(), "Хранилище пустое.");
        assertTrue(filmController.getFilms().contains(movie), "Фильм не добавлен");

        filmController.updateFilm(updatedMovie);
        Film returnedFilm = filmController.getFilm(1);

        assertEquals(1, filmController.getFilms().size(), "Хранилище пустое.");
        assertEquals(returnedFilm.getName(), updatedMovie.getName(), "Названия фильмов не совпадают.");
        assertEquals(returnedFilm.getDescription(), updatedMovie.getDescription(), "Описания фильмов не совпадают.");
        assertEquals(returnedFilm.getReleaseDate(), updatedMovie.getReleaseDate(), "Даты выхода фильмов не совпадают.");
        assertEquals(returnedFilm.getDuration(), updatedMovie.getDuration(), "Продолжительности фильмов не совпадают.");

    }

    @Test
    @DisplayName("Обновляем фильм на фильм с пустым названием")
    void updateFilmWithoutNameTest() {
        filmController.addFilm(movie);
        movieWithoutName.setId(1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieWithoutName));
        assertFalse(filmController.getFilms().contains(movieWithoutName), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с описанием в 200 знаков")
    void updateFilmWith200SymbolsDescriptionTest() {
        filmController.addFilm(movie);
        movieDescription200.setId(1);
        filmController.updateFilm(movieDescription200);

        assertTrue(filmController.getFilms().contains(movieDescription200), "Фильм не добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с описанием в 201 знак")
    void updateFilmWith201SymbolsDescriptionTest() {
        filmController.addFilm(movie);
        movieDescription201.setId(1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDescription201), "Описание меньше 200.");
        assertFalse(filmController.getFilms().contains(movieDescription201), "Фильм ошибочно добавлен");

    }

    @Test
    @DisplayName("Обновляем фильм на фильм с нулевой продолжительностью")
    void updateFilmWithDurationZeroTest() {
        filmController.addFilm(movie);
        movieDurationZero.setId(1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDurationZero));
        assertFalse(filmController.getFilms().contains(movieDurationZero), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с отрицательной продолжительностью")
    void updateFilmWithNegativeDurationTest() {
        filmController.addFilm(movie);
        movieDurationNegative.setId(1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDurationNegative));
        assertFalse(filmController.getFilms().contains(movieDurationNegative), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм c датой релиза за день до начала киноэпохи")
    void updateFilmWithDateBefore28Dec1895Test() {
        filmController.addFilm(movie);
        movie27dec1895.setId(1);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movie27dec1895));
        assertFalse(filmController.getFilms().contains(movie27dec1895), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм c датой релиза в день начала киноэпохи")
    void updateFilmWithDate28Dec1895Test() {
        filmController.addFilm(movie);
        movie28dec1895.setId(1);
        filmController.updateFilm(movie28dec1895);

        assertTrue(filmController.getFilms().contains(movie28dec1895), "Фильм не добавлен");
    }

}