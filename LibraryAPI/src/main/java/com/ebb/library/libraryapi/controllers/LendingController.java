package com.ebb.library.libraryapi.controllers;

import com.ebb.library.libraryapi.models.daos.LendingEntityDAO;
import com.ebb.library.libraryapi.models.entities.LendingEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The 'Lending' controller.
 */
@RestController
@RequestMapping("/api-rest-ebb2223/lending")
public class LendingController {
    private final LendingEntityDAO lendingEntityDAO;

    /**
     * Instantiates a new 'Lending' controller.
     *
     * @param lendingEntityDAO the 'Lending' entity DAO
     */
    public LendingController(LendingEntityDAO lendingEntityDAO) {
        this.lendingEntityDAO = lendingEntityDAO;
    }

    /**
     * Create 'Lending' in database by given 'Lending' entity
     *
     * @param lendingEntity the 'Lending' to add to database
     * @return the 'Lending' entity
     */
    @PostMapping
    public LendingEntity createLending(@Validated
                                       @RequestBody LendingEntity lendingEntity) {
        return lendingEntityDAO.save(lendingEntity);
    }

    /**
     * Search 'Lending' in database by given 'Lending' id.
     *
     * @param id the 'Lending' id to search by
     * @return the 'ResponseEntity' with the created 'Lending'
     */
    @GetMapping(value = "/{id:^[1-9][0-9]*}")
    public ResponseEntity<LendingEntity> findLendingById(@Validated
                                                         @PathVariable(value = "id") int id) {
        Optional<LendingEntity> optionalLendingEntity = lendingEntityDAO.findById(String.valueOf(id));

        if (optionalLendingEntity.isPresent()) {
            return ResponseEntity.ok().body(optionalLendingEntity.get());
        } else {
            throw new LendingException("Failed to retrieve lending!");
        }
    }

    /**
     * Search the latest active 'Lending' (null return date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param book     the 'Book' ISBN to search by
     * @param borrower the 'User' code to search by
     * @return the 'ResponseEntity' with the 'Lending' id
     */
    @GetMapping("/latest-active-{book}-{borrower}")
    public ResponseEntity<Integer> findLatestActiveLendingId(@Validated
                                                             @PathVariable(value = "book") String book,
                                                             @PathVariable(value = "borrower") String borrower) {
        Optional<Integer> optionalLendingId = lendingEntityDAO.findLatestActiveLendingId(book, borrower);

        if (optionalLendingId.isPresent()) {
            return ResponseEntity.ok().body(optionalLendingId.get());
        } else {
            throw new LendingException("Failed to retrieve latest active lending id!");
        }
    }

    /**
     * Find 'Lending' lending date by given 'Lending' id.
     *
     * @param id the 'Lending' id to search by
     * @return the 'ResponseEntity' with the 'Lending' lending date
     */
    @GetMapping(value = "/{id:^[1-9][0-9]*}/lending-date")
    public ResponseEntity<Date> findLendingLendingDate(@Validated
                                                       @PathVariable(value = "id") int id) {
        Optional<Date> optionalLendingEntity = lendingEntityDAO.findLendingLendingDate(id);

        if (optionalLendingEntity.isPresent()) {
            return ResponseEntity.ok().body(optionalLendingEntity.get());
        } else {
            throw new LendingException("Failed to retrieve lending lending date!");
        }
    }

    /**
     * Get 'List' of all 'Lending' in database.
     *
     * @return the list of 'Lending'
     */
    @GetMapping
    public List<LendingEntity> findAllLending() {
        return (List<LendingEntity>) lendingEntityDAO.findAll();
    }

    /**
     * Update 'Lending' (by given 'Lending' id).
     *
     * @param newLending the new 'Lending' to update the old one with
     * @param id         the 'Lending' id to search by
     * @return the 'ResponseEntity' with the updated 'Lending'
     */
    @PutMapping("/{id:^[1-9][0-9]*}")
    public ResponseEntity<?> updateLending(@Validated
                                           @RequestBody LendingEntity newLending,
                                           @PathVariable(value = "id") int id) {

        Optional<LendingEntity> lending = lendingEntityDAO.findById(String.valueOf(id));

        if (lending.isPresent()) {
            lending.get().setLendingDate(newLending.getLendingDate());
            lending.get().setReturningDate(newLending.getReturningDate());
            lending.get().setBook(newLending.getBook());
            lending.get().setBorrower(newLending.getBorrower());

            lendingEntityDAO.save(lending.get());

            return ResponseEntity.ok().body("Lending updated!");
        } else {
            throw new LendingException("Failed to retrieve lending to update!");
        }
    }

    /**
     * Return if a 'User' (by given 'User' code) is currently borrowing a 'Book' (by given 'Book' ISBN).
     *
     * @param book     the 'Book' ISBN to search by
     * @param borrower the 'User' code to search by
     * @return ''true'' if the user is currently borrowing the book; ''false'' otherwise
     */
    @GetMapping("/active-exists-{book}-{borrower}")
    public boolean isUserLendingBook(@Validated
                                     @PathVariable(value = "book") String book,
                                     @PathVariable(value = "borrower") String borrower) {
        return lendingEntityDAO.existsByBookAndBorrowerAndReturningDateIsNullOrderByIdDesc(book, borrower);
    }

    /**
     * Exception for generic 'Lending' errors.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class LendingException extends RuntimeException {
        /**
         * Instantiates a new 'Lending' exception with a custom message.
         *
         * @param message the message
         */
        public LendingException(String message) {
            super(message);
        }
    }
}
