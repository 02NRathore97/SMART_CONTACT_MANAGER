
package com.smartcontactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.service.EmailService;

@Controller
public class ForgetController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping("/forget")
	public String openEmailForm() {
		return "forgetemailform";
	}

	@PostMapping("/sendOTP")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("EMAIL " + email);

		// generate OTP of 4 digit
		Random random = new Random();
		int otp = random.nextInt(9999);

		session.setAttribute("myotp", otp);

		System.out.println("OTP " + otp);

		String subject = "OTP From SmartContactManager";
		String message = "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>"
				+ "Smart Contact Manager OTP is " + "<b>" + otp + "</n>" + "</h1>" + "</div>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);
		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);

			return "verifyOTP";

		} else {
			session.setAttribute("message", "check your email id !!");
			return "forgetemailform";

		}

	}

	// verifyOTP
	@PostMapping("/verifyOTP")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {

		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myotp == otp) {
			// password change form
			User user = this.userRepository.getUserByUserName(email);
			if (user == null) {
				// send error
				session.setAttribute("message", "User does not exist with email.....");
				return "forgetemailform";
			}
			return "changepasswordform";
		} else {
			session.setAttribute("message", "you have intered wronge OTP......");
			return "verifyOTP";
		}
	}

	// change password
	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully....";
	}
}
