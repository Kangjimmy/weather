package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) {
        String sql = " insert into memo values(?, ?) ";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());

        return memo;
    }

    public List<Memo> findAll() {
        String sql = " select id, text from memo ";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id) {
        String sql = " select id, text from memo where id = ? ";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    private RowMapper<Memo> memoRowMapper() {
        // ResultSet 형태로 받아오게 되는데
        // {id = 1, text = 'this is memo'} 이런형식이라 치면
        // 이걸 Memo형식으로 mapper하기 위해 만든다.

        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text"));
    }
}
