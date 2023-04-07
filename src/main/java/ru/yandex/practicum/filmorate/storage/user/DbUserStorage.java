package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Component
@Slf4j
public class DbUserStorage implements UserStorage {

    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String GET_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";


    private final JdbcTemplate jdbcTemplate;
    private final FriendsStorage friendsStorage;

    public DbUserStorage(JdbcTemplate jdbcTemplate, FriendsStorage friendsStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendsStorage = friendsStorage;
    }

    @Override
    public User addUser(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("login","name", "email", "birthday")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of(
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "birthday", java.sql.Date.valueOf(user.getBirthday())))
                .getKeys();
        user.setId((Integer) keys.get("user_id"));
        log.info("Добавлен новый пользователь: id={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!isExisting(user.getId())) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", user.getId()));
        }
        jdbcTemplate.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Обновлен пользователь с id={}", user.getId());
        return user;
    }

    @Override
    public void removeUser(Integer id) {
        if (!isExisting(id)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", id));
        }
        jdbcTemplate.update(DELETE_USER, id);
        log.info("Удален пользователь с id={}", id);
    }


    @Override
    public Map<Integer, User> getUsersMap() {
        return jdbcTemplate.query(GET_USERS, (rs, rowNum) -> renderUser(rs)).stream()
                .collect(Collectors.toMap(u -> u.getId() , u -> u));
    }

    public User getUser(Integer userId) {
        if (!isExisting(userId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", userId));
        }
        return getUsersMap().get(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (!isExisting(userId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", userId));
        }
        if (!isExisting(friendId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", friendId));
        }
        friendsStorage.addFriend(userId, friendId);
        return getUser(friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (!isExisting(userId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", userId));
        }
        if (!isExisting(friendId)) {
            logAndThrowNotFound(String.format("Не найден пользователь с id=%d", friendId));
        }
        friendsStorage.removeFriend(userId, friendId);
    }


    private User renderUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                friendsStorage.findFriends(rs.getInt("user_id"))
        );
    }
    /*
    private Set<Integer> getFriendIdSet(Integer userId) {
        return friendsStorage.getFriends(userId).stream()
                .map(u -> u.getId())
                .collect(Collectors.toSet());
    }

     */



    private boolean isExisting(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_USER_BY_ID, id);
        return userRows.next();
    }

    private void logAndThrowNotFound(String message) {
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }


}
