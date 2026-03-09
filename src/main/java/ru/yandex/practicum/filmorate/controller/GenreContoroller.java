package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreContoroller {
    private final GenreService genreService;

    @Autowired
    public GenreContoroller(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> findAll() {
        log.info("Получен запрос на получение всех жанров");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Integer id) {
        log.info("Получен запрос на получение жанра по ID={}", id);
        return genreService.getById(id);
    }
}
