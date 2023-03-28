package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.Email;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	Random random = new Random(100000);

	@GetMapping("/forgot")
	public String forgotpasswordform(Model model) {
		model.addAttribute("title", "Forgot Password");
		return "forgrtform";
	}

	@PostMapping("/sendotp")
	public String verifyotp(@RequestParam("email") String email, Model model, HttpSession session) {

		User user = userRepository.findByEmail(email);
		if (user != null) {

			model.addAttribute("title", "OTP send   ");

			int otp = random.nextInt(999999);

			Email em = new Email();
			em.setTo(email);
			em.setSubject("Email Verification OTP -Smart Contact Manager ");
			em.setMessage("Verify your email with the following One Time Password (OTP) - " + otp
					+ " and do not share this OTP with anyone.");

			emailService.sendEmail(em);

			session.setAttribute("sendotp", otp);
			session.setAttribute("email", email);

			return "verifyotp";

		} else {
			session.setAttribute("message",
					new Message(
							"Your email not present in our System please check again Otherwise Create new Account !!",
							"alert-danger"));
			return "/forgot";
		}
	}

	@PostMapping("/verifyotp")
	public String VerifyOtp(@RequestParam("verifyotp") String verifyotp, HttpSession session) {

		int userotp = Integer.parseInt(verifyotp);
		int sendotp = (int) session.getAttribute("sendotp");

		if (userotp == sendotp) {
			return "changepassword";
		}

		session.setAttribute("message", new Message("You Have Entered Wrong OTP please check  !!", "alert-danger"));

		return "verifyotp";
	}

	@PostMapping("/changepassword")
	public String ChangePassword(@RequestParam("password") String password,
			@RequestParam("checkpassword") String checkpassword, HttpSession session) {

		if (password.equals(checkpassword)) {

			String email = (String) session.getAttribute("email");
			System.out.println(email);
			User user = userRepository.findByEmail(email);

			if (bCryptPasswordEncoder.matches(password, user.getPassword())) {

				session.setAttribute("message",
						new Message(" New Password Cannot be same as Current Password!!", "alert-danger"));
				return "changepassword";
			} else {
				user.setPassword(bCryptPasswordEncoder.encode(password));
				userRepository.save(user);
				session.setAttribute("message",
						new Message("Password Change Sucessfully ...Now you can Signin!!", "alert-success"));
				return "redirect:/signin";
			}

		} else {

			session.setAttribute("message", new Message("Password Not Match Re-entered again  !!", "alert-danger"));

			return "changepassword";
		}

	}

}
