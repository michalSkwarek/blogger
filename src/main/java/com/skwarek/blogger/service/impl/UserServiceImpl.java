package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.domain.User;
import com.skwarek.blogger.dto.UserRequest;
import com.skwarek.blogger.exception.DuplicateUserException;
import com.skwarek.blogger.exception.NotFoundUserException;
import com.skwarek.blogger.repository.UserRepository;
import com.skwarek.blogger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found user with id: " + userId));
    }

    @Override
    public User create(UserRequest userRequest) {
        boolean isUserExist = userRepository.existsByEmail(userRequest.getEmail());

        if (!isUserExist) {
            User newUser = User.builder()
                    .email(userRequest.getEmail())
                    .password(userRequest.getPassword())
                    .build();

            return userRepository.save(newUser);
        } else {
            throw new DuplicateUserException("Duplicate user with email: " + userRequest.getEmail());
        }
    }

    @Override
    public User update(Long userId, UserRequest userRequest) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found user with id: " + userId));

        boolean isUserExist = userRepository.existsByEmail(userRequest.getEmail());

        if (oldUser.getEmail().equals(userRequest.getEmail()) || !isUserExist) {
            oldUser.setEmail(userRequest.getEmail());
            oldUser.setPassword(userRequest.getPassword());

            return userRepository.save(oldUser);
        } else {
            throw new DuplicateUserException("Duplicate user with name: " + userRequest.getEmail());
        }
    }

    @Override
    public void deleteById(Long userId) {
        User userDb = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found user with id: " + userId));

        List<Post> posts = new ArrayList<>(userDb.getPosts());
        posts.forEach(userDb::removePost);

        userRepository.deleteById(userDb.getId());
    }

}
