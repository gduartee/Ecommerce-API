package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateAddressDto;
import com.ecommerce.joias.dto.response.AddressResponseDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.update.UpdateAddressDto;
import com.ecommerce.joias.entity.Address;
import com.ecommerce.joias.repository.AddressRepository;
import com.ecommerce.joias.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public AddressResponseDto createAddress(UUID userId, CreateAddressDto createAddressDto) {
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

    public AddressResponseDto getAddressById(Integer addressId) {
        var addressEntity = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        return new AddressResponseDto(
                addressEntity.getAddressId(),
                addressEntity.getCep(),
                addressEntity.getStreet(),
                addressEntity.getNum()
        );
    }

    public ApiResponse<AddressResponseDto> listAddresses(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = addressRepository.findAll(pageable);

        var addressesDto = pageData.getContent().stream().map(address -> new AddressResponseDto(
                address.getAddressId(),
                address.getCep(),
                address.getStreet(),
                address.getNum()
        )).toList();

        return new ApiResponse<>(
                addressesDto,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void updateAddressById(Integer addressId, UpdateAddressDto updateAddressDto) {
        var addressEntity = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        if (updateAddressDto.cep() != null)
            addressEntity.setCep(updateAddressDto.cep());

        if (updateAddressDto.street() != null)
            addressEntity.setStreet(updateAddressDto.street());

        if (updateAddressDto.num() != null)
            addressEntity.setNum(updateAddressDto.num());

        addressRepository.save(addressEntity);
    }

    public void deleteAddressById(Integer addressId) {
        addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        addressRepository.deleteById(addressId);
    }
}
