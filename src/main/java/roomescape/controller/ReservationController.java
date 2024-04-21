package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.request.ReservationRequest;
import roomescape.dao.ReservationDAO;
import roomescape.model.Reservation;

@RestController
public class ReservationController {

    private final List<Reservation> reservations = new ArrayList<>();
    private final ReservationDAO reservationDAO;

    public ReservationController(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(reservationDAO.findAllReservations());
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationRequest reservationDto) {
        Reservation reservation =
                new Reservation(reservationDto.name(), reservationDto.date(), reservationDto.time());
        long id = reservationDAO.add(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + id)).body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable("id") long id) {
        Optional<Reservation> reservationOptional = reservations.stream()
                .filter(it -> it.getId() == id)
                .findFirst();

        if (reservationOptional.isPresent()) {
            reservations.remove(reservationOptional.get());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
