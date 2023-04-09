package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;

    private static final String GET_FILMS = "SELECT * FROM films";
    private static final String GET_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_FILM_GENRE = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_LIKES = "DELETE FROM likes WHERE film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, LikesStorage likesStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.likesStorage = likesStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film addFilm(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("name", "description", "duration", "release_date", "mpa_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of(
                        "name", film.getName(),
                        "description", film.getDescription(),
                        "duration", film.getDuration(),
                        "release_date", java.sql.Date.valueOf(film.getReleaseDate()),
                        "mpa_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("film_id"));
        genreStorage.addGenres(film);
        log.info("Добавлен фильм с id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isExisting(film.getId())) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", film.getId()));
        }
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        genreStorage.removeGenres(film);
        genreStorage.addGenres(film);
        log.info("Обновлен фильм с id={}", film.getId());

        return getFilm(film.getId());
    }

    @Override
    public void removeFilm(Integer filmId) {
        if (!isExisting(filmId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", filmId));
        }
        jdbcTemplate.update(DELETE_FILM_FROM_LIKES, filmId);
        jdbcTemplate.update(DELETE_FILM_FROM_FILM_GENRE, filmId);
        jdbcTemplate.update(DELETE_FILM, filmId);
        log.info("Удаллен фильм с id={}", filmId);
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return jdbcTemplate.query(GET_FILMS, (rs, rowNum) -> renderFilm(rs)).stream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    }

    @Override
    public Film getFilm(Integer filmId) {
        if (!isExisting(filmId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", filmId));
        }
        return getFilmsMap().get(filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        likesStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        likesStorage.removeLike(filmId, userId);
    }

    public Set<Genre> setGenres(int[] genreIds) {
        return Arrays.stream(genreIds)
                .mapToObj(id -> genreStorage.getGenre(id))
                .collect(Collectors.toSet());
    }

    public Mpa setMpa(int mpaId) {
        return mpaStorage.getMpa(mpaId);
    }

    private Film renderFilm(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                genreStorage.getFilmGenres(rs.getInt("film_id")),
                mpaStorage.getMpa(rs.getInt("mpa_id")),
                likesStorage.getFilmLikes(rs.getInt("film_id"))
        );
    }

    private boolean isExisting(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_FILM_BY_ID, id);
        return userRows.next();
    }

    private void logAndThrowNotFound(String message) {
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }


}
