package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping({"/films"})
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private static int id = 1;
    private static final LocalDate FIRST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId()) || isRepeat(film)) {
            logAndThrowException("Фильм уже занесен в базу");
        }
        if (isValid(film)) {
            film.setId(id++);
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: " + film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            logAndThrowException("Не введено id фильма");
        }
        if (!films.containsKey(film.getId())) {
            logAndThrowException("Фильма нет в базе");
        }
        if (isValid(film)) {
            Film updatedFilm = films.get(film.getId());
            if (!film.getName().isBlank()) {
                updatedFilm.setName(film.getName());
            }
            updatedFilm.setDescription(film.getDescription());
            updatedFilm.setReleaseDate(film.getReleaseDate());
            updatedFilm.setDuration(film.getDuration());
            films.put(film.getId(), updatedFilm);
            log.debug("Обновлен фильм " + updatedFilm);
        }
        return film;
    }

    private boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            logAndThrowException("Название не должно быть пустым");
        }
        if (film.getDescription().length() == 0 || film.getDescription().length() > 200) {
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


}
