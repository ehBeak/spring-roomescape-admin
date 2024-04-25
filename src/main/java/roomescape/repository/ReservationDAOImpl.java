package roomescape.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;

@Repository
public class ReservationDAOImpl implements ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;

    public ReservationDAOImpl(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<Reservation> selectAllReservations() {
        String sql = """
                select
                    r.id as reservation_id,
                    r.name,
                    r.date,
                    t.id as time_id,
                    t.start_at as time_start_at
                from reservation as r
                inner join reservation_time as t
                on r.time_id = t.id
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
                new Reservation(
                        resultSet.getLong("reservation_id"),
                        resultSet.getString("name"),
                        resultSet.getDate("date").toLocalDate(),
                        new ReservationTime(
                                resultSet.getLong("time_id"),
                                resultSet.getTime("time_start_at").toLocalTime()
                        )
                ));
    }

    @Override
    public Reservation insertReservation(Reservation reservation) {
        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put("name", reservation.getName());
        parameters.put("date", reservation.getDate());
        parameters.put("time_id", reservation.getTime().getId());
        Number newId = insertActor.executeAndReturnKey(parameters);
        reservation.setId(newId.longValue());
        return reservation;
    }

    @Override
    public long deleteReservation(long id) {
        String sql = "delete from reservation where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}