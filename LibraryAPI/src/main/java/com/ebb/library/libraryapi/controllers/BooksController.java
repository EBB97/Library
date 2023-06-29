package com.ebb.library.libraryapi.controllers;

import com.ebb.library.libraryapi.models.daos.BooksEntityDAO;
import com.ebb.library.libraryapi.models.entities.BooksEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api-rest-ebb2223/books")
public class BooksController {
    private final BooksEntityDAO booksEntityDAO;

    public BooksController(BooksEntityDAO booksEntityDAO) {
        this.booksEntityDAO = booksEntityDAO;
    }

    @GetMapping
    public List<BooksEntity> findAllBooks() {
        return (List<BooksEntity>) booksEntityDAO.findAll();
    }
}
