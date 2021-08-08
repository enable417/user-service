package ru.evotor.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evotor.userservice.exception.UserNotFoundException;
import ru.evotor.userservice.model.User;
import ru.evotor.userservice.service.UserService;
import ru.evotor.userservice.wrapper.DateRange;
import ru.evotor.userservice.wrapper.FullName;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    private static final String GETTING_USER_ERROR_MESSAGE = "Error getting users";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GETTING_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping("/find")
    public ResponseEntity getUserById(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GETTING_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping("/find/full-name")
    public ResponseEntity getUsersByFullNameParts(@RequestBody FullName fullName) {
        try {
            return ResponseEntity.ok().body(userService.getUsersByFullNameParts(fullName));
        } catch (UserNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GETTING_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping("find/date-of-birth")
    public ResponseEntity getUsersByDateOfBirth(@RequestBody DateRange dateOfBirthRange) {
        try {
            return ResponseEntity.ok().body(userService.getUsersByDateOfBirthRange(dateOfBirthRange));
        } catch (UserNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GETTING_USER_ERROR_MESSAGE);
        }
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User successfully created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating user");
        }
    }

    @PostMapping("/update")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok("User successfully updated");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(@RequestParam Long id) {
        try {
            return ResponseEntity.ok(userService.deleteUser(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user");
        }
    }
}
