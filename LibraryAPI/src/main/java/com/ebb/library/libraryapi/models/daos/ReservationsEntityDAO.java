package com.ebb.library.libraryapi.models.daos;

import com.ebb.library.libraryapi.models.entities.ReservationsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * The interface 'Reservations' entity DAO.
 */
public interface ReservationsEntityDAO extends CrudRepository<ReservationsEntity, String> {
    /**
     * Search the latest active 'Reservations' (null return date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param book     the 'Book' ISBN to search by
     * @param borrower the 'User' code to search by
     * @return the optional 'ResponseEntity'
     */
    @Query("SELECT r.id " +
            "FROM ReservationsEntity r " +
            "WHERE r.lendingDate IS NULL " +
            "AND r.book=:book " +
            "AND r.borrower=:borrower " +
            "ORDER BY r.id DESC")
    Optional<Integer> findLatestActiveReservationId(@Param("book") String book, @Param("borrower") String borrower);

    /**
     * Search 'Reservations' by soonest 'Reservations' in database by given 'Book' ISBN.
     *
     * @param book the 'Book' ISBN to search by
     * @return the optional 'Reservations'
     */
    Optional<ReservationsEntity> findFirstByBookAndLendingDateIsNullOrderByIdAsc(@Param("book") String book);

    /**
     * Return if a 'Book' is currently reserved (by given 'Book' ISBN).
     *
     * @param book the 'Book' ISBN to search by
     * @return ''true'' if the book is currently being reserved; ''false'' otherwise
     */
    boolean existsByBookAndLendingDateIsNull(@Param ("book") String book);

    /**
     * Return if a 'User' (by given 'User' code) is currently reserving a 'Book' (by given 'Book' ISBN).
     *
     * @param book     the 'User' code to search by
     * @param borrower the 'Book' ISBN to search by
     * @return ''true'' if the user is currently reserving the book; ''false'' otherwise
     */
    boolean existsByBookAndBorrowerAndLendingDateIsNullOrderByIdDesc(@Param("book") String book, @Param("borrower") String borrower);
}
