package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @Getter
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationsException e) {
        log.warn("Ошибка валидации (400): {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class
            , GenreNotFountException.class, MpaNotFountException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.warn("Объект не найден (404): {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternal(final Throwable e) {
        log.error("Необработанное исключение (500): ", e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
