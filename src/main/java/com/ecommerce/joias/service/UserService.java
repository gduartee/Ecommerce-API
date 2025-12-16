package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.CreateUserDto;
import com.ecommerce.joias.entity.User;
import com.ecommerce.joias.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UUID createUser(CreateUserDto createUserDto){
        // DTO -> ENTITY
        User userEntity = new User();
        userEntity.setName(createUserDto.name());
        userEntity.setEmail(createUserDto.email());
        userEntity.setPhoneNumber(createUserDto.phoneNumber());
        userEntity.setCpf(createUserDto.cpf());
        userEntity.setPassword(createUserDto.password());

        var userSaved = userRepository.save(userEntity);

        return userSaved.getUserId();
    }
}
