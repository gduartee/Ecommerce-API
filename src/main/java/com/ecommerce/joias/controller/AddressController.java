package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateAddressDto;
import com.ecommerce.joias.dto.response.AddressResponseDto;
import com.ecommerce.joias.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<AddressResponseDto> createAddress(@PathVariable("userId") UUID userId, @RequestBody @Valid CreateAddressDto createAddressDto){
        var addressCreated = addressService.createAddress(userId, createAddressDto);

        URI location = URI.create("/addresses/" + userId + "/" + addressCreated.addressId());

        return ResponseEntity.created(location).body(addressCreated);
    }
}
