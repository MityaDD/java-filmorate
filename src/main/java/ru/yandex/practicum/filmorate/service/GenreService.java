package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@RequiredArgsConstructor
@Component
@Slf4j
public class GenreService {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_GENRES = "SELECT * FROM genre";
    private static final String GET_GENRE_NAME = "SELECT name FROM genre WHERE genre_id = ?";

    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_GENRES, ((rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        ));
    }

    public Genre getGenre(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_GENRE_NAME, id);
        if (userRows.next()) {
            Genre genre = new Genre(
                    id,
                    userRows.getString("name")
            );
            log.info("Genre found = {} ", genre);
            return genre;
        } else throw new ObjectNotFoundException(String.format("Genre not found: id=%d", id));
    }
}
