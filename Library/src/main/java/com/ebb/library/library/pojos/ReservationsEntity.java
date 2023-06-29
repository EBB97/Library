package com.ebb.library.library.pojos;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "reservations", schema = "public", catalog = "Library")
public class ReservationsEntity {
    private int id;
    private Date lendingDate;
    private String bookISBN;
    private String reserveeCode;
    private UsersEntity reservee;
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
    @Column(name = "lending_date", nullable = true)
    public Date getLendingDate() {
        return lendingDate;
    }

    public void setLendingDate(Date lendingDate) {
        this.lendingDate = lendingDate;
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
    public String getReserveeCode() {
        return reserveeCode;
    }

    public void setReserveeCode(String reserveeCode) {
        this.reserveeCode = reserveeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationsEntity that = (ReservationsEntity) o;
        return id == that.id && Objects.equals(lendingDate, that.lendingDate) && Objects.equals(bookISBN, that.bookISBN) && Objects.equals(reserveeCode, that.reserveeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lendingDate, bookISBN, reserveeCode);
    }

    @ManyToOne
    @JoinColumn(name = "borrower", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
    public UsersEntity getReservee() {
        return reservee;
    }

    public void setReservee(UsersEntity reservee) {
        this.reservee = reservee;
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
