package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilmsMap();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void removeFilm(Integer id);

    Film getFilm(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
