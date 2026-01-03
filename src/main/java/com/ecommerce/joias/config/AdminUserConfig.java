package com.ecommerce.joias.config;

import com.ecommerce.joias.entity.Employee;
import com.ecommerce.joias.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserConfig(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception{
        var adminRole = "MANAGER";

        var userAdmin = employeeRepository.findByEmail("admin@email.com.br");

        if(userAdmin == null){
            Employee admin = new Employee();
            admin.setName("Admin Inicial");
            admin.setEmail("admin@email.com.br");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(adminRole);

            employeeRepository.save(admin);
            System.out.println("ADMIN INICIAL CRIADO COM SUCESSO!");
        }
    }
}
