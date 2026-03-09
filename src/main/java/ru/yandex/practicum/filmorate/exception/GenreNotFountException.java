package ru.yandex.practicum.filmorate.exception;

public class GenreNotFountException extends RuntimeException {
    public GenreNotFountException(String message) {
        super(message);
    }
}
