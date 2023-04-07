package ru.yandex.practicum.filmorate.storage.friends;

import java.util.Set;

public interface FriendsStorage {
    void addFriend(Integer  userId, Integer  friendId);

    void removeFriend(Integer  userId, Integer friendId);

    Set<Integer> findFriends(Integer id);
}

