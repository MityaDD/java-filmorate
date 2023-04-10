package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DbFriendsStorage implements FriendsStorage {

    private static final String ADD_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS = "SELECT friend_id FROM friends WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        log.info("Пользователь с id={} запросил дружбу с пользователем id={}", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(REMOVE_FRIEND, userId, friendId);
        log.info("Пользователь с id={} удалил из френдов id={}", userId, friendId);
    }

    @Override
    public Set<Integer> findFriends(Integer userId) {
        return new HashSet<Integer>(jdbcTemplate.query(GET_FRIENDS, (rs, rowNum) ->
                (rs.getInt("friend_id")), userId));

    }


}
