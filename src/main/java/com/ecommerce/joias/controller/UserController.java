package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.CreateUserDto;
import com.ecommerce.joias.entity.User;
import com.ecommerce.joias.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UUID> createUser(@RequestBody @Valid CreateUserDto createUserDto){
        UUID userId = userService.createUser(createUserDto);

        return ResponseEntity.created(URI.create("/users" + userId.toString())).body(userId);
    }
}
