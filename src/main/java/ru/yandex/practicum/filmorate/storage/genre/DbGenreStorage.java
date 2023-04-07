package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;

@RequiredArgsConstructor
@Component
public class DbGenreStorage implements GenreStorage {

    private static final String ADD_GENRE_TO_FILM = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_TO_FILM = "DELETE FROM film_genre WHERE film_id = ?";

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
                jdbcTemplate.update( ADD_GENRE_TO_FILM, film.getId(), genre.getId() );
            }
        }
    }

    @Override
    public void removeGenres(Film film) {
        jdbcTemplate.update( DELETE_GENRES_TO_FILM, film.getId());
    }

    @Override
    public HashSet<Genre> getFilmGenres(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(GET_GENRES_OF_FILM, (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")),
                   filmId
        ));
    }


}
