package com.skwarek.blogger.controller;

import com.skwarek.blogger.domain.User;
import com.skwarek.blogger.dto.UserRequest;
import com.skwarek.blogger.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userService.findById(userId);

        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/users/create")
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User createdUser = userService.create(userRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/users")
                .path("/{userId}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping(value = "/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable("userId") Long userId,
                                           @RequestBody UserRequest userRequest) {
        User updatedUser = userService.update(userId, userRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("userId") Long userId) {
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

}
