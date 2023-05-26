package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long userId);

    User create(User userRequest);

    User update(Long userId, User userRequest);

    void deleteById(Long userId);

}
