package com.ebookeria.ecommerce.controller;

import com.ebookeria.ecommerce.dto.user.*;
import com.ebookeria.ecommerce.service.cart.CartService;
import com.ebookeria.ecommerce.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;
    private final CartService cartService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }


    @GetMapping(path = "/users")
    public ResponseEntity<UserResponse> findUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value="sortField", defaultValue = "email", required = false) String sortField,
            @RequestParam(value="sortDirection", defaultValue = "asc", required = false) String sortDirection

            ) {
        UserResponse users = userService.findAll(pageNo, pageSize, sortField, sortDirection);
        if (users.content().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }





    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable int id) {
        UserDTO userDTO = userService.findById(id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserCreateDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createdUser =  userService.save(userCreateDTO);
        cartService.createCart(createdUser.id());

        return new ResponseEntity<>(userCreateDTO, HttpStatus.CREATED);

    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PutMapping(path = "/users")
    public ResponseEntity<UserUpdateDTO> updateUser(@Valid @RequestBody UserUpdateDTO userDTO) {
         userService.update(userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    @PutMapping(path = "/users/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        userService.changePassword(userChangePasswordDTO);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }



}