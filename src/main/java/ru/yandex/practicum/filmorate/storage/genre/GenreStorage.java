package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;

public interface GenreStorage {

    void addGenres(Film film);

    void removeGenres(Film film);

    HashSet<Genre> getFilmGenres(Integer filmId);

    Collection<Genre> getAllGenres();

    Genre getGenre(int genreId);
}
