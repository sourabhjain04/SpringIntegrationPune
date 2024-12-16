package com.springboot_integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot_integration.gateway.EmployeeGateway;
import com.springboot_integration.model.Employee;

@RestController
@RequestMapping("/integrate")
public class EmployeeController {

	@Autowired
	public EmployeeGateway employeeGateway;
	
	
	//#########################  SERVICE ACTIVATOR  ########################//
	
	@GetMapping(value = "{name}")
	public String getEmployeeName(@PathVariable("name") String name) {
		return employeeGateway.getEmployeeName(name);
	}
	
	@PostMapping("/hireEmployee")
	public Employee hireEmployee(@RequestBody Employee employee) {
		Message<Employee> reply = employeeGateway.hireEmployee(employee);
		Employee empReponse = reply.getPayload();
		return empReponse;
	}
	
	//###################Transformer##################
	@GetMapping(value = "/processEmployeeStatus/{status}")
	public String processEmployeeStatus(@PathVariable("status") String status) {
		return employeeGateway.processEmployeeStatus(status);
	}
	
	//########################Splitter####################
	@GetMapping(value = "/getManagerList/{managers}")
	public String getManagerList(@PathVariable("managers") String managers) {
		return employeeGateway.getManagerList(managers);
	}
	//#########################  FILTER  ########################//
	
			@GetMapping(value = "/getEmployeeIfADeveloper/{empDesignation}")
			public String getEmployeeIfADeveloper(@PathVariable("empDesignation") String empDesignation) {
				return employeeGateway.getEmployeeIfADeveloper(empDesignation);
			}
			
			//#####################Router###########################
			
			@GetMapping(value = "/getEmployeeDepartment")
			public String getEmployeeDepartment(@RequestBody Employee employee) {
			    return employeeGateway.getEmployeeDepartment(employee);
			}

	


}

