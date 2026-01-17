package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateUserDto;
import com.ecommerce.joias.dto.response.AddressResponseDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.UserResponseDto;
import com.ecommerce.joias.dto.update.UpdateUserDto;
import com.ecommerce.joias.entity.User;
import com.ecommerce.joias.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID createUser(CreateUserDto createUserDto) {

        if (userRepository.existsByEmail(createUserDto.email()))
            throw new RuntimeException("Este e-mail já está cadastrado.");

        if (userRepository.existsByPhoneNumber(createUserDto.phoneNumber()))
            throw new RuntimeException("Este telefone já está cadastrado.");

        if (userRepository.existsByCpf(createUserDto.cpf()))
            throw new RuntimeException("Este CPF já está cadastrado.");

        // DTO -> ENTITY
        User userEntity = new User();
        userEntity.setName(createUserDto.name());
        userEntity.setEmail(createUserDto.email());
        userEntity.setPhoneNumber(createUserDto.phoneNumber());
        userEntity.setCpf(createUserDto.cpf());
        userEntity.setPassword(passwordEncoder.encode(createUserDto.password()));

        var userSaved = userRepository.save(userEntity);

        return userSaved.getUserId();
    }

    public UserResponseDto getUserById(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(("Usuário não encontrado")));

        return new UserResponseDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCpf(),
                user.getPassword(),
                user.getAddresses().stream().map(
                        address -> new AddressResponseDto(
                                address.getAddressId(),
                                address.getCep(),
                                address.getStreet(),
                                address.getNum())
                ).toList()
        );
    }

    public ApiResponse<UserResponseDto> listUsers(Integer page, Integer limit, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> pageData;

        if(name != null && !name.isBlank())
            pageData = userRepository.findByNameContainingIgnoreCase(name, pageable);
        else
            pageData = userRepository.findAll(pageable);

        var usersDto = pageData.getContent().stream().map(user -> new UserResponseDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCpf(),
                user.getPassword(),
                user.getAddresses().stream().map(address -> new AddressResponseDto(
                        address.getAddressId(),
                        address.getCep(),
                        address.getStreet(),
                        address.getNum()
                )).toList()
        )).toList();

        return new ApiResponse<>(
                usersDto,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void deleteUserById(UUID userId) {
        var userExists = userRepository.existsById(userId);

        if (!userExists)
            throw new RuntimeException("Usuário não encontrado para exclusão");

        userRepository.deleteById(userId);
    }

    public void updateUserById(UUID userId, UpdateUserDto updateUserDto) {
        var userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!userEntity.getEmail().equals(updateUserDto.email()) && userRepository.existsByEmail(updateUserDto.email()))
            throw new RuntimeException("Este e-mail já está em uso por outro usuário.");

        if (!userEntity.getPhoneNumber().equals(updateUserDto.phoneNumber()) && userRepository.existsByPhoneNumber(updateUserDto.phoneNumber()))
            throw new RuntimeException("Este número de telefone já está em uso por outro usuário.");

        if (updateUserDto.name() != null)
            userEntity.setName(updateUserDto.name());

        if (updateUserDto.email() != null)
            userEntity.setEmail(updateUserDto.email());

        if (updateUserDto.phoneNumber() != null)
            userEntity.setPhoneNumber(updateUserDto.phoneNumber());

        userRepository.save(userEntity);
    }
}
