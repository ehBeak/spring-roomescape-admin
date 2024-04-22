package roomescape.model;

import java.time.LocalTime;

public class ReservationTime {

    private long id;
    private LocalTime startAt;

    public ReservationTime() {
    }

    public ReservationTime(LocalTime startAt) {
        this.startAt = startAt;
    }

    public ReservationTime(long id, LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalTime startAt) {
        this.startAt = startAt;
    }
}
