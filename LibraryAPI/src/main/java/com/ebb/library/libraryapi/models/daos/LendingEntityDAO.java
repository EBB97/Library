package com.ebb.library.libraryapi.models.daos;

import com.ebb.library.libraryapi.models.entities.LendingEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

/**
 * The interface 'Lending' entity DAO.
 */
public interface LendingEntityDAO extends CrudRepository<LendingEntity, String> {
    /**
     * Search the latest active 'Lending' (null return date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param book     the 'Book' ISBN to search by
     * @param borrower the 'User' code to search by
     * @return the optional 'Lending' id
     */
    @Query("SELECT l.id " +
            "FROM LendingEntity l " +
            "WHERE l.returningDate IS NULL " +
            "AND l.book=:book " +
            "AND l.borrower=:borrower " +
            "ORDER BY l.id DESC")
    Optional<Integer> findLatestActiveLendingId(@Param("book") String book, @Param("borrower") String borrower);

    /**
     * Find 'Lending' lending date by given 'Lending' id.
     *
     * @param id the 'Lending' id to search by
     * @return the optional 'Lending' lending date
     */
    @Query("SELECT l.lendingDate " +
            "FROM LendingEntity l " +
            "WHERE l.id=:id")
    Optional<Date> findLendingLendingDate(@Param("id") int id);

    /**
     * Return if a 'User' (by given 'User' code) is currently borrowing a 'Book' (by given 'Book' ISBN).
     *
     * @param book     the 'User' code to search by
     * @param borrower the 'Book' ISBN to search by
     * @return ''true'' if the user is currently borrowing the book; ''false'' otherwise
     */
    boolean existsByBookAndBorrowerAndReturningDateIsNullOrderByIdDesc(@Param("book") String book, @Param("borrower") String borrower);
}
