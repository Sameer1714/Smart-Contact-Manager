package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.*;
import javax.validation.Valid;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");

		return "index";
	}

	@RequestMapping("/signup")
	public String SignUp(Model model,HttpSession session) {
		model.addAttribute("title", "Register-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/register")
	public String RegisterUser(@Valid @ModelAttribute("user") User user, BindingResult re,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("default.png");

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = userRepository.save(user);

			if (re.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}

			System.out.println(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

			if (result == null) {
				session.setAttribute("message", new Message("Somthing went wrong - ", "alert-danger"));
			}

			// model.addAttribute("session", session);

			return "signup";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// model.addAttribute("user", user);
			// session.setAttribute("message", new Message("Somthing went wrong - " +
			// e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	@GetMapping("/signin")
	public String Login(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
	@GetMapping("/about")
	public String About(Model model) {
		model.addAttribute("title", "About Page");
		return "about";
	}

}
