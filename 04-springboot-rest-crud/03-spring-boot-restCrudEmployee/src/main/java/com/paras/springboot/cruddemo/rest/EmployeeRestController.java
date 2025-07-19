package com.paras.springboot.cruddemo.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paras.springboot.cruddemo.entity.Employee;
import com.paras.springboot.cruddemo.service.EmployeeService;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {
    private EmployeeService employeeService;
    private ObjectMapper objectMapper;



    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService,ObjectMapper theObjectMapper){
        employeeService=theEmployeeService;
        objectMapper=theObjectMapper;
    }

    @GetMapping("/employees")
    public List<Employee>findAll(){
        return employeeService.findAll();
    }

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId){
        Employee theEmployee=employeeService.findById(employeeId);
        if(theEmployee==null){
            throw new RuntimeException("Employee id not found "+theEmployee);
        }
        return theEmployee;
    }


    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee){
        // in case id is sent in JSON set it to 0
        theEmployee.setId(0);
        Employee savedEmployee=employeeService.save(theEmployee);
        return savedEmployee;
    }


    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee){
        Employee savedEmployee=employeeService.save(theEmployee);
        return savedEmployee;
    }

    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId,@RequestBody Map<String,Object>patchPayload){
        Employee tempEmployee=employeeService.findById(employeeId);
        if(tempEmployee==null){
            throw new RuntimeException("Employee id not found");
        }

        if(patchPayload.containsKey("id")){
            throw new RuntimeException("Employee id is not allowed in request body "+employeeId);
        }

        Employee updatedEmployee=apply(patchPayload,tempEmployee);

        Employee dbEmployee=employeeService.save(updatedEmployee);

        return dbEmployee;

    }

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId){
        Employee emp=employeeService.findById(employeeId);
        if(emp==null){
            throw new RuntimeException("Id doesnt exist");
        }
        employeeService.deleteById(employeeId);

        return "Deleted employee id of "+employeeId;
    }

    private Employee apply(Map<String, Object> patchPayload, Employee tempEmployee) {
        //convert employee object to JSON object node
        ObjectNode employeeNode=objectMapper.convertValue(tempEmployee,ObjectNode.class);
        //convert patch object to JSON object node
        ObjectNode patchNode=objectMapper.convertValue(patchPayload,ObjectNode.class);
        //merge the patch object into employee
        employeeNode.setAll(patchNode);

        return objectMapper.convertValue(employeeNode,Employee.class);
    }


}
