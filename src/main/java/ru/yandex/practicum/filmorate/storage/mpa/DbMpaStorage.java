package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

@RequiredArgsConstructor
@Component
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_MPA_NAME = "SELECT name FROM mpa_rating WHERE mpa_id = ?";

    @Override
    public Mpa getMpa(int mpaId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_MPA_NAME, mpaId);
        if (userRows.next()) {
            return new Mpa(mpaId, userRows.getString("name"));
        }
        return null;
    }
}
