package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.authentication.AuthenticationDto;
import com.ecommerce.joias.entity.Employee;
import com.ecommerce.joias.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/employee/login")
    public ResponseEntity employeeLogin(@RequestBody @Valid AuthenticationDto authenticationDto){
       try{
           var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password());

           var auth = authenticationManager.authenticate(usernamePassword);

           var token = tokenService.generateToken((Employee) auth.getPrincipal());

           return ResponseEntity.ok(token);
       } catch (org.springframework.security.authentication.BadCredentialsException e){
           return ResponseEntity.status(401).body("E-mail ou senha inválidos");
       } catch (Exception e) {
           return ResponseEntity.status(500).body("Erro ao realizar login: " + e.getMessage());
       }
    }
}
