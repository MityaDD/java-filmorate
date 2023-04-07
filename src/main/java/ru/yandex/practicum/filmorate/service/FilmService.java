package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate FIRST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final int MAX_DESCRIPTION_SIZE = 200;

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Map<Integer, Film> getFilmsMap() {
        return filmStorage.getFilmsMap();
    }

    public Film addFilm(Film film) {
        if (isValid(film)) {
            filmStorage.addFilm(film);
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (isValid(film)) {
            return filmStorage.updateFilm(film);
        }
        return film;
    }

    public void removeFilm(Integer id) {
        filmStorage.removeFilm(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            logAndThrowException("ID не может быть отрицательным" + filmId + userId);
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            logAndThrowNotFound("ID не может быть отрицательным" + filmId + userId);
        }
        filmStorage.removeLike(filmId, userId);
    }

    public Film getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        return getFilmsMap().values().stream()
                .sorted(Comparator.comparing(Film::getLikesSize).reversed())
                .limit(count).collect(Collectors.toList());
    }

    private boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            logAndThrowException("Название не должно быть пустым");
        }
        if (film.getDescription().length() == 0 || film.getDescription().length() > MAX_DESCRIPTION_SIZE) {
            logAndThrowException("Описание должно быть не пустым и меньше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(FIRST_DATE)) {
            logAndThrowException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            logAndThrowException("Продолжительность фильма должна быть положительной");
        }
        return true;
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
