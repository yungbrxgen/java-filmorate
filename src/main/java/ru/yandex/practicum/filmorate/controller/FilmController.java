package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET /films - возвращаем все фильмы");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        log.info("POST /films - создание фильма: {}", newFilm.getId());
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films - обновление фильма id={}", film.getId());
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("GET /films/{id} - запроса на получение фильма с ID={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        log.info("PUT /films/{id}/like/{userId} - запрос на добавление лайка фильму {} от пользователя {}",
                id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        log.info("DELETE /films/{id}/like/{userId} - запрос на удаление лайка у фильма {} от пользователя {}",
                id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={} - запрос на получение популярных фильмов", count);
        if (count <= 0) {
            log.warn("Попытка получить отрицательное количество фильмов");
            throw new ValidationsException("Количество популярных фильмов должно быть положительным");
        }
        return filmService.getPopularFilms(count);
    }
}
