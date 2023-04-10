package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {

    void addGenres(Film film);

    void removeGenres(Film film);

    Set<Genre> getFilmGenres(Integer filmId);

    Collection<Genre> getAllGenres();

    Genre getGenre(int genreId);
}
