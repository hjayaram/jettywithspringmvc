package com.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.myapp.model.Person;

@Controller
public class AController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "hello";
	}
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String loadDetailsForm(ModelMap model){
		Person person = new Person();
		person.setAddress("blah blah blah");
		person.setAge(33);
		person.setId(100);
		person.setName("Some name");
		model.addAttribute("pers",person );
		return "person";
	}   
}
