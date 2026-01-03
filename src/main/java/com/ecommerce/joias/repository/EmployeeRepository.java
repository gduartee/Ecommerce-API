package com.ecommerce.joias.repository;

import com.ecommerce.joias.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByEmail(String email);

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    UserDetails findByEmail(String email);
}
