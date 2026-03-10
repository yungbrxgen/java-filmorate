package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getLong("id"), rs.getString("name"));
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    makeGenre(rs), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
