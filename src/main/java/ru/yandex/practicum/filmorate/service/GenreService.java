package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        log.debug("Получение всех жанров");
        return genreStorage.getAll();
    }

    public Genre getById(Long id) {
        log.debug("Получение жанра в ID={}", id);
        return genreStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Жанр с ID={} не найден", id);
                    return new GenreNotFoundException("Жанр не найден");
                });
    }
}
