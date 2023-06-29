package com.ebb.library.libraryapi.controllers;

import com.ebb.library.libraryapi.models.daos.ReservationsEntityDAO;
import com.ebb.library.libraryapi.models.entities.ReservationsEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The 'Reservations' controller.
 */
@RestController
@RequestMapping("/api-rest-ebb2223/reservations")
public class ReservationsController {
    private final ReservationsEntityDAO reservationsEntityDAO;

    /**
     * Instantiates a new 'Reservations' controller.
     *
     * @param reservationsEntityDAO the 'Reservations' entity DAO
     */
    public ReservationsController(ReservationsEntityDAO reservationsEntityDAO) {
        this.reservationsEntityDAO = reservationsEntityDAO;
    }

    /**
     * Create 'Reservations' in database by given 'Reservations' entity
     *
     * @param reservationEntity the 'Reservations' to add to database
     * @return the 'Reservations' entity
     */
    @PostMapping
    public ReservationsEntity createReservation(@Validated
                                                @RequestBody ReservationsEntity reservationEntity) {
        return reservationsEntityDAO.save(reservationEntity);
    }

    /**
     * Search 'Reservations' in database by given 'Reservations' id.
     *
     * @param id the 'Reservations' id to search by
     * @return the 'ResponseEntity' with the created 'Reservations'
     */
    @GetMapping(value = "/{id:^[1-9][0-9]*}")
    public ResponseEntity<ReservationsEntity> findReservationById(@Validated
                                                                  @PathVariable(value = "id") int id) {
        Optional<ReservationsEntity> optionalReservationsEntity = reservationsEntityDAO.findById(String.valueOf(id));

        if (optionalReservationsEntity.isPresent()) {
            return ResponseEntity.ok().body(optionalReservationsEntity.get());
        } else {
            throw new ReservationsController.ReservationException("Failed to retrieve reservation!");
        }
    }

    /**
     * Search the latest active 'Reservations' (null return date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param book     the 'Book' ISBN to search by
     * @param borrower the 'User' code to search by
     * @return the 'ResponseEntity' with the 'Reservations' id
     */
    @GetMapping("/latest-active-{book}-{borrower}")
    public ResponseEntity<Integer> findLatestActiveReservationId(@Validated
                                                                 @PathVariable(value = "book") String book,
                                                                 @PathVariable(value = "borrower") String borrower) {
        Optional<Integer> optionalReservationId = reservationsEntityDAO.findLatestActiveReservationId(book, borrower);

        if (optionalReservationId.isPresent()) {
            return ResponseEntity.ok().body(optionalReservationId.get());
        } else {
            throw new ReservationsController.ReservationException("Failed to retrieve latest active reservation id!");
        }
    }


    /**
     * Search 'Reservations' id in database by given 'Book' ISBN.
     *
     * @param book the 'Book' ISBN to search by
     * @return the 'ResponseEntity' with the ''Reservations' id
     */
    @GetMapping("/{book}/active/id")
    public ResponseEntity<Integer> findNextActiveReservationId(@Validated
                                                         @PathVariable(value = "book") String book) {
        Optional<ReservationsEntity> optionalNextReservation = reservationsEntityDAO.findFirstByBookAndLendingDateIsNullOrderByIdAsc(book);

        if (optionalNextReservation.isPresent()) {
            return ResponseEntity.ok().body(optionalNextReservation.get().getId());
        } else {
            throw new LendingController.LendingException("Failed to retrieve next reservation id!");
        }
    }

    /**
     * Search 'User' code by soonest 'Reservations' in database by given 'Book' ISBN.
     *
     * @param book the 'Book' ISBN to search by
     * @return the 'ResponseEntity' with the 'User' code
     */
    @GetMapping("/{book}/active/borrower")
    public ResponseEntity<String> findNextActiveReservationBorrower(@Validated
                                                              @PathVariable(value = "book") String book) {
        Optional<ReservationsEntity> optionalNextReservation = reservationsEntityDAO.findFirstByBookAndLendingDateIsNullOrderByIdAsc(book);

        if (optionalNextReservation.isPresent()) {
            return ResponseEntity.ok().body(optionalNextReservation.get().getBorrower());
        } else {
            throw new LendingController.LendingException("Failed to retrieve next reservation borrower!");
        }
    }

    /**
     * Get 'List' of all 'Reservations' in database.
     *
     * @return the list of 'Reservations'
     */
    @GetMapping
    public List<ReservationsEntity> findAllReservation() {
        return (List<ReservationsEntity>) reservationsEntityDAO.findAll();
    }

    /**
     * Update 'Reservations' (by given 'Reservations' id).
     *
     * @param newReservation the new 'Reservations' to update the old one with
     * @param id             the 'Reservations' id to search by
     * @return the 'ResponseEntity' with the updated 'Reservations'
     */
    @PutMapping("/{id:^[1-9][0-9]*}")
    public ResponseEntity<?> updateReservation(@Validated
                                               @RequestBody ReservationsEntity newReservation,
                                               @PathVariable(value = "id") int id) {

        Optional<ReservationsEntity> reservation = reservationsEntityDAO.findById(String.valueOf(id));

        if (reservation.isPresent()) {
            reservation.get().setLendingDate(newReservation.getLendingDate());
            reservation.get().setBook(newReservation.getBook());
            reservation.get().setBorrower(newReservation.getBorrower());

            reservationsEntityDAO.save(reservation.get());

            return ResponseEntity.ok().body("Reservation updated!");
        } else {
            throw new ReservationsController.ReservationException("Failed to retrieve reservation to update!");
        }
    }

    /**
     * Return if a 'Book' is currently reserved (by given 'Book' ISBN).
     *
     * @param book     the 'Book' ISBN to search by
     * @return ''true'' if the book is currently being reserved; ''false'' otherwise.
     */
    @GetMapping("/is-{book}-reserved")
    public boolean isBookReserved(@Validated
                                  @PathVariable(value = "book") String book) {
        return reservationsEntityDAO.existsByBookAndLendingDateIsNull(book);
    }

    /**
     * Return if a 'User' (by given 'User' code) is currently reserving a 'Book' (by given 'Book' ISBN).
     *
     * @param book     the 'User' code to search by
     * @param borrower the 'Book' ISBN to search by
     * @return ''true'' if the user is currently reserving the book; ''false'' otherwise.
     */
    @GetMapping("/active-exists-{book}-{borrower}")
    public boolean isUserReservingBook(@Validated
                                       @PathVariable(value = "book") String book,
                                       @PathVariable(value = "borrower") String borrower) {
        return reservationsEntityDAO.existsByBookAndBorrowerAndLendingDateIsNullOrderByIdDesc(book, borrower);
    }

    /**
     * Exception for generic 'Reservations' errors.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class ReservationException extends RuntimeException {
        /**
         * Instantiates a new 'Reservations' exception with a custom message.
         *
         * @param message the message
         */
        public ReservationException(String message) {
            super(message);
        }
    }
}
