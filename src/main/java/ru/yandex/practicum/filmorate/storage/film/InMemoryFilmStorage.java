package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId()) || isRepeat(film)) {
            logAndThrowNotFound("Фильм уже занесен в базу");
        }

        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: " + film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            logAndThrowException("Не введено id фильма");
        }
        if (!films.containsKey(film.getId())) {
            logAndThrowNotFound("Фильма нет в базе");
        }

        Film updatedFilm = films.get(film.getId());
        if (!film.getName().isBlank()) {
            updatedFilm.setName(film.getName());
        }
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());
        films.put(film.getId(), updatedFilm);
        log.debug("Обновлен фильм " + updatedFilm);

        return film;
    }

    @Override
    public void removeFilm(Integer id) {
        if (!films.containsKey(id)) {
            logAndThrowNotFound("Нет в базе фильма с id " + id);
        }
        films.remove(id);
        log.debug("Удален фильм " + id);
    }

    @Override
    public Film getFilm(Integer filmId) {
        if (!getFilmsMap().containsKey(filmId)) {
            logAndThrowNotFound("В базе нет фильма с id " + filmId);
        }
        return getFilmsMap().get(filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

        Film film = getFilm(filmId);
        film.getLikes().add(userId);

    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {

        Film film = getFilm(filmId);
        if (!film.getLikes().contains(userId)) {
            logAndThrowNotFound("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        film.getLikes().remove(userId);

    }

    private boolean isRepeat(Film film) {
        if (films.isEmpty()) {
            return false;
        }
        for (Film savedFilm : films.values()) {
            if (savedFilm.getName().equals(film.getName())
                    && (savedFilm.getDescription().equals(film.getDescription()))) {
                return true;
            }
        }
        return false;
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
