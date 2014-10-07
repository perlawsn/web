package org.dei.perla.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	@RequestMapping("/console/query/{id}")
	public String output(@PathVariable("id") int id, Model model) {
		model.addAttribute("id", id);
		return "output";
	}

}
