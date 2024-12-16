package com.springboot_integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

import com.springboot_integration.model.Employee;

@MessagingGateway
public interface EmployeeGateway {

	//#########################  SERVICE ACTIVATOR  ########################//
	
	//	Get call
	@Gateway(requestChannel = "request-emp-name-channel")
	public String getEmployeeName(String name);
	
	//	Post call
	@Gateway(requestChannel = "request-hire-emp-channel")
	public Message<Employee> hireEmployee(Employee employee);
	
	
	//###########transformer##################
	@Gateway(requestChannel = "emp-status-channel")
	public String processEmployeeStatus(String status);

	//########################splitter###############

	@Gateway(requestChannel = "emp-managers-channel")
	public String getManagerList(String managers);
	

	//#########################  FILTER  ########################//
	
	@Gateway(requestChannel = "dev-emp-channel")
	public String getEmployeeIfADeveloper(String empDesignation);

	//######################  ROUTER  ######################//
			@Gateway(requestChannel = "emp-dept-channel")
			public String getEmployeeDepartment(Employee employee);


}

