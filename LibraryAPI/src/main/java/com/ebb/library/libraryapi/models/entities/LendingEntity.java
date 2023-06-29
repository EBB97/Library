package com.ebb.library.libraryapi.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.sql.Date;
import java.util.Objects;

/**
 * The 'Lending' entity.
 */
@Entity
@Table(name = "lending", schema = "public", catalog = "Library")
public class LendingEntity {
    private int id;
    private Date lendingDate;
    private Date returningDate;
    private String book;
    private String borrower;

    /**
     * Gets id.
     *
     * @return the id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    @Valid
    @NotNull(message = "Lending id must not be null.")
    @Digits(integer = 10, fraction = 0, message = "Lending id must have 10 digits or less, and no decimals.")
    @Min(value = 0, message = "Lending id must be greater than 0.")
    @Max(value = Integer.MAX_VALUE, message = "Lending id must not be greater than " + Integer.MAX_VALUE + ".")
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets lending date.
     *
     * @return the lending date
     */
    @Basic
    @Column(name = "lending_date", nullable = false)
    @Valid
    @NotNull(message = "Lending lending date must not be null.")
    public Date getLendingDate() {
        return lendingDate;
    }

    /**
     * Sets lending date.
     *
     * @param lendingDate the lending date
     */
    public void setLendingDate(Date lendingDate) {
        this.lendingDate = lendingDate;
    }

    /**
     * Gets returning date.
     *
     * @return the returning date
     */
    @Basic
    @Column(name = "returning_date", nullable = true)
    @Valid
    public Date getReturningDate() {
        return returningDate;
    }

    /**
     * Sets returning date.
     *
     * @param returningDate the returning date
     */
    public void setReturningDate(Date returningDate) {
        this.returningDate = returningDate;
    }

    /**
     * Gets book.
     *
     * @return the book
     */
    @Basic
    @Column(name = "book", nullable = false, length = 13)
    @Valid
    @NotBlank(message = "Lending book ISBN must not be blank.")
    @Size(min = 13, max = 13, message = "Lending book ISBN length must be 13.")
    public String getBook() {
        return book;
    }

    /**
     * Sets book.
     *
     * @param book the book
     */
    public void setBook(String book) {
        this.book = book;
    }

    /**
     * Gets borrower.
     *
     * @return the borrower
     */
    @Basic
    @Column(name = "borrower", nullable = false, length = 13)
    @Valid
    @NotBlank(message = "Lending borrower code must not be blank.")
    @Size(min = 1, max = 13, message = "Lending borrower code length must be between 1 and 13.")
    public String getBorrower() {
        return borrower;
    }

    /**
     * Sets borrower.
     *
     * @param borrower the borrower
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LendingEntity that = (LendingEntity) o;
        return id == that.id && Objects.equals(lendingDate, that.lendingDate) && Objects.equals(returningDate, that.returningDate) && Objects.equals(book, that.book) && Objects.equals(borrower, that.borrower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lendingDate, returningDate, book, borrower);
    }
}
