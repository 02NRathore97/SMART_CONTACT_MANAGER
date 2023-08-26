package com.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	//Testing 
//	@Autowired
//	private UserRepository userRepository;
//	
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test() {
//		
//		User user = new User();
//		user.setName("Neeraj Rathore");
//		user.setEmail("rathoreneeraj448@gmail.com");
//		
//		userRepository.save(user);
//		return "controller is working";
//	}
	
	
	//Home page Handler
	@RequestMapping({"/","/home"})
	public String homeHandler(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	
	
	//About page Handler
	@RequestMapping("/about")
	public String aboutHandler(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	
	//Signup page Handler
	@RequestMapping("/signup")
	public String signupHandler(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	//Handler for registering user
		@RequestMapping(value="/do_register", method = RequestMethod.POST)
		public String do_registerHandler(@Valid @ModelAttribute("user") User user,
												BindingResult bindingResult,
										@RequestParam(value="agreement",defaultValue = "false") boolean agreement, 
										Model model,
										HttpSession session) {
			try {
				
				if(!agreement) {
					System.out.println(" You have not agreed terms & conditions");
					throw new Exception(" You have not agreed terms & conditions");
				}
				
				
				if(bindingResult.hasErrors()) {
					model.addAttribute("user", user);
					System.out.println("input validations" + bindingResult.toString());
					return "signup";
				}
				
				//setting values manually
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				
				
				//set password after encode 
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				
				
				//print values on console
				System.out.println("Agreement " + agreement);
				System.out.println("User " + user);
				
				
				//save data to database
				User result = this.userRepository.save(user);
				
				
				//print values on console
				System.out.println("result "+ result);
				System.out.println("user saved successfully");
				
				
				//it will return all the data to the fields
				model.addAttribute("user",new User());
				
				
				//provide success message
				session.setAttribute("message", new Message("SuccessFully Registered !!!! " , "alert-success"));
				
				//return to signup page
				return "signup";
				
			} catch (Exception e) {
				e.printStackTrace();
				//it will return all the data to the fields
				model.addAttribute("user",user);
				
				//provide error message
				session.setAttribute("message", new Message("Something went wrong " + e.getMessage() +"!!!!!", "alert-danger"));
				
				//return to signup page
				return "signup";
			}
		}
		
		
		//Handler for custom login
		@GetMapping("/signin")
		public String customLoginHandler(Model model) {
			model.addAttribute("title", "Login - Smart Contact Manager");
			return "login";
		}
	
	
}
