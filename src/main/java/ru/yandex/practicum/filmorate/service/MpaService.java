package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@RequiredArgsConstructor
@Component
@Slf4j
public class MpaService {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_FROM_MPA_RATING = "SELECT * FROM MPA_RATING";
    private static final String SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID =
            "SELECT NAME FROM MPA_RATING WHERE MPA_ID = ?";


    public Collection<Mpa> getMpa() {
        return jdbcTemplate.query(SELECT_FROM_MPA_RATING, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

    public Mpa get(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID, id);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    id,
                    userRows.getString("name")
            );
            log.info("Mpa предоставлен: {}", mpa);
            return mpa;
        } else throw new ObjectNotFoundException(String.format("Mpa не найден: id=%d", id));
    }
}

