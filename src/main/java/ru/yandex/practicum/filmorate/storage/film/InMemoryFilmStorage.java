package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MINIMUM_DATE = LocalDate.of(1895, 12, 28);

    private Long getNextId() {
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
            log.warn("Валидация: описание слишком длинное: length={}", film.getDescription().length());
            throw new ValidationsException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(MINIMUM_DATE)) {
            log.warn("Валидация: дата релиза раньше минимальной: {}", film.getReleaseDate());
            throw new ValidationsException("Дата релиза не может быть раньше 28-го декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Валидация: отрицательная продолжительность:  {}", film.getDuration());
            throw new ValidationsException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @Override
    public List<Film> getAll() {
        log.debug("InMemoryFilmStorage: Получение всех фильмов. Всего: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film save(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("InMemoryFilmStorage: Добавлен фильм с ID={}. Название: {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("InMemoryFilmStorage: Попытка обновления несуществующего фильма с ID={}", film.getId());
            throw new FilmNotFoundException("Фильм с ID = " + film.getId() + " не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("InMemoryFilmStorage: Обновлен фильм с ID={}. Название: {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Optional<Film> getById(Long id) {
        log.debug("InMemoryFilmStorage: Получение фильма по ID={}", id);
        return Optional.ofNullable(films.get(id));
    }

}
