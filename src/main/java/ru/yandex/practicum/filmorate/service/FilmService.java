package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationsException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        log.info("Добавление нового фильма: {}", film.getName());
        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        log.info("Обновление фильма с ID={}", film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        log.debug("Запрос на получение списка всех фильмов");
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        log.debug("Запрос на получние фильма по ID={}", id);
        return filmStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Фильм с ID={} не найден", id);
                    return new FilmNotFoundException("Фильм с ID " + id + " не найден");
                });
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос на получение ТОП-{} популярных фильмов", count);
        return filmStorage.getPopular(count);
    }
}
