package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DbLikesStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_FILMS = "SELECT * FROM films";
    private static final String GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String ADD_LIKE = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String GET_TOP_FILMS = "SELECT FILMS.film_id, name, description, release_date, duration," +
            "COUNT(L.USER_ID) as rating " +
            "FROM films " +
            "LEFT JOIN likes AS l ON films.film_id = l.film_id " +
            "GROUP BY films.film_id " +
            "ORDER BY rating DESC " +
            "LIMIT ?";

    public void addLike(Integer filmId, Integer userId) {
        // isExisting??
        jdbcTemplate.update(ADD_LIKE, userId, filmId);
        log.info("Пользователь {} поставил like к фильму {}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        // isExisting??
        jdbcTemplate.update(REMOVE_LIKE, userId, filmId);
        log.info("Пользователь {} удалил свой like к фильму {}", userId, filmId);

    }

    public Collection<Film> getPopularFilms(Integer count) {
        Collection<Film> films = jdbcTemplate.query(GET_TOP_FILMS, (rs, rowNum) -> renderFilm(rs), count);
        if (films.isEmpty()) {
            return jdbcTemplate.query(GET_FILMS, (rs, rowNum) -> renderFilm(rs), count);
        }
        return films;
    }

    public Set<Integer> getFilmLikes(Integer filmId) {
        return new HashSet<Integer>(jdbcTemplate.query(GET_LIKES, (rs, rowNum) ->
                (rs.getInt("user_id")), filmId));

    }

    private Film renderFilm(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration")
        );
    }


}
