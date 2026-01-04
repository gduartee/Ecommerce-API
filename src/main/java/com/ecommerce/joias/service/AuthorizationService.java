package com.ecommerce.joias.service;

import com.ecommerce.joias.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        var employee = employeeRepository.findByEmail(email);

        if(employee == null)
            throw new UsernameNotFoundException("E-mail ou senha inválidos");

        return employee;
    }
}
