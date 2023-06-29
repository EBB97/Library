package com.ebb.library.library;

import com.ebb.library.library.pojos.BooksEntity;
import com.ebb.library.library.pojos.LendingEntity;
import com.ebb.library.library.pojos.ReservationsEntity;
import com.ebb.library.library.pojos.UsersEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Library model.
 */
public class LibraryModel {
    /**
     * "queryHeader" is added to the start of every database query.
     */
    String pojoQuery = "FROM com.ebb.library.library.pojos.";

    // SECTION - USER

    /**
     * Create 'User' in database by given 'User' entity.
     *
     * @param user the 'User' to add to database
     * @throws DatabaseException the database exception
     */
    protected void createUser(UsersEntity user) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(user);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception).getMessage());
        }
    }

    /**
     * Search 'User' in database by given 'User' code.
     *
     * @param userCode the 'User' code to search by
     * @return the 'User' entity found
     * @throws DatabaseException the database exception
     */
    protected UsersEntity getUser(String userCode) throws DatabaseException {
        UsersEntity user;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            user = session
                    .createQuery(pojoQuery + "UsersEntity WHERE code=:code",
                                    UsersEntity.class)
                    .setParameter("code", userCode)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return user;
    }

    /**
     * Search 'User' concatenated name and surname in database by given 'User' code.
     *
     * @param userCode the 'User' code to search by
     * @return the 'User' full name found
     * @throws DatabaseException the database exception
     */
    protected String getUserName(String userCode) throws DatabaseException {
        String name;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            name = (String) session
                    .createQuery("SELECT CONCAT(u.name, ' ',  u.surname) " +
                                    pojoQuery + "UsersEntity u " +
                                    "WHERE u.code=:userCode")
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return name;
    }

    /**
     * Update 'User' in database by given 'User' entity.
     *
     * @param editedUser the 'User' to update in database
     * @throws DatabaseException the database exception
     */
    protected void updateUser(UsersEntity editedUser) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.update(editedUser);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    protected void deleteUser(String userCode) throws DatabaseException {
        UsersEntity user;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            user = session
                    .createQuery(pojoQuery + "UsersEntity u WHERE u.code=:userCode",
                            UsersEntity.class)
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();

            Transaction transaction;

            Query<LendingEntity> lendingEntities =
                    session.createQuery(pojoQuery +
                    "LendingEntity l WHERE l.borrowerCode=:userCode")
                    .setParameter("userCode", userCode);

            List<LendingEntity> lendings = lendingEntities.list();

            for (LendingEntity lending : lendings) {
                transaction = session.beginTransaction();

                session.delete(lending);

                transaction.commit();
            }
            Query<ReservationsEntity> reservationsEntities =
                    session.createQuery(pojoQuery +
                    "ReservationsEntity r WHERE r.reserveeCode=:userCode")
                    .setParameter("userCode", userCode);

            List<ReservationsEntity> reservations = reservationsEntities.list();

            for (ReservationsEntity reservation : reservations) {
                transaction = session.beginTransaction();

                session.delete(reservation);

                transaction.commit();
            }

            transaction = session.beginTransaction();

            session.delete(user);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * Adds the date a month from now to 'User' fined attribute in database by given 'User' code.
     *
     * @param userCode the 'User' code to search by
     * @throws DatabaseException the database exception
     */
    protected void fineUser(String userCode) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            UsersEntity user = getUser(userCode);
            user.setFined(Date.valueOf(LocalDate.now().plusMonths(1)));

            session.update(user);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    // SECTION - BOOK

    /**
     * Create 'Book' in database by given 'Book' entity.
     *
     * @param book the 'Book' to add to database
     * @throws DatabaseException the database exception
     */
    protected void createBook(BooksEntity book) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(book);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * Search 'Book' in database by given 'Book' ISBN.
     *
     * @param bookISBN the 'Book' ISBN to search by
     * @return the 'Book' entity found
     * @throws DatabaseException the database exception
     */
    protected BooksEntity getBook(String bookISBN) throws DatabaseException {
        BooksEntity book;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            book = session
                    .createQuery(pojoQuery + "BooksEntity WHERE isbn=:bookISBN",
                                    BooksEntity.class)
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return book;
    }

    /**
     * Search 'Book' title in database by given 'Book' ISBN.
     *
     * @param bookISBN the 'Book' ISBN to search by
     * @return the 'Book' title found
     * @throws DatabaseException the database exception
     */
    protected String getBookTitle(String bookISBN) throws DatabaseException {
        String title;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            title = (String) session
                    .createQuery("SELECT b.title " +
                                    pojoQuery + "BooksEntity b " +
                                    "WHERE b.isbn=:bookISBN")
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return title;
    }

    /**
     * Update 'Book' in database by given 'Book' entity.
     *
     * @param book the 'Book' to update in database
     * @throws DatabaseException the database exception
     */
    protected void updateBook(BooksEntity book) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.update(book);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    protected void deleteBook(String bookISBN) throws DatabaseException {
        BooksEntity book;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            book = session
                    .createQuery(pojoQuery + "BooksEntity WHERE isbn=:isbn",
                            BooksEntity.class)
                    .setParameter("isbn", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();

            Transaction transaction = session.beginTransaction();

            session.delete(book);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    // SECTION - LENDING

    /**
     * Create 'Lending' in database by given 'Lending' entity.
     *
     * @param lending the 'Lending' to add to database
     * @throws DatabaseException the database exception
     */
    protected void createLending(LendingEntity lending) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(lending);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * Search 'Lending' in database by given 'Lending' id.
     *
     * @param id the 'Lending' id to search by
     * @return the latest active 'Lending'
     * @throws DatabaseException the database exception
     */
    protected LendingEntity getLending(int id) throws DatabaseException {
        LendingEntity lending;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            lending = session
                    .createQuery(pojoQuery + "LendingEntity l WHERE l.id=:id",
                                    LendingEntity.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return lending;
    }

    /**
     * Search the latest active 'Lending' (null return date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param userCode the 'User' code to search by
     * @param bookISBN the 'Book' ISBN to search by
     * @return the latest active 'Lending' id
     * @throws DatabaseException the database exception
     */
    protected int getLatestActiveLendingId(String userCode, String bookISBN) throws DatabaseException {
        int id;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult = session
                    .createQuery("SELECT l.id " +
                                    pojoQuery + "LendingEntity l " +
                                    "WHERE l.returningDate IS NULL " +
                                        "AND l.bookISBN=:bookISBN " +
                                        "AND l.borrowerCode=:userCode " +
                                    "ORDER BY l.id DESC")
                    .setParameter("bookISBN", bookISBN)
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                throw new DatabaseException("Lending id not found in database!");
            }

            id = (int) queryResult;
        } catch (Exception exception) {
            throw new DatabaseException(exception.getMessage(), findRootCause(exception));
        }

        return id;
    }

    /**
     * Find 'Lending' lending date by given 'Lending' id.
     *
     * @param id the 'Lending' id to search by
     * @return the 'Lending' lending date
     * @throws DatabaseException the database exception
     */
    protected LocalDate getLendingLendingDate(int id) throws DatabaseException {
        LocalDate localDate;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult = session
                    .createQuery("SELECT l.lendingDate " +
                                    pojoQuery + "LendingEntity l " +
                                    "WHERE l.id=:id")
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                throw new DatabaseException("Lending lending date not found in database!");
            }

            localDate = Date.valueOf(queryResult.toString()).toLocalDate();
        } catch (Exception exception) {
            throw new DatabaseException(exception.getMessage(), findRootCause(exception));
        }

        return localDate;
    }

    /**
     * Update 'Lending' (by given 'Lending' id) returning date with given date.
     *
     * @param id            the 'Lending' id to search by
     * @param returningDate the new 'Lending' returning date
     * @throws DatabaseException the database exception
     */
    protected void updateLendingReturningDate(int id, LocalDate returningDate) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            LendingEntity lending = getLending(id);

            if (lending == null) {
                throw new DatabaseException("Failed to update lending returning date in database!");

            }

            if (returningDate == null) {
                lending.setReturningDate(null);
            } else {
                lending.setReturningDate(Date.valueOf(returningDate));
            }

            Transaction transaction = session.beginTransaction();

            session.update(lending);

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new DatabaseException(findRootCause(exception));
        }
    }

    protected boolean isUserLending(String userCode) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult = session
                    .createQuery("SELECT l.borrowerCode " +
                            pojoQuery + "LendingEntity l " +
                            "WHERE l.borrowerCode=:borrowerCode " +
                            "AND l.returningDate IS NULL " +
                            "ORDER BY l.id DESC")
                    .setParameter("borrowerCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                return false;
            }
        } catch (Exception exception) {
            throw new DatabaseException(exception.getMessage(), findRootCause(exception));
        }

        return true;
    }

    protected boolean isBookLent(String bookISBN) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult = session
                    .createQuery("SELECT l.bookISBN " +
                            pojoQuery + "LendingEntity l " +
                            "WHERE l.bookISBN=:bookISBN " +
                            "AND l.returningDate IS NULL " +
                            "ORDER BY l.id DESC")
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                return false;
            }
        } catch (Exception exception) {
            throw new DatabaseException(exception.getMessage(), findRootCause(exception));
        }

        return true;
    }

    /**
     * Return if a 'User' (by given 'User' code) is currently borrowing a 'Book' (by given 'Book' ISBN).
     *
     * @param userCode the 'User' code to search by
     * @param bookISBN the 'Book' ISBN to search by
     * @return ''true'' if the user is currently borrowing the book; ''false'' otherwise
     * @throws DatabaseException the database exception
     */
    protected boolean isUserLendingBook(String userCode, String bookISBN) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT l.id " +
                                     pojoQuery + "LendingEntity l " +
                                    "WHERE l.returningDate IS NULL " +
                                        "AND l.bookISBN=:bookISBN " +
                                        "AND l.borrowerCode=:userCode " +
                                    "ORDER BY l.id DESC",
                                    LendingEntity.class)
                    .setParameter("bookISBN", bookISBN)
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult() != null;
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    // SECTION - RESERVATION

    /**
     * Create 'Reservations' in database by given 'User' code and 'Book' ISBN.
     *
     * @param reservation the 'Reservations' to add to database
     * @throws DatabaseException the database exception
     */
    protected void createReservation(ReservationsEntity reservation) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(reservation);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * Search 'Reservations' in database by given 'Reservations' id.
     *
     * @param id the 'Reservations' id to search by
     * @return the latest active 'Reservations'
     * @throws DatabaseException the database exception
     */
    protected ReservationsEntity getReservation(int id) throws DatabaseException {
        ReservationsEntity reservation;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            reservation = session
                    .createQuery(pojoQuery + "ReservationsEntity r WHERE r.id=:id ",
                                    ReservationsEntity.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return reservation;
    }

    /**
     * Search the latest active 'Reservations' (null lending date) id in database by given 'User' code and 'Book' ISBN.
     *
     * @param userCode the 'User' code to search by
     * @param bookISBN the 'Book' ISBN to search by
     * @return the latest active 'Reservations' id
     * @throws DatabaseException the database exception
     */
    protected int getLatestActiveReservationId(String userCode, String bookISBN) throws DatabaseException {
        int id;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult =  session
                    .createQuery("SELECT r.id " +
                                    pojoQuery + "ReservationsEntity r " +
                                    "WHERE r.lendingDate IS NULL " +
                                        "AND r.bookISBN=:bookISBN " +
                                        "AND r.reserveeCode=:userCode " +
                                    "ORDER BY r.id DESC")
                    .setParameter("bookISBN", bookISBN)
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                throw new DatabaseException("Reservation id not found in database!");
            }

            id = (int) queryResult;
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return id;
    }

    /**
     * Search 'Reservations' id by next (oldest) 'Reservations' in database by given 'Book' ISBN.
     *
     * @param bookISBN the 'Book' ISBN to search by
     * @return the 'Reservations' id
     * @throws DatabaseException the database exception
     */
    protected int getNextActiveReservationId(String bookISBN) throws DatabaseException {
        int id;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult =  session
                    .createQuery("SELECT r.id " +
                            pojoQuery + "ReservationsEntity r " +
                            "WHERE r.lendingDate IS NULL " +
                            "AND r.bookISBN=:bookISBN " +
                            "ORDER BY r.id ASC")
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                throw new DatabaseException("Reservation id not found in database!");
            }

            id = (int) queryResult;
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return id;
    }

    /**
     * Search 'User' full name by next (oldest) 'Reservations' in database by given 'Book' ISBN.
     *
     * @param bookISBN the 'Book' ISBN to search by
     * @return the 'User' full name
     * @throws DatabaseException the database exception
     */
    protected String getNextActiveReservationUserName(String bookISBN) throws DatabaseException {
        String userName;

        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            userName = (String) session
                    .createQuery("SELECT CONCAT(r.reservee.name, ' ', r.reservee.surname) " +
                                    pojoQuery + "ReservationsEntity r " +
                                    "WHERE r.lendingDate IS NULL " +
                                        "AND r.bookISBN=:bookISBN " +
                                    "ORDER BY r.id ASC")
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }

        return userName;
    }

    /**
     * Update 'Reservations' (by given 'Reservations' id) lending date with given date.
     *
     * @param id          the 'Reservations' id to search by
     * @param lendingDate the new 'Reservations' lending date
     * @throws DatabaseException the database exception
     */
    protected void updateReservationLendingDate(int id, LocalDate lendingDate) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            ReservationsEntity reservation = getReservation(id);

            if (reservation == null) {
                throw new DatabaseException("Failed to update reservation lending date in database!");
            }

            if (lendingDate == null) {
                reservation.setLendingDate(null);
            } else {
                reservation.setLendingDate(Date.valueOf(lendingDate));
            }

            Transaction transaction = session.beginTransaction();

            session.update(reservation);

            transaction.commit();
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * Return if a 'Book' is currently reserved (by given 'Book' ISBN).
     *
     * @param bookISBN the 'Book' ISBN to search by
     * @return ''true'' if the book is currently being reserved; ''false'' otherwise
     * @throws DatabaseException the database exception
     */
    protected boolean isBookReserved(String bookISBN) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT r.bookISBN " +
                                    pojoQuery + "ReservationsEntity r " +
                                    "WHERE r.lendingDate IS NULL " +
                                        "AND r.bookISBN=:bookISBN " +
                                    "ORDER BY r.id DESC",
                            ReservationsEntity.class)
                    .setParameter("bookISBN", bookISBN)
                    .setMaxResults(1)
                    .uniqueResult() != null;
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    protected boolean isUserReserving(String userCode) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            Object queryResult = session
                    .createQuery("SELECT r.reserveeCode " +
                            pojoQuery + "ReservationsEntity r " +
                            "WHERE r.reserveeCode=:reserveeCode " +
                            "AND r.lendingDate IS NULL " +
                            "ORDER BY r.id DESC")
                    .setParameter("reserveeCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult();

            if (queryResult == null) {
                return false;
            }
        } catch (Exception exception) {
            throw new DatabaseException(exception.getMessage(), findRootCause(exception));
        }

        return true;
    }

    /**
     * Return if a 'User' (by given 'User' code) is currently reserving a 'Book' (by given 'Book' ISBN).
     *
     * @param userCode the 'User' code to search by
     * @param bookISBN the 'Book' ISBN to search by
     * @return ''true'' if the user is currently reserving the book; ''false'' otherwise
     * @throws DatabaseException the database exception
     */
    protected boolean isUserReservingBook(String userCode, String bookISBN) throws DatabaseException {
        try (Session session = LibraryApplication.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT r.id " +
                                    pojoQuery + "ReservationsEntity r " +
                                    "WHERE r.lendingDate IS NULL " +
                                        "AND r.bookISBN=:bookISBN " +
                                        "AND r.reserveeCode=:userCode " +
                                    "ORDER BY r.id DESC",
                            ReservationsEntity.class)
                    .setParameter("bookISBN", bookISBN)
                    .setParameter("userCode", userCode)
                    .setMaxResults(1)
                    .uniqueResult() != null;
        } catch (Exception exception) {
            throw new DatabaseException(findRootCause(exception));
        }
    }

    /**
     * This method iterates through 'Throwable' objects until it finds the one at the bottom of the stack,
     * which it then returns.
     *
     * @param throwable the throwable
     * @return the throwable
     */
    protected Throwable findRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        while (throwable.getCause() != null && throwable.getCause() != throwable) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    /**
     * Exception for generic database errors.
     */
    protected static class DatabaseException extends Exception {
        /**
         * Instantiates a new Database exception.
         *
         * @param message the message
         * @param cause   the cause
         */
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Instantiates a new Database exception.
         *
         * @param cause the cause
         */
        public DatabaseException(Throwable cause) {
            super(cause);
        }

        /**
         * Instantiates a new Database exception.
         *
         * @param message the message
         */
        public DatabaseException(String message) {
            super(message);
        }
    }
}

