package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Map<Integer, Film> allFilms() {
        return filmStorage.allFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(Integer id) {
        filmStorage.removeFilm(id);
    }

    public Film addLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            logAndThrowException("ID не может быть отрицательным" + filmId + userId);
        }
        Film film = getFilm(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            logAndThrowNotFound("ID не может быть отрицательным" + filmId + userId);
        }
        Film film = getFilm(filmId);
        if (!film.getLikes().contains(userId)) {
            logAndThrowNotFound("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.allFilms().values().stream()
                .sorted(Comparator.comparing(Film::getLikesSize).reversed())
                .limit(count).collect(Collectors.toList());
    }

    public Film getFilm(Integer filmId) {
        if (!filmStorage.allFilms().containsKey(filmId)) {
            logAndThrowNotFound("В базе нет фильма с id " + filmId);
        }
        return filmStorage.allFilms().get(filmId);
    }

    private void logAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }

    private void logAndThrowNotFound(String message) {
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }


}
