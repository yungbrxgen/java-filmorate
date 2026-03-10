package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationsException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        if (film.getMpa() != null && mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            log.warn("Ошибка при поиске MPA рейтинга");
            throw new MpaNotFoundException("MPA рейтинг с ID " + film.getMpa().getId() + " не найден.");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.getById(genre.getId()).isEmpty()) {
                    log.warn("Ошибка при поиске жанра");
                    throw new GenreNotFoundException("Жанр с ID " + genre.getId() + " не найден.");
                }
            }
        }

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
        log.debug("Запрос на получение фильма по ID={}", id);
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
