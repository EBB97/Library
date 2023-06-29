package com.ebb.library.library.pojos;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "books", schema = "public", catalog = "Library")
public class BooksEntity {
    private String isbn;
    private String title;
    private Integer copies;
    private String cover;
    private String outline;
    private String publisher;
    private List<LendingEntity> borrowedBy;
    private List<ReservationsEntity> reservedBy;

    @Id
    @Column(name = "isbn", nullable = false, length = 13)
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Basic
    @Column(name = "title", nullable = false, length = 90)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "copies", nullable = true)
    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    @Basic
    @Column(name = "cover", nullable = true, length = 255)
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Basic
    @Column(name = "outline", nullable = false, length = 255)
    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    @Basic
    @Column(name = "publisher", nullable = true, length = 60)
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksEntity that = (BooksEntity) o;
        return Objects.equals(isbn, that.isbn) && Objects.equals(title, that.title) && Objects.equals(copies, that.copies) && Objects.equals(cover, that.cover) && Objects.equals(outline, that.outline) && Objects.equals(publisher, that.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, copies, cover, outline, publisher);
    }

    @OneToMany(mappedBy = "book")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<LendingEntity> getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(List<LendingEntity> borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    @OneToMany(mappedBy = "book")
    public List<ReservationsEntity> getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(List<ReservationsEntity> reservedBy) {
        this.reservedBy = reservedBy;
    }
}
