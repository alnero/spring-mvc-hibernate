package com.example.project.dao;

import com.example.project.model.User;

import java.util.List;

public interface UserDao {
    List<User> listUsers();
    User getById(long id);
    void add(User user);
    void edit(User user);
    void delete(User user);
}