package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    public class ErrorResponse {
        private String error;
        private String description;

        public ErrorResponse(String error, String description) {
            this.error = error;
            this.description = description;
        }
    }

    @ExceptionHandler(ValidationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationsException e) {
        log.warn("Validation error: {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации!", e.getMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(Exception e) {
        log.warn("Not found: {}", e.getMessage());
        return new ErrorResponse("Ошибка поиска!", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternal(Exception e) {
        log.error("Необработанное исключение, перехваченное обработчиком", e);
        return new ErrorResponse("Внутрення ошибка сервера", "Произошла непредвиденная ошибка.");
    }
}
