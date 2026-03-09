package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
public class FilmorateApplicationTests {
    private final UserService userService;
    private final FilmService filmService;
    private final GenreService genreService;
    private final MpaService mpaService;

    private User makeValidUser() {
        return User.builder()
                .email("test@example.com")
                .login("login")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    private Film makeValidFilm() {
        return Film.builder()
                .name("My Film")
                .description("ShortDescription")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .mpa(new Mpa(1L, "G"))
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void createUserSuccessSetsIdAndReturnsUser() {
        User u = makeValidUser();
        User created = userService.createUser(u);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test User");
    }

    @Test
    public void createUserNameNullDefaultsLogin() {
        User u = makeValidUser();
        u.setName(null);
        User created = userService.createUser(u);

        assertThat(created.getName()).isEqualTo("login");
    }

    @Test
    public void updateUserSuccessUpdateFields() {
        User created = userService.createUser(makeValidUser());
        User updated = User.builder()
                .id(created.getId())
                .email("new@example.com")
                .login("newlogin")
                .name("New Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User result = userService.updateUser(updated);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getLogin()).isEqualTo("newlogin");
    }

    @Test
    public void updateUserNotFoundThrowsException() {
        User u = makeValidUser();
        u.setId(999L);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(u));
    }

    @Test
    public void createFilmSuccessSetsIdAndReturnsFilm() {
        Film f = makeValidFilm();
        Film created = filmService.createFilm(f);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(filmService.getAllFilms()).hasSize(1);
    }

    @Test
    public void createFilmToEarlyReleaseThrowsValidation() {
        Film f = makeValidFilm();
        f.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidationsException.class, () -> filmService.createFilm(f));
    }

    @Test
    public void updateFilmNotFoundThrowsNotFound() {
        Film f = makeValidFilm();
        f.setId(999L);

        assertThrows(FilmNotFoundException.class, () -> filmService.updateFilm(f));
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaService.getMpaById(1L);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    public void testGetAllMpa() {
        List<Mpa> mpaList = mpaService.getAllMpa();
        assertThat(mpaList).hasSize(5);
        assertThat(mpaList).extracting(Mpa::getName).contains("G", "NC-17");
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreService.getById(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreService.getAll();
        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName).contains("Боевик", "Драма");
    }
}