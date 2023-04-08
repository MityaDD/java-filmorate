package ru.yandex.practicum.filmorate.storage.mpa;

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
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_MPA_NAME = "SELECT name FROM mpa_rating WHERE mpa_id = ?";
    private static final String GET_ALL_MPA = "SELECT * FROM mpa_rating";

    @Override
    public Mpa getMpa(int mpaId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_MPA_NAME, mpaId);
        if (userRows.next()) {
            Mpa mpa = new Mpa(mpaId, userRows.getString("name"));
            log.info("Mpa предоставлен: {}", mpa);
            return mpa;
        } else throw new ObjectNotFoundException(String.format("Mpa не найден: id=%d", mpaId));
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query(GET_ALL_MPA, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

}
