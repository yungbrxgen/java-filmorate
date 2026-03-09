package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFountException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        log.debug("Запрос на получние всех рейтингов MPA");
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Integer id) {
        log.debug("Запрос на получение рейтинга MPA о ID={}", id);
        return mpaStorage.getById(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с ID={} не найден", id);
                    return new MpaNotFountException("Рейтинг MPA с ID " + id + " не найден");
                });
    }
}
