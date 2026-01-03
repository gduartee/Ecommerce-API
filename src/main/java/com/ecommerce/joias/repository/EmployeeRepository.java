package com.ecommerce.joias.repository;

import com.ecommerce.joias.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByEmail(String email);

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Employee findByEmail(String email);
}
