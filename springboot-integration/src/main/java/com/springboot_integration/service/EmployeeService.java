package com.springboot_integration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.springboot_integration.model.Employee;
@Service
public class EmployeeService {
	//#########################  SERVICE ACTIVATOR  ########################//
	
		//Get call
		@ServiceActivator(inputChannel = "request-emp-name-channel")
		public void getEmployeeName(Message<String> name) {
			MessageChannel replyChannel = (MessageChannel) name.getHeaders().getReplyChannel();
			replyChannel.send(name);
		}
		
		//Post call
		@ServiceActivator(inputChannel = "request-hire-emp-channel", outputChannel = "process-emp-channel")
		public Message<Employee> hireEmployee(Message<Employee> employee){
			return employee;
		}
	
		// creating the chain of channels
		
		@ServiceActivator(inputChannel = "process-emp-channel", outputChannel = "get-emp-status-channel")
		public Message<Employee> processEmployee(Message<Employee> employee){
			employee.getPayload().setEmployeeStatus("Permanent Role");
			return employee;
		}
		
		@ServiceActivator(inputChannel = "get-emp-status-channel")
		public void getEmployeeStatus(Message<Employee> employee){
			MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
			replyChannel.send(employee);
		}

//##############################Transformer############################
		@Transformer(inputChannel="emp-status-channel", outputChannel="output-channel")
		public Message<String> convertToUppercase(Message<String> message) {
			
			String payload = message.getPayload();
			Message<String> messageInUppercase = MessageBuilder.withPayload(payload.toUpperCase())
					.copyHeaders(message.getHeaders())
					.build();
			return messageInUppercase;
		}
		
		@Splitter(inputChannel = "emp-managers-channel", outputChannel = "managers-channel")
		List<Message<String>> splitMessage(Message<?> message) {
			List<Message<String>> messages = new ArrayList<Message<String>>();
			String[] msgSplits = message.getPayload().toString().split(",");
			
			for(String split : msgSplits) {
				Message<String> msg = MessageBuilder.withPayload(split)
						.copyHeaders(message.getHeaders())
						.build();
				messages.add(msg);
			}
			
			return messages;
		}
		//######################Common output channel########################
		@ServiceActivator(inputChannel = "output-channel")
		public void consumeStringMessage(Message<String> message){
			System.out.println("Received message from output channel : " + message.getPayload());
			MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
			replyChannel.send(message);
		}
		//######################  AGGREGATOR  ######################//
				@Aggregator(inputChannel = "managers-channel", outputChannel = "output-channel")
				Message<String> getAllManagers(List<Message<String>> messages) {
				    StringJoiner joiner = new StringJoiner(" & ", "[", "]");
				    for (Message<String> message : messages) {
				        joiner.add(message.getPayload());
				    }
				    String managers = joiner.toString();
				    System.out.println("Managers: " + managers);
				    Message<String> updatedMsg = MessageBuilder.withPayload(managers)
				                                                .build();
				    return updatedMsg;
				}
				
				//#########################  FILTER  ########################//
				
				@Filter(inputChannel = "dev-emp-channel", outputChannel = "output-channel")
				boolean filter(Message<?> message) {
					String msg = message.getPayload().toString();
					return msg.contains("Dev");
				}
				
				//######################  ROUTER  ######################//
				@Router(inputChannel = "emp-dept-channel")
				public String getEmployeeDepartment(Message<Employee> message) {
				    String deptRoute = null;

				    switch (message.getPayload().getEmployeeDepartment()) {
				        case "SALES":
				            deptRoute = "sales-channel";
				            break;
				        case "MARKETING":
				            deptRoute = "marketing-channel";
				            break;
				    }

				    return deptRoute;
				}


				// creating two routing channels for router to route the message:
				@ServiceActivator(inputChannel = "sales-channel")
				public void getSalesDept(Message<Employee> employee) {
				    Message<String> sales = MessageBuilder.withPayload("SALES DEPARTMENT").build();
				    System.out.println("Received message from : " + sales.getPayload());
				    MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
				    replyChannel.send(sales);
				}

				@ServiceActivator(inputChannel = "marketing-channel")
				public void getMarketingDept(Message<Employee> employee) {
				    Message<String> marketing = MessageBuilder.withPayload("MARKETING DEPARTMENT").build();
				    System.out.println("Received message from : " + marketing.getPayload());
				    MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
				    replyChannel.send(marketing);
				}



		
}
