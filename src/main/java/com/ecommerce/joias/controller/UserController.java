package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateUserDto;
import com.ecommerce.joias.dto.update.UpdateUserDto;
import com.ecommerce.joias.entity.User;
import com.ecommerce.joias.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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

        return ResponseEntity.created(URI.create("/users/" + userId.toString())).body(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") String userId){
        var user = userService.getUserById(UUID.fromString(userId));

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers(){
        var users = userService.listUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserByid(@PathVariable("userId") String userId,
                                               @RequestBody UpdateUserDto updateUserDto){
        userService.updateUserById(UUID.fromString(userId), updateUserDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable ("userId") String userId){
        userService.deleteUserById(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

}
