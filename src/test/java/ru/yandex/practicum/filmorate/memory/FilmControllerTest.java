package ru.yandex.practicum.filmorate.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

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
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        movie = new Film(1, "The Showshank Redemption", "Over the course of several years, two convicts",
                LocalDate.of(1993, 5, 6), 141);
        updatedMovie = new Film(1, "The Shawshank Redemption", "Over the course of several years, two convicts form a friendship",
                LocalDate.of(1994, 6, 7), 142);
        movieWithoutName = new Film(1, "", "The jury of 12 in a New York City murder trial",
                LocalDate.of(1957, 2, 9), 142);
        movieDescription200 = new Film(1, "The Godfather", "The Godfather Vito Corleone is the head of the Corleone mafia family in New York. He is at the event of his daughter's wedding. His youngest son Michael and a decorated WW II Marine is also present...",
                LocalDate.of(1972, 5, 17), 175);
        movieDescription201 = new Film(1, "The Godfather 2", "The Godfather Vito Corleone is the head of the Corleone mafia family in New York. He is at the event of his daughter's wedding. His youngest son Michael and a decorated WW II Marine is also present...+",
                LocalDate.of(1972, 11, 12), 163);
        movieDurationNegative = new Film(1, "Hotaru no haka", "A young boy and his little sister struggle to survive",
                LocalDate.of(1988, 3, 12), -107);
        movieDurationZero = new Film(1, "Memento", "A man with short-term memory loss attempts ",
                LocalDate.of(2004, 2, 23), 0);
        movie28dec1895 = new Film(1, "Interstellar", "A team of explorers travel through a wormhole in space",
                LocalDate.of(1895, 12, 28), 142);
        movie27dec1895 = new Film(1, "Inception", "A thief who steals corporate secrets through the use of dream-sharing technology",
                LocalDate.of(1895, 12, 27), 142);

    }

    @AfterEach
    void tearDown() {
        filmController = null;
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
    @DisplayName("Добавляем фильм c датой релиза в день начала киноэпохи")
    void addFilmWithDate28Dec1895Test() {
        filmController.addFilm(movie28dec1895);

        assertTrue(filmController.getFilms().contains(movie28dec1895), "Фильм не добавлен");
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

        assertEquals(1, filmController.getFilms().size(), "Хранилище пустое.");
        assertEquals(movie.getName(), updatedMovie.getName(), "Названия фильмов не совпадают.");
        assertEquals(movie.getDescription(), updatedMovie.getDescription(), "Описания фильмов не совпадают.");
        assertEquals(movie.getReleaseDate(), updatedMovie.getReleaseDate(), "Даты выхода фильмов не совпадают.");
        assertEquals(movie.getDuration(), updatedMovie.getDuration(), "Продолжительности фильмов не совпадают.");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с пустым названием")
    void updateFilmWithoutNameTest() {
        filmController.addFilm(movie);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieWithoutName));
        assertFalse(filmController.getFilms().contains(movieWithoutName), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с описанием в 200 знаков")
    void updateFilmWith200SymbolsDescriptionTest() {
        filmController.addFilm(movie);
        filmController.updateFilm(movieDescription200);

        assertTrue(filmController.getFilms().contains(movieDescription200), "Фильм не добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с описанием в 201 знак")
    void updateFilmWith201SymbolsDescriptionTest() {
        filmController.addFilm(movie);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDescription201), "Описание меньше 200.");
        assertFalse(filmController.getFilms().contains(movieDescription201), "Фильм ошибочно добавлен");

    }

    @Test
    @DisplayName("Обновляем фильм на фильм с нулевой продолжительностью")
    void updateFilmWithDurationZeroTest() {
        filmController.addFilm(movie);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDurationZero));
        assertFalse(filmController.getFilms().contains(movieDurationZero), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм с отрицательной продолжительностью")
    void updateFilmWithNegativeDurationTest() {
        filmController.addFilm(movie);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movieDurationNegative));
        assertFalse(filmController.getFilms().contains(movieDurationNegative), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм c датой релиза за день до начала киноэпохи")
    void updateFilmWithDateBefore28Dec1895Test() {
        filmController.addFilm(movie);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(movie27dec1895));
        assertFalse(filmController.getFilms().contains(movie27dec1895), "Фильм ошибочно добавлен");
    }

    @Test
    @DisplayName("Обновляем фильм на фильм c датой релиза в день начала киноэпохи")
    void updateFilmWithDate28Dec1895Test() {
        filmController.addFilm(movie);
        filmController.updateFilm(movie28dec1895);

        assertTrue(filmController.getFilms().contains(movie28dec1895), "Фильм не добавлен");
    }

}