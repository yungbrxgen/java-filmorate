package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmorateApplicationTests {
    private InMemoryUserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private InMemoryFilmStorage filmStorage;
    private FilmService filmService;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();

        userService = new UserService(userStorage);
        userController = new UserController(userService);

        filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);
    }

    private User makeValidUser() {
        User u = new User();
        u.setEmail("test@example.com");
        u.setLogin("login");
        u.setName("Test User");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return u;
    }

    private Film makeValidFilm() {
        Film f = new Film();
        f.setName("My Film");
        f.setDescription("ShortDescription");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(120L);
        return f;
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void createUserSuccessSetsIdAndReturnsUser() {
        User u = makeValidUser();
        User created = userController.create(u);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("Test User", created.getName());
        Collection<User> all = userController.getAll();
        assertEquals(1, all.size());
    }

    @Test
    public void createUserNameNullDefaultsLogin() {
        User u = makeValidUser();
        u.setName(null);
        User created = userController.create(u);

        assertEquals("login", created.getName());
        assertEquals(1L, created.getId());
    }

    @Test
    public void createUserMissingAtInEmailThrowsValidation() {
        User u = makeValidUser();
        u.setEmail("invalid.email.com");

        ValidationsException ex = assertThrows(ValidationsException.class, () -> userController.create(u));
        assertTrue(ex.getMessage().toLowerCase().contains("имейл") || ex.getMessage().toLowerCase().contains("email"));
    }

    @Test
    public void updateUserSuccessUpdateFields() {
        User u = makeValidUser();
        User created = userController.create(u);

        User updated = new User();
        updated.setId(created.getId());
        updated.setEmail("new@example.com");
        updated.setLogin("newlogin");
        updated.setName("New Name");
        updated.setBirthday(LocalDate.of(1995, 5, 5));

        User result = userController.update(updated);

        assertEquals(created.getId(), result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("newlogin", result.getLogin());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    public void updateUserNotFoundThrowsFound() {
        User u = makeValidUser();
        u.setId(999L);

        assertThrows(UserNotFoundException.class, () -> userController.update(u));
    }

    @Test
    public void createFilmSuccessSetsIdAndReturnsFilm() {
        Film f = makeValidFilm();
        Film created = filmController.create(f);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        Collection<Film> all = filmController.getAll();
        assertEquals(1, all.size());
    }

    @Test
    public void createFilmEmptyNameThrowsValidation() {
        Film f = makeValidFilm();
        f.setName("");

        assertThrows(ValidationsException.class, () -> filmController.create(f));
    }

    @Test
    public void createFilmLongDescriptionThrowsValidaion() {
        Film f = makeValidFilm();
        f.setDescription("x".repeat(201));

        assertThrows(ValidationsException.class, () -> filmController.create(f));
    }

    @Test
    public void createFilmToEarlyReleaseThrowsValidation() {
        Film f = makeValidFilm();
        f.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidationsException.class, () -> filmController.create(f));
    }

    @Test
    public void createFilmNegativeDurationThrowsException() {
        Film f = makeValidFilm();
        f.setDuration(-10L);

        assertThrows(ValidationsException.class, () -> filmController.create(f));
    }

    @Test
    public void updateFilmSuccessUpdatesFields() {
        Film f = makeValidFilm();
        Film created = filmController.create(f);

        Film upd = new Film();
        upd.setId(created.getId());
        upd.setName("Updated");
        upd.setDescription("Updated desc");
        upd.setDuration(90L);
        upd.setReleaseDate(LocalDate.of(2010, 2, 2));

        Film result = filmController.update(upd);

        assertEquals(created.getId(), result.getId());
        assertEquals("Updated", result.getName());
        assertEquals(90L, result.getDuration());
    }

    @Test
    public void updateFilmNotFoundThrowsNotFound() {
        Film f = makeValidFilm();
        f.setId(999L);

        assertThrows(FilmNotFoundException.class, () -> filmController.update(f));
    }
}