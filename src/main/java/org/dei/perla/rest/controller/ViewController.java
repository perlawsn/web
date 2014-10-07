package org.dei.perla.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/console")
public class ViewController {

	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	@RequestMapping("/query/{id}")
	public String queryOutput(@PathVariable("id") int id, Model model) {
		model.addAttribute("id", id);
		return "output";
	}
	
	@RequestMapping("/query")
	public String listQuery() {
		return "query";
	}
	
	@RequestMapping("/fpc")
	public String listFpc() {
		return "fpc";
	}

}
