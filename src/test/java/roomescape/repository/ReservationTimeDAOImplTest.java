package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.model.ReservationTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationTimeDAOImplTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Autowired
    ReservationTimeDAOImpl reservationTimeDAOImpl;

    SimpleJdbcInsert insertActor;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("drop table reservation if exists");
        jdbcTemplate.execute("drop table reservation_time if exists");
        jdbcTemplate.execute("""
                create table reservation_time
                (
                    id   bigint       not null AUTO_INCREMENT,
                    start_at varchar(255) not null,
                    primary key (id)
                );
                """
        );
        jdbcTemplate.execute("""
                create table reservation
                (
                    id   bigint       not null AUTO_INCREMENT,
                    name varchar(255) not null ,
                    date varchar(255) not null ,
                    time_id bigint,
                    primary key (id),
                    foreign key (time_id) references reservation_time (id)
                );
                """
        );

        insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation_time")
                .usingGeneratedKeyColumns("id");
        insertToReservationTime("10:00");
        insertToReservationTime("11:00");
    }

    private void insertToReservationTime(String startAt) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("start_at", startAt);
        insertActor.execute(parameters);
    }

    @DisplayName("모든 예약 시간을 조회한다")
    @Test
    void should_get_reservation_times() {
        List<ReservationTime> reservationTimes = reservationTimeDAOImpl.selectAllReservationTimes();
        assertThat(reservationTimes).hasSize(2);
    }

    @DisplayName("예약 시간을 추가한다")
    @Test
    void should_add_reservation_time() {
        reservationTimeDAOImpl.insertReservationTime(new ReservationTime(LocalTime.of(12, 0)));
        Integer count = jdbcTemplate.queryForObject("select count(1) from reservation_time", Integer.class);
        assertThat(count).isEqualTo(3);
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_delete_reservation_time() {
        reservationTimeDAOImpl.deleteReservationTime(1);
        Integer count = jdbcTemplate.queryForObject("select count(1) from reservation_time", Integer.class);
        assertThat(count).isEqualTo(1);
    }
}