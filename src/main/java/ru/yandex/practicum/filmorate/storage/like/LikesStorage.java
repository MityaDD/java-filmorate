package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface LikesStorage {

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer filmId, Integer userId);

    Collection<Film> getPopularFilms(Integer count);

    Set<Integer> getFilmLikes(Integer filmId);
}
