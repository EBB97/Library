package com.ebb.library.libraryapi.controllers;

import com.ebb.library.libraryapi.models.daos.UsersEntityDAO;
import com.ebb.library.libraryapi.models.entities.UsersEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api-rest-ebb2223/users")
public class UsersController {
    private final UsersEntityDAO usersEntityDAO;

    public UsersController(UsersEntityDAO usersEntityDAO) {
        this.usersEntityDAO = usersEntityDAO;
    }

    @GetMapping
    public List<UsersEntity> findAllUsers() {
        return (List<UsersEntity>) usersEntityDAO.findAll();
    }
}
