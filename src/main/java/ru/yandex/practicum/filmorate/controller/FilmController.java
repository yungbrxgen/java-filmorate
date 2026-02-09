package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MINIMUM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        log.info("GET /films - возвращаем все фильмы, count={}", films.size());
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("Валидация: film == null");
            throw new ValidationsException("Фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация: пустое название фильма");
            throw new ValidationsException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация: описание слишком длинное: lentgth={}", film.getDescription().length());
            throw new ValidationsException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(MINIMUM_DATE)) {
            log.warn("Валидация: дата релиза раньше минимальной: {}", film.getReleaseDate());
            throw new ValidationsException("Дата релиза не может быть раньше 28-го декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.warn("Валидация: отрицательная продолжительность:  {}", film.getDuration());
            throw new ValidationsException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.info("POST /films - создание фильма: {}", newFilm);
        validateFilm(newFilm);
        long id = getNextId();
        newFilm.setId(id);
        films.put(id, newFilm);
        log.info("Создан фильм с id={}", id);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("PUT /films - обновление фильма id={}", newFilm != null ? newFilm.getId() : null);
        validateFilm(newFilm);

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Фильм с id={}", newFilm.getId());
            throw new NotFoundException("Фильм с ID = " + newFilm.getId() + " не найден");
        }

        Film stored = films.get(newFilm.getId());
        stored.setName(newFilm.getName());
        stored.setDescription(newFilm.getDescription());
        stored.setDuration(newFilm.getDuration());
        stored.setReleaseDate(newFilm.getReleaseDate());

        log.info("Фильм с id={} обновлен", newFilm.getId());
        return stored;
    }
}
