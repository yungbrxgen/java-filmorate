package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;
    private Long duration;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Set<Long> likes = new HashSet<>();

    @NotNull(message = "Рейтинг MPA должен быть указан")
    private Mpa mpa;

    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
}
