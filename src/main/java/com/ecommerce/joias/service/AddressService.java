package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateAddressDto;
import com.ecommerce.joias.dto.response.AddressResponseDto;
import com.ecommerce.joias.entity.Address;
import com.ecommerce.joias.repository.AddressRepository;
import com.ecommerce.joias.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public AddressResponseDto createAddress(UUID userId, CreateAddressDto createAddressDto){
       var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // DTO -> ENTITY
        var addressEntity = new Address();
        addressEntity.setUser(user);
        addressEntity.setCep(createAddressDto.cep());
        addressEntity.setStreet(createAddressDto.street());
        addressEntity.setNum(createAddressDto.num());

        var addressSaved = addressRepository.save(addressEntity);

        return new AddressResponseDto(
                addressSaved.getAddressId(),
                addressSaved.getCep(),
                addressSaved.getStreet(),
                addressSaved.getNum()
        );
    }
}
