package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addcommanData(Model model, Principal principal) {
		String name = principal.getName();
		User user = userRepository.findByEmail(name);
		System.out.println(user);
		model.addAttribute("user", user);

	}

	@GetMapping("/index")
	public String UserHome(Model model, Principal principal,HttpSession session) {
		model.addAttribute("title", "Dashboard");
		String name = principal.getName();
		User user = userRepository.findByEmail(name);
		// System.out.println(user);
		model.addAttribute("user", user);
		return "user/user_dashboard";
	}

	@GetMapping("/addcontact")
	public String openAddContact(Model model) {
		model.addAttribute("title", "Add-Contact");
		model.addAttribute("contact", new Contact());
		return "user/addcontactform";
	}

	@PostMapping("/processcontact")
	public String proccesscontact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session) {

		try {
			model.addAttribute("title", "Add Contact");
			System.out.println("**********************************");

			User user = (User) model.getAttribute("user");

			contact.setUser(user);

			// processing and uploading file

			if (file.isEmpty()) {
				contact.setImageUrl("Defaultcontact.png");

			} else {
				contact.setImageUrl(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			user.getContacts().add(contact);

			User mre = userRepository.save(user);

			if (mre != null) {

				session.setAttribute("message", new Message(" Contact Added Successfully !!", "alert-success"));
			} else {
				session.setAttribute("message", new Message("Phone No or Mail id  already Exist !!", "alert-danger"));
			}

			if (bindingResult.hasErrors()) {

				return "user/addcontactform";
			}

			model.addAttribute("contact", contact);

			System.out.println("MRE" + mre);

			System.out.println(user);

			System.out.println("Contaxt User :-> " + contact.getUser());

		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
		}

		return "user/addcontactform";
	}

	@GetMapping("/viewcontact/{page}")
	public String ShowContact(@PathVariable("page") Integer page, Model model) {

		model.addAttribute("title", "View Contacts");

		User user = (User) model.getAttribute("user");

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepository.findByUser(user, pageable);

		model.addAttribute("list", contacts);
		model.addAttribute("currentpage", page);
		model.addAttribute("totalpage", contacts.getTotalPages());

		return "user/viewContact";

	}

	@GetMapping("/{cId}/contact")
	public String ShowingParticularContactDetailes(@PathVariable("cId") Integer cId, Model model) {

		Optional<Contact> optional = contactRepository.findById(cId);
		Contact contact = optional.get();

		User user = (User) model.getAttribute("user");
		model.addAttribute("title", "error");

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		}

		return "user/contactdetails";
	}

	@GetMapping("/delete/{cId}")
	public String deletecontact(@PathVariable("cId") Integer cId, Model model, HttpSession session) {

		Optional<Contact> optional = contactRepository.findById(cId);
		Contact contact = optional.get();

		User user = (User) model.getAttribute("user");
		model.addAttribute("title", "error");

		System.out.println(cId);

		if (user.getId() == contact.getUser().getId()) {

			contact.setUser(null);
			
			user.getContacts().remove(contact);
			
			userRepository.save(user);

			/* contactRepository.delete(contact); */
			
			model.addAttribute("title", "Deleted");
			session.setAttribute("message", new Message(" Contact Deleted Successfully !!", "alert-success"));
		} else {
			session.setAttribute("message", new Message("server Problem !!", "alert-danger"));
		}

		return "redirect:/user/viewcontact/0";
	}

	@PostMapping("/updatecontact/{cId}")
	public String updateform(@PathVariable("cId") Integer cId, Model model) {

		Optional<Contact> optional = contactRepository.findById(cId);
		Contact contact = optional.get();
		
		model.addAttribute("title", "updatecontact");
		
		User user=(User) model.getAttribute("user");
         
		if(user == contact.getUser()) {
		model.addAttribute("contact", contact);}

		return "user/updateform";
	}

	@PostMapping("/proccessupdate")
	public String updatehandler(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model,
			HttpSession session, @RequestParam("profileImage") MultipartFile file) {

		try {
			
			Optional<Contact> optional = contactRepository.findById(contact.getcId());
			Contact oldContact = optional.get();
			
			if (!file.isEmpty()) {
				
				
				//delete file
				File oldfile = new ClassPathResource("static/image").getFile();
				File file1=new File(oldfile,oldContact.getImageUrl());
				file1.delete();
				
				
				
				//update file
				File savefile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						
				contact.setImageUrl(file.getOriginalFilename());
				
			} 
			else {
				
				contact.setImageUrl(oldContact.getImageUrl());
			
			}
			
			/*
			 * if(bindingResult.hasErrors()) {
			 * 
			 * return "user/updatecontact/"+contact.getcId(); }
			 */
			
		User user = (User) model.getAttribute("user");
		
		contact.setUser(user);
		
		   
			
			contactRepository.save(contact);
			session.setAttribute("message", new Message(" Contact Update Successfully !!", "alert-success"));
			

		} catch (Exception e) {
			// TODO: handle exception
			//return "user/updatecontact/"+contact.getcId();
		}

		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//profile
	
	@GetMapping("/profile")
	public String profile(Model model) {
		
		model.addAttribute("title", "Profile page");
		return "user/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String opensetting(Model model) {
		model.addAttribute("title", "settings");
		return "user/settings";
	}
	
	//change password
	@PostMapping("/changepassword")
	public String ChangePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Model model,HttpSession session) {
		
		User user=(User) model.getAttribute("user");
		
		if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			session.setAttribute("message", new Message(" Your Password is Successfully Change !!", "alert-success"));
			
		}
		else {
			session.setAttribute("message", new Message(" old Password is Invalid !!", "alert-danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}
