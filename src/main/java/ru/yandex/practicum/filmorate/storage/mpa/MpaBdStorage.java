package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class MpaBdStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getLong("id"), rs.getString("name"));
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> makeMpa(rs), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
