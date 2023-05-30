package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.User;
import com.skwarek.blogger.dto.UserRequest;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long userId);

    User create(UserRequest userRequest);

    User update(Long userId, UserRequest userRequest);

    void deleteById(Long userId);

}
