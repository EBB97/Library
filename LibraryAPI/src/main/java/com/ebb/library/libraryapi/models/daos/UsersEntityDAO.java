package com.ebb.library.libraryapi.models.daos;

import com.ebb.library.libraryapi.models.entities.UsersEntity;
import org.springframework.data.repository.CrudRepository;

public interface UsersEntityDAO extends CrudRepository<UsersEntity, String> {
}
