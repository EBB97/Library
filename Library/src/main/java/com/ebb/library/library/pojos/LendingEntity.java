package com.ebb.library.library.pojos;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "lending", schema = "public", catalog = "Library")
public class LendingEntity {
    private int id;
    private Date lendingDate;
    private Date returningDate;
    private String bookISBN;
    private String borrowerCode;
    private UsersEntity borrower;
    private BooksEntity book;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "lending_date", nullable = false)
    public Date getLendingDate() {
        return lendingDate;
    }

    public void setLendingDate(Date lendingDate) {
        this.lendingDate = lendingDate;
    }

    @Basic
    @Column(name = "returning_date", nullable = true)
    public Date getReturningDate() {
        return returningDate;
    }

    public void setReturningDate(Date returningDate) {
        this.returningDate = returningDate;
    }

    @Basic
    @Column(name = "book", nullable = false, length = 13)
    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    @Basic
    @Column(name = "borrower", nullable = false, length = 13)
    public String getBorrowerCode() {
        return borrowerCode;
    }

    public void setBorrowerCode(String borrowerCode) {
        this.borrowerCode = borrowerCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LendingEntity that = (LendingEntity) o;
        return id == that.id && Objects.equals(lendingDate, that.lendingDate) && Objects.equals(returningDate, that.returningDate) && Objects.equals(bookISBN, that.bookISBN) && Objects.equals(borrowerCode, that.borrowerCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lendingDate, returningDate, bookISBN, borrowerCode);
    }

    @ManyToOne
    @JoinColumn(name = "borrower", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
    public UsersEntity getBorrower() {
        return borrower;
    }

    public void setBorrower(UsersEntity borrower) {
        this.borrower = borrower;
    }

    @ManyToOne
    @JoinColumn(name = "book", referencedColumnName = "isbn", nullable = false, insertable = false, updatable = false)
    public BooksEntity getBook() {
        return book;
    }

    public void setBook(BooksEntity book) {
        this.book = book;
    }
}
