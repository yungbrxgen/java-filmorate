package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        log.debug("Запрос на получение всех рейтингов MPA");
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Long id) {
        log.debug("Запрос на получение рейтинга MPA о ID={}", id);
        return mpaStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с ID={} не найден", id);
                    return new MpaNotFoundException("Рейтинг MPA с ID " + id + " не найден");
                });
    }
}
