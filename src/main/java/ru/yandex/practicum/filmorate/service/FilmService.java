package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film createFilm(Film film) {
        log.debug("FilmService: Создание фильма");
        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        log.debug("FilmService: Обновление фильма с ID={}", film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        log.debug("FilmService: Получение всех фильмов");
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        log.debug("FilmService: Получение фильма по ID={}", id);
        return filmStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("FilmService: Фильм с ID={} не найден", id);
                    return new FilmNotFoundException("Фильм с ID = " + id + " не найден");
                });
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);

        userService.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            log.warn("FilmService: Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            return;
        }

        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("FilmService: Пользователь {} поставил лайк фильму {}. Всего лайков: {}", userId, filmId, film.getLikes().size());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);

        userService.getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            log.warn("FilmService: Пользователь {} не ставил лайк фильму {}", userId, filmId);
            return;
        }

        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("FilmService: Пользователь {} удалил лайк у фильма {}. Всего лайков: {}", userId, filmId, film.getLikes().size());
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAll();

        List<Film> popularFilms = allFilms.stream()
                .sorted(Comparator.comparingInt((Film film) ->
                        film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

        log.debug("FilmService: Получен список {} популярных фильмов. Всего: {}", popularFilms.size(), allFilms.size());
        return popularFilms;
    }
}
