package com.ebb.library.libraryapi.models.daos;

import com.ebb.library.libraryapi.models.entities.BooksEntity;
import org.springframework.data.repository.CrudRepository;

public interface BooksEntityDAO extends CrudRepository<BooksEntity, String> {
}