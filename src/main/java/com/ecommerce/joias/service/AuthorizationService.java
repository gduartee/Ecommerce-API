package com.ecommerce.joias.service;

import com.ecommerce.joias.repository.EmployeeRepository;
import com.ecommerce.joias.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // Primeiro tenta buscar nos funcionários
        UserDetails employee = employeeRepository.findByEmail(username);
        if(employee != null)
            return employee;

        UserDetails user = userRepository.findByEmail(username);
        if(user != null)
            return user;

        throw new UsernameNotFoundException("E-mail ou senha inválidos");

    }
}
