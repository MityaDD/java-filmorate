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
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DbFilmStorageTest {
    private final DbFilmStorage filmStorage;
    private Film movie;
    private Film movieWithGenres;
    private Film updatedMovie;
    private Film updatedMovieWithGenres;

    @BeforeEach
    public void setUp() {

        int[] genres = {1, 2};
        int[] updatedGenres = {2, 3};
        movie = new Film("The Shawshank Redemption", "Over the course of several years, two convicts",
                LocalDate.of(1993, 5, 6), 141, filmStorage.setMpa(2));
        movieWithGenres = new Film("Memento", "A man with short-term memory loss attempts ",
                LocalDate.of(2004, 2, 23), 121, filmStorage.setMpa(3),filmStorage.setGenres(genres));
        updatedMovie = new Film(1,"Red Dead Redemption", "the family of former outlaw is kidnapped",
                LocalDate.of(2003, 3, 7), 101, filmStorage.setMpa(1));
        updatedMovieWithGenres = new Film(2, "Memento mori", "dulce et decorum est pro patria mori",
                LocalDate.of(2014, 1, 13), 181, filmStorage.setMpa(4),filmStorage.setGenres(updatedGenres));

        filmStorage.addFilm(movie);

    }

    @Test
    @DisplayName("Проверяем извлечение фильма по id")
    void shouldReturnFilmById() {
        filmStorage.addFilm(movieWithGenres);

        assertThat(filmStorage.getFilm(1)).isEqualTo(movie);
        assertThat(filmStorage.getFilm(2)).isEqualTo(movieWithGenres);
    }

    @Test
    @DisplayName("Добавляем фильм без жанра")
    void addFilmWithoutGenresTest() {
        Map<Integer, Film> films = filmStorage.getFilmsMap();
        Film returnedFilm = films.get(1);

        assertThat(films).hasSize(1);
        assertThat(returnedFilm.getId()).isEqualTo(1);
        assertThat(returnedFilm.getName()).isEqualTo("The Shawshank Redemption");
        assertThat(returnedFilm.getDescription()).isEqualTo("Over the course of several years, two convicts");
        assertThat(returnedFilm.getReleaseDate()).isEqualTo("1993-05-06");
        assertThat(returnedFilm.getDuration()).isEqualTo(141);
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Добавляем фильм c жанрами")
    void addFilmWithGenresTest() {
        filmStorage.addFilm(movieWithGenres);
        Map<Integer, Film> films = filmStorage.getFilmsMap();
        Film returnedFilm = films.get(2);

        assertThat(films).hasSize(2);
        assertThat(returnedFilm.getId()).isEqualTo(2);
        assertThat(returnedFilm.getName()).isEqualTo(movieWithGenres.getName());
        assertThat(returnedFilm.getDescription()).isEqualTo(movieWithGenres.getDescription());
        assertThat(returnedFilm.getReleaseDate()).isEqualTo(movieWithGenres.getReleaseDate());
        assertThat(returnedFilm.getDuration()).isEqualTo(movieWithGenres.getDuration());
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(movieWithGenres.getMpa().getId());
        assertThat(returnedFilm.getGenres()).isEqualTo(movieWithGenres.getGenres());
    }

    @Test
    @DisplayName("Обновляем фильм без жанра")
    void updateFilmWithoutGenresTest() {
        filmStorage.updateFilm(updatedMovie);
        Map<Integer, Film> films = filmStorage.getFilmsMap();
        Film returnedFilm = films.get(1);

        assertThat(updatedMovie).isEqualTo(filmStorage.getFilmsMap().get(1));
        assertThat(films).hasSize(1);
        assertThat(returnedFilm.getId()).isEqualTo(1);
        assertThat(returnedFilm.getName()).isEqualTo("Red Dead Redemption");
        assertThat(returnedFilm.getDescription()).isEqualTo("the family of former outlaw is kidnapped");
        assertThat(returnedFilm.getReleaseDate()).isEqualTo("2003-03-07");
        assertThat(returnedFilm.getDuration()).isEqualTo(101);
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Обновляем фильм с жанрами")
    void updateFilmWithGenresTest() {
        filmStorage.addFilm(updatedMovieWithGenres);
        Map<Integer, Film> films = filmStorage.getFilmsMap();
        Film returnedFilm = films.get(2);

        assertThat(updatedMovieWithGenres).isEqualTo(filmStorage.getFilmsMap().get(2));
        assertThat(films).hasSize(2);
        assertThat(returnedFilm.getId()).isEqualTo(2);
        assertThat(returnedFilm.getName()).isEqualTo("Memento mori");
        assertThat(returnedFilm.getDescription()).isEqualTo("dulce et decorum est pro patria mori");
        assertThat(returnedFilm.getReleaseDate()).isEqualTo("2014-01-13");
        assertThat(returnedFilm.getDuration()).isEqualTo(181);
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(4);
    }

    @Test
    @DisplayName("Удаляем фильм без жанра")
    void deleteFilmWithoutGenresTest() {
        assertThat(filmStorage.getFilmsMap().get(1).getId()).isEqualTo(1);
        assertThat(filmStorage.getFilmsMap()).hasSize(1);

        filmStorage.removeFilm(1);

        assertThat(filmStorage.getFilmsMap()).isEmpty();
    }

    @Test
    @DisplayName("Удаляем фильм с жанрами")
    void deleteFilmWithGenresTest() {
        filmStorage.addFilm(movieWithGenres);

        assertThat(filmStorage.getFilmsMap().get(2).getId()).isEqualTo(2);
        assertThat(filmStorage.getFilmsMap()).hasSize(2);

        filmStorage.removeFilm(2);

        assertThat(filmStorage.getFilmsMap()).hasSize(1);
    }


}