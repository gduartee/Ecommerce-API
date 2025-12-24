package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateAddressDto;
import com.ecommerce.joias.dto.response.AddressResponseDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.update.UpdateAddressDto;
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

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable("addressId") Integer addressId){
        var addressDto = addressService.getAddressById(addressId);

        return ResponseEntity.ok(addressDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AddressResponseDto>> listAddresses(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ){
        var addressesDto = addressService.listAddresses(page, limit);

        return ResponseEntity.ok(addressesDto);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<Void> updateAddressById(@PathVariable("addressId") Integer addressId, @RequestBody UpdateAddressDto updateAddressDto){
        addressService.updateAddressById(addressId, updateAddressDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddressById(@PathVariable("addressId") Integer addressId){
        addressService.deleteAddressById(addressId);

        return ResponseEntity.noContent().build();
    }
}
