package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateEmployeeDto;
import com.ecommerce.joias.dto.update.UpdateEmployeeDto;
import com.ecommerce.joias.entity.Employee;
import com.ecommerce.joias.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Integer> createEmployee(@RequestBody @Valid CreateEmployeeDto createEmployeeDto){
        Integer employeeId = employeeService.createEmployee(createEmployeeDto);

        return ResponseEntity.created(URI.create("/employee/" + employeeId.toString())).body(employeeId);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("employeeId") Integer employeeId){
        var employee = employeeService.getEmployeeById(employeeId);

        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> listEmployees(){
        var employees = employeeService.listEmployees();

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Void> updateEmployeeById(@PathVariable("employeeId") Integer employeeId, @RequestBody UpdateEmployeeDto updateEmployeeDto){
        employeeService.updateEmployeeById(employeeId, updateEmployeeDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable("employeeId") Integer employeeId){
        employeeService.deleteEmployeeById(employeeId);

        return ResponseEntity.noContent().build();
    }

}
