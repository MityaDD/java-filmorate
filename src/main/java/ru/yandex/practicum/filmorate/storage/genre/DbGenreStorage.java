package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DbGenreStorage implements GenreStorage {

    private static final String ADD_GENRE_TO_FILM = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_TO_FILM = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_GENRES = "SELECT * FROM genre";
    private static final String GET_GENRE_NAME = "SELECT name FROM genre WHERE genre_id = ?";
    private static final String GET_GENRES_OF_FILM =
            "SELECT genre.genre_id, " +
                    "name " +
                    "FROM film_genre\n" +
                    "JOIN genre ON film_genre.genre_id = genre.genre_id\n" +
                    "WHERE film_id = ?\n" +
                    "ORDER BY genre.genre_id";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(ADD_GENRE_TO_FILM, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void removeGenres(Film film) {
        jdbcTemplate.update(DELETE_GENRES_TO_FILM, film.getId());
    }

    @Override
    public Set<Genre> getFilmGenres(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(GET_GENRES_OF_FILM, (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")),
                filmId
        ));
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_GENRES, ((rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        ));
    }

    @Override
    public Genre getGenre(int genreId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_GENRE_NAME, genreId);
        if (userRows.next()) {
            Genre genre = new Genre(genreId, userRows.getString("name"));
            log.info("Для id={} найден жанр {} ", genreId, genre);
            return genre;
        } else throw new ObjectNotFoundException(String.format("Жанр не найден для id=%d", genreId));
    }


}
