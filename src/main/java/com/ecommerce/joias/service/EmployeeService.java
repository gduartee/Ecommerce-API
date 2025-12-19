package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateEmployeeDto;
import com.ecommerce.joias.dto.update.UpdateEmployeeDto;
import com.ecommerce.joias.entity.Employee;
import com.ecommerce.joias.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder){
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Integer createEmployee(CreateEmployeeDto createEmployeeDto){

        if(employeeRepository.existsByEmail(createEmployeeDto.email()))
            throw new RuntimeException("E-mail já está em uso por outro funcionário");

        // DTO -> ENTITY
        Employee employeeEntity = new Employee();
        employeeEntity.setName(createEmployeeDto.name());
        employeeEntity.setEmail(createEmployeeDto.email());
        employeeEntity.setPassword(passwordEncoder.encode(createEmployeeDto.password()));
        employeeEntity.setRole(createEmployeeDto.role());

        var employeeSaved = employeeRepository.save(employeeEntity);

        return employeeSaved.getEmployeeId();
    }

    public Employee getEmployeeById(Integer employeeId){
        return employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public List<Employee> listEmployees(){
        return employeeRepository.findAll();
    }

    public void updateEmployeeById(Integer employeeId, UpdateEmployeeDto updateEmployeeDto){
        var employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if(!employeeEntity.getEmail().equals(updateEmployeeDto.email()) && employeeRepository.existsByEmail(updateEmployeeDto.email()))
            throw new RuntimeException("Este e-mail já está em uso por outro funcionário.");

        if(updateEmployeeDto.name() != null)
           employeeEntity.setName(updateEmployeeDto.name());

        if(updateEmployeeDto.email() != null)
            employeeEntity.setEmail(updateEmployeeDto.email());

        if(updateEmployeeDto.role() != null)
            employeeEntity.setRole(updateEmployeeDto.role());

        employeeRepository.save(employeeEntity);
    }

    public void deleteEmployeeById(Integer employeeId){
        employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        employeeRepository.deleteById(employeeId);
    }

}
